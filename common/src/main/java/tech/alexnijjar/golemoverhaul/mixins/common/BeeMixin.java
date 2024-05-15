package tech.alexnijjar.golemoverhaul.mixins.common;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.alexnijjar.golemoverhaul.common.entities.AdditionalBeeData;
import tech.alexnijjar.golemoverhaul.common.entities.goals.GoToHoneyGolemHiveGoal;
import tech.alexnijjar.golemoverhaul.common.entities.goals.HealHoneyGolemGoal;
import tech.alexnijjar.golemoverhaul.common.entities.goals.LocateHoneyGolemHiveGoal;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HoneyGolem;

import java.util.UUID;

@Mixin(Bee.class)
public abstract class BeeMixin extends PathfinderMob implements AdditionalBeeData {

    @Shadow
    public abstract boolean hasNectar();

    @Unique
    @Nullable
    private UUID golemoverhaul$owner;

    protected BeeMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void golemoverhaul$registerGoals(CallbackInfo ci) {
        Bee bee = (Bee) (Object) this;
        this.goalSelector.addGoal(5, new LocateHoneyGolemHiveGoal(bee));
        this.goalSelector.addGoal(6, new GoToHoneyGolemHiveGoal(bee));
        this.goalSelector.addGoal(7, new HealHoneyGolemGoal(bee));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void golemoverhaul$tick(CallbackInfo ci) {
        if (tickCount % 40 == 0 && golemoverhaul$owner != null && level() instanceof ServerLevel level) {
            if (level.getEntity(golemoverhaul$owner) instanceof HoneyGolem golem && golem.getTarget() != null) {
                if (getTarget() == null) {
                    setTarget(golem.getTarget());
                }
            }
        }
    }

    @Inject(method = "setHasStung", at = @At("HEAD"), cancellable = true)
    private void golemoverhaul$setHasStung(boolean hasStung, CallbackInfo ci) {
        if (hasStung && this.golemoverhaul$owner != null) {
            ci.cancel();
        }
    }

    @WrapWithCondition(
        method = "doHurtTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee;stopBeingAngry()V"
        )
    )
    private boolean golemoverhaul$doHurtTarget2(Bee instance) {
        return this.golemoverhaul$owner == null;
    }

    @WrapOperation(
        method = "doHurtTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee;getAttributeValue(Lnet/minecraft/core/Holder;)D"
        )
    )
    private double golemoverhaul$doHurtTarget3(Bee instance, Holder<Attribute> holder, Operation<Double> original) {
        return original.call(instance, holder) * (this.golemoverhaul$owner != null ? 3 : 1);
    }

    @Override
    public UUID golemoverhaul$getOwner() {
        return this.golemoverhaul$owner;
    }

    @Override
    public void golemoverhaul$setOwner(UUID owner) {
        this.golemoverhaul$owner = owner;
    }

    @Override
    public boolean hasGolemHive() {
        return this.golemoverhaul$owner != null;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void golemoverhaul$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (this.golemoverhaul$owner != null) {
            compound.putUUID("HoneyGolemOwner", this.golemoverhaul$owner);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void golemoverhaul$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("HoneyGolemOwner")) {
            this.golemoverhaul$owner = compound.getUUID("HoneyGolemOwner");
        }
    }

    @Inject(method = "wantsToEnterHive", at = @At("HEAD"), cancellable = true)
    private void golemoverhaul$wantsToEnterHive(CallbackInfoReturnable<Boolean> cir) {
        if (this.golemoverhaul$owner != null && this.level() instanceof ServerLevel level) {
            if (level.getEntity(this.golemoverhaul$owner) instanceof HoneyGolem golem) {
                if (golem.getHealth() < golem.getMaxHealth()) {
                    cir.setReturnValue(false);
                    return;
                }

                if (golem.getTarget() != null) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            if (level.isNight()) {
                cir.setReturnValue(true);
                return;
            }

            if (this.hasNectar()) {
                cir.setReturnValue(true);
            }
        }
    }
}
