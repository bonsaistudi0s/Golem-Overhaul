package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;
import tech.alexnijjar.golemoverhaul.common.entities.golems.NetheriteGolem;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

    @Inject(method = "trySpawnGolem", at = @At("TAIL"))
    private void golemoverhaul$trySpawnGolem(Level level, BlockPos pos, CallbackInfo ci) {
        HayGolem.trySpawnGolem(level, pos);
        NetheriteGolem.trySpawnGolem(level, pos);
    }
}
