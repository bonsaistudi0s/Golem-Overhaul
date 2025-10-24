package tech.alexnijjar.golemoverhaul.common.registry.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@SuppressWarnings("unused")
public class ModEntityTypesImpl {

    public static <T extends Entity> ModEntityTypes.PlatformEntityBuilder<T> createEntityBuilder(EntityType.EntityFactory<T> factory, MobCategory category) {
        return new FabricEntityBuilder<>(FabricEntityTypeBuilder.create(category, factory));
    }

    private record FabricEntityBuilder<T extends Entity>(
            FabricEntityTypeBuilder<T> builder) implements ModEntityTypes.PlatformEntityBuilder<T> {

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> sized(float width, float height) {
            builder.dimensions(EntityDimensions.scalable(width, height));
            return this;
        }

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> clientTrackingRange(int range) {
            builder.trackRangeChunks(range);
            return this;
        }

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> updateInterval(int interval) {
            builder.trackedUpdateRate(interval);
            return this;
        }

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> fireImmune() {
            builder.fireImmune();
            return this;
        }

        @Override
        public EntityType<T> build(String id) {
            return builder.build();
        }
    }
}
