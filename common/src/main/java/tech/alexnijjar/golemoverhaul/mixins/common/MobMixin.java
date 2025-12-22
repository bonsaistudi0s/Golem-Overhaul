package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void golemoverhaul$setTarget(LivingEntity target, CallbackInfo ci) {
        var mob = (Mob) (Object) this;
        if (mob instanceof IronGolem || mob instanceof SnowGolem) {
            if (target instanceof BaseGolem) {
                ci.cancel();
            }
        }
    }
}
