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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.alexnijjar.golemoverhaul.common.entities.AdditionalBeeData;
import tech.alexnijjar.golemoverhaul.common.entities.goals.GoToHoneyGolemHiveGoal;
import tech.alexnijjar.golemoverhaul.common.entities.goals.LocateHoneyGolemHiveGoal;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HoneyGolem;

import java.util.UUID;

@Mixin(Bee.class)
public abstract class BeeMixin extends PathfinderMob implements AdditionalBeeData {

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
    }

    /**
     * Set the target to the honey golem's target.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void golemoverhaul$tick(CallbackInfo ci) {
        if (tickCount + getId() % 40 == 0 && getTarget() == null && level() instanceof ServerLevel level) {
            if (golemoverhaul$owner != null && level.getEntity(golemoverhaul$owner) instanceof HoneyGolem golem && golem.getTarget() != null) {
                setTarget(golem.getTarget());
            }
        }
    }

    /**
     * Allow bees from honey golems to sting without dying.
     */
    @Inject(method = "setHasStung", at = @At("HEAD"), cancellable = true)
    private void golemoverhaul$setHasStung(boolean hasStung, CallbackInfo ci) {
        if (hasStung && this.golemoverhaul$owner != null) {
            ci.cancel();
        }
    }

    /**
     * Prevent bees that have stung their target from losing aggro when the honey golem is their owner.
     */
    @WrapWithCondition(
        method = "doHurtTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee;stopBeingAngry()V"
        )
    )
    private boolean golemoverhaul$doHurtTarget(Bee instance) {
        return this.golemoverhaul$owner == null;
    }

    /**
     * Increase the damage of bees that belong to a honey golem.
     */
    @WrapOperation(
        method = "doHurtTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee;getAttributeValue(Lnet/minecraft/core/Holder;)D"
        )
    )
    private double golemoverhaul$doHurtTarget2(Bee instance, Holder<Attribute> holder, Operation<Double> original) {
        return original.call(instance, holder) * (this.golemoverhaul$owner != null ? 6 : 1);
    }

    @Override
    public @Nullable UUID golemoverhaul$getOwner() {
        return this.golemoverhaul$owner;
    }

    @Override
    public void golemoverhaul$setOwner(UUID owner) {
        this.golemoverhaul$owner = owner;
    }

    @Override
    public boolean golemoverhaul$hasGolemHive() {
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
                if (golem.getTarget() != null) {
                    cir.setReturnValue(false);
                    return;
                }

                if (level.isNight()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
