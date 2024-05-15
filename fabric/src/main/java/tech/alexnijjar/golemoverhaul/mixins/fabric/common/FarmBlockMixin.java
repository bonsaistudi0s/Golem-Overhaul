package tech.alexnijjar.golemoverhaul.mixins.fabric.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void cadmus$fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (!level.isClientSide()) {
            AABB bounds = state.getCollisionShape(level, pos).bounds().move(pos).inflate(10);
            if (!level.getEntitiesOfClass(HayGolem.class, bounds).isEmpty()) {
                ci.cancel();
            }
        }
    }
}
