package tech.alexnijjar.golemoverhaul.mixins.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tech.alexnijjar.golemoverhaul.common.entities.golems.KelpGolem;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin {

    @Inject(
        method = "applyEffects",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void golemoverhaul$applyEffects(Level level, BlockPos pos, List<BlockPos> positions, CallbackInfo ci, @Local AABB aabb, @Local(ordinal = 1) int distance) {
        if (pos == null) return;
        level.getEntitiesOfClass(KelpGolem.class, aabb).forEach(entity -> {
            if (pos.closerThan(entity.blockPosition(), distance) && entity.isInWaterOrRain()) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
            }
        });
    }
}
