package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.TerracottaGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class TerracottaGolemRenderer extends BaseGolemRenderer<TerracottaGolem> {

    public static final ResourceLocation MODEL = new ResourceLocation(GolemOverhaul.MOD_ID, "geo/entity/terracotta/terracotta_golem.geo.json");
    public static final ResourceLocation CACTUS_MODEL = new ResourceLocation(GolemOverhaul.MOD_ID, "geo/entity/terracotta/cactus_terracotta_golem.geo.json");
    public static final ResourceLocation DEAD_BUSH_MODEL = new ResourceLocation(GolemOverhaul.MOD_ID, "geo/entity/terracotta/dead_bush_terracotta_golem.geo.json");

    public TerracottaGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.TERRACOTTA_GOLEM, true, 10) {
            @Override
            public ResourceLocation getModelResource(TerracottaGolem golem) {
                return switch (golem.getTerracottaType()) {
                    case NORMAL -> MODEL;
                    case CACTUS -> CACTUS_MODEL;
                    case DEAD_BUSH -> DEAD_BUSH_MODEL;
                };
            }
        });
    }
}
