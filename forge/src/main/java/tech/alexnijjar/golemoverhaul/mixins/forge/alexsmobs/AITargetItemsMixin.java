package tech.alexnijjar.golemoverhaul.mixins.forge.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;

@SuppressWarnings("rawtypes")
@Mixin(targets = "com.github.alexthe666.alexsmobs.entity.EntityCrow$AITargetItems")
public abstract class AITargetItemsMixin extends CreatureAITargetItems {

    public AITargetItemsMixin(PathfinderMob creature, boolean checkSight) {
        super(creature, checkSight);
    }

    @Inject(method = "canUse()Z", at = @At("RETURN"), cancellable = true)
    private void cancelIfNearHayGolem(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || this.targetEntity == null) {
            return;
        }

        var level = this.targetEntity.level();
        var searchBox = this.targetEntity.getBoundingBox().inflate(12.0, 6.0, 12.0);
        var nearbyGolems = level.getEntitiesOfClass(HayGolem.class, searchBox);

        if (!nearbyGolems.isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}
