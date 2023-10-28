package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;

@Mixin(PiglinSpecificSensor.class)
public abstract class PiglinSpecificSensorMixin {

    @Inject(
        method = "doTick",
        at = @At("TAIL")
    )
    private void golemoverhaul$doTick(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            .ifPresent(entities -> entities.findAll(e -> e instanceof NetheriteGolem g && !g.isGilded())
                .forEach(e -> entity.getBrain().setMemory(MemoryModuleType.ANGRY_AT, e.getUUID())));
    }
}
