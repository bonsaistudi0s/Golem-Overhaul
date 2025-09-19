package tech.alexnijjar.golemoverhaul.mixins.common;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.BarrelGolem;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {

    private static final ResourceLocation IRON_GOLEM_STRUCTURE =
            ResourceLocation.withDefaultNamespace("village/common/iron_golem");

    @Shadow
    @Final
    protected Either<ResourceLocation, StructureTemplate> template;

    @Inject(
            method = "place(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;" +
                    "Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;" +
                    "Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;" +
                    "Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;" +
                    "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;" +
                    "Lnet/minecraft/util/RandomSource;" +
                    "Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;Z)Z",
            at = @At("RETURN")
    )
    private void golemoverhaul$place(StructureTemplateManager structureTemplateManager, WorldGenLevel worldGenLevel,
                                     StructureManager structureManager, ChunkGenerator generator, BlockPos offset,
                                     BlockPos pos, Rotation rotation, BoundingBox box, RandomSource random,
                                     LiquidSettings liquidSettings, boolean keepJigsaws,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (worldGenLevel.isClientSide()) return;

        this.template.left().ifPresent(templateLocation -> {
            if (!templateLocation.equals(IRON_GOLEM_STRUCTURE)) return;

            var difficulty = worldGenLevel.getCurrentDifficultyAt(offset);
            var serverLevel = worldGenLevel.getLevel();
            var spawnPos = new Vec3(offset.getX() + 0.5, offset.getY() + 1, offset.getZ() + 0.5);

            var barrelGolemEntityType = ModEntityTypes.BARREL_GOLEM.get();
            if (BarrelGolem.checkMobSpawnRules(barrelGolemEntityType, worldGenLevel, MobSpawnType.SPAWNER, offset,
                    worldGenLevel.getRandom())) { // MobSpawnType.SPAWNER bypasses lighting checks
                var barrelGolem = new BarrelGolem(barrelGolemEntityType, serverLevel);
                barrelGolem.setPos(spawnPos);
                barrelGolem.finalizeSpawn(serverLevel, difficulty, MobSpawnType.STRUCTURE, null);
                serverLevel.addFreshEntity(barrelGolem);
            }

            var hayGolemEntityType = ModEntityTypes.HAY_GOLEM.get();
            if (HayGolem.checkMobSpawnRules(hayGolemEntityType, worldGenLevel, MobSpawnType.SPAWNER, offset,
                    worldGenLevel.getRandom())) {
                var hayGolem = new HayGolem(hayGolemEntityType, serverLevel);
                hayGolem.setPos(spawnPos);
                hayGolem.finalizeSpawn(serverLevel, difficulty, MobSpawnType.STRUCTURE, null);
                serverLevel.addFreshEntity(hayGolem);
            }
        });
    }
}
