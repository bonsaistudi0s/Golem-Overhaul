package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem {

    protected IronGolemMixin(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
        method = "doHurtTarget",
        at = @At("HEAD"),
        cancellable = true
    )
    public void golemoverhaul$doHurtTarget(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof BaseGolem golem && !golem.doesSwingAttack()) {
            cir.setReturnValue(super.doHurtTarget(target));
        }
    }
}