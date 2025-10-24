package tech.alexnijjar.golemoverhaul.common.registry.forge;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@SuppressWarnings("unused")
public class ModEntityTypesImpl {

    public static <T extends Entity> ModEntityTypes.PlatformEntityBuilder<T> createEntityBuilder(EntityType.EntityFactory<T> factory, MobCategory category) {
        return new ForgeEntityBuilder<>(EntityType.Builder.of(factory, category));
    }

    private record ForgeEntityBuilder<T extends Entity>(
            EntityType.Builder<T> builder) implements ModEntityTypes.PlatformEntityBuilder<T> {

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> sized(float width, float height) {
            builder.sized(width, height);
            return this;
        }

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> clientTrackingRange(int range) {
            builder.clientTrackingRange(range);
            return this;
        }

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> updateInterval(int interval) {
            builder.updateInterval(interval);
            return this;
        }

        @Override
        public ModEntityTypes.PlatformEntityBuilder<T> fireImmune() {
            builder.fireImmune();
            return this;
        }

        @Override
        public EntityType<T> build(String id) {
            return builder.build(id);
        }
    }
}
