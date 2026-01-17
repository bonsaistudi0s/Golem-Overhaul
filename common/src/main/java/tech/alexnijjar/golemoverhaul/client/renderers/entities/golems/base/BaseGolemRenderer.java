package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;

public class BaseGolemRenderer<T extends BaseGolem> extends GeoEntityRenderer<T> {

    public BaseGolemRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    public static <T extends Entity> ResourceLocation texture(RegistryEntry<EntityType<T>> golem) {
        String name = name(golem).getPath();
        return ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "%s/%s".formatted(name.replace("_golem", ""), name));
    }

    public static <T extends Entity> ResourceLocation name(RegistryEntry<EntityType<T>> golem) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(golem.get());
    }
}
