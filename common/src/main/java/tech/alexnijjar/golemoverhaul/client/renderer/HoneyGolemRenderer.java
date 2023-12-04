package tech.alexnijjar.golemoverhaul.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.common.entities.HoneyGolem;

public class HoneyGolemRenderer extends BaseGolemEntityRenderer<HoneyGolem> {

    public HoneyGolemRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem) {
        super(renderManager, new BaseGolemModel<>(
            BuiltInRegistries.ENTITY_TYPE.getKey(golem),
            true,
            new ResourceLocation(GolemOverhaul.MOD_ID, "honey/honey_golem"),
            HONEY_GOLEM_ANIMATION, 20));
    }
}
