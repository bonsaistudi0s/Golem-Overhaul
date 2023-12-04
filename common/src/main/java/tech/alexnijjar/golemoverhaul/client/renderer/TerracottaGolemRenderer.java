package tech.alexnijjar.golemoverhaul.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.common.entities.terracotta.TerracottaGolem;

public class TerracottaGolemRenderer<T extends TerracottaGolem> extends BaseGolemEntityRenderer<T> {

    public TerracottaGolemRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem) {
        super(renderManager, new BaseGolemModel<>(
            BuiltInRegistries.ENTITY_TYPE.getKey(golem),
            true,
            new ResourceLocation(GolemOverhaul.MOD_ID, "terracotta/terracotta_golem"),
            TERRACOTTA_GOLEM_ANIMATION, 10));
    }
}
