package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class HayGolemRenderer extends BaseGolemRenderer<HayGolem> {

    public static final ResourceLocation GREEN_TEXTURE_1 = GolemOverhaul.asResource("textures/entity/hay/green_hay_golem_1.png");
    public static final ResourceLocation GREEN_TEXTURE_2 = GolemOverhaul.asResource("textures/entity/hay/green_hay_golem_2.png");
    public static final ResourceLocation GREEN_TEXTURE_3 = GolemOverhaul.asResource("textures/entity/hay/green_hay_golem_3.png");

    public static final ResourceLocation RED_TEXTURE_1 = GolemOverhaul.asResource("textures/entity/hay/red_hay_golem_1.png");
    public static final ResourceLocation RED_TEXTURE_2 = GolemOverhaul.asResource("textures/entity/hay/red_hay_golem_2.png");
    public static final ResourceLocation RED_TEXTURE_3 = GolemOverhaul.asResource("textures/entity/hay/red_hay_golem_3.png");

    public static final ResourceLocation GREEN_MODEL = GolemOverhaul.asResource("geo/entity/hay/green_hay_golem.geo.json");
    public static final ResourceLocation SHEARED_GREEN_MODEL = GolemOverhaul.asResource("geo/entity/hay/green_hay_golem_sheared.geo.json");

    public static final ResourceLocation RED_MODEL = GolemOverhaul.asResource("geo/entity/hay/red_hay_golem.geo.json");
    public static final ResourceLocation SHEARED_RED_MODEL = GolemOverhaul.asResource("geo/entity/hay/red_hay_golem_sheared.geo.json");

    public HayGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.HAY_GOLEM, true, 90) {
            @Override
            public ResourceLocation getModelResource(HayGolem golem) {
                boolean sheared = golem.isSheared();
                return golem.getColor() == HayGolem.Color.GREEN ?
                    sheared ? SHEARED_GREEN_MODEL : GREEN_MODEL :
                    sheared ? SHEARED_RED_MODEL : RED_MODEL;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(HayGolem golem) {
        HayGolem.Color color = golem.getColor();
        return switch (golem.getCrackiness()) {
            case NONE, LOW -> color == HayGolem.Color.GREEN ? GREEN_TEXTURE_1 : RED_TEXTURE_1;
            case MEDIUM -> color == HayGolem.Color.GREEN ? GREEN_TEXTURE_2 : RED_TEXTURE_2;
            case HIGH -> color == HayGolem.Color.GREEN ? GREEN_TEXTURE_3 : RED_TEXTURE_3;
        };
    }
}
