package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.CandleGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CandleGolemRenderer extends BaseGolemRenderer<CandleGolem> {

    public static final ResourceLocation GLOW_1 = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/candle/candle_golem_1_glow.png");
    public static final ResourceLocation GLOW_2 = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/candle/candle_golem_2_glow.png");

    public static final ResourceLocation MODEL_1 = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "geo/entity/candle/candle_golem_1.geo.json");
    public static final ResourceLocation MODEL_2 = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "geo/entity/candle/candle_golem_2.geo.json");
    public static final ResourceLocation MODEL_3 = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "geo/entity/candle/candle_golem_3.geo.json");

    public CandleGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.CANDLE_GOLEM, true, 90) {
            @Override
            public ResourceLocation getModelResource(CandleGolem golem) {
                return switch (golem.getCrackiness()) {
                    case NONE, LOW -> MODEL_1;
                    case MEDIUM -> MODEL_2;
                    case HIGH -> MODEL_3;
                };
            }
        });

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected @Nullable RenderType getRenderType(CandleGolem golem, @Nullable MultiBufferSource bufferSource) {
                return switch (golem.getCrackiness()) {
                    case NONE, LOW -> RenderType.eyes(GLOW_1);
                    case MEDIUM -> RenderType.eyes(GLOW_2);
                    case HIGH -> super.getRenderType(golem, bufferSource);
                };
            }

            @Override
            public void render(PoseStack poseStack, CandleGolem golem, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if (golem.isLit()) {
                    super.render(poseStack, golem, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }
}
