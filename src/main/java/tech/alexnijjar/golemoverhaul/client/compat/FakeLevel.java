package tech.alexnijjar.golemoverhaul.client.compat;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Objects;

public class FakeLevel implements BlockAndTintGetter {

    private final Map<BlockPos, BlockState> blocks;

    public FakeLevel(Map<BlockPos, BlockState> blocks) {
        this.blocks = blocks;
    }

    public static final Vector3f SCENE_LIGHT_1 = new Vector3f(1, 0, 1);
    public static final Vector3f SCENE_LIGHT_2 = new Vector3f(-1, 1, -1);

    @Override
    public float getShade(Direction direction, boolean shade) {
        return 1;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return Objects.requireNonNull(Minecraft.getInstance().level)
            .getBlockTint(blockPos, colorResolver);
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int amount) {
        return 15;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return blocks.getOrDefault(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Blocks.AIR.defaultBlockState().getFluidState();
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }

    public void renderBlocks(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        RenderSystem.setupGui3DDiffuseLighting(SCENE_LIGHT_1, SCENE_LIGHT_2);

        blocks.forEach((pos, state) -> {
            RenderType renderType = ItemBlockRenderTypes.getRenderType(state, false);
            VertexConsumer consumer = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            dispatcher.renderBatched(
                state,
                pos,
                this,
                poseStack,
                consumer,
                true,
                Objects.requireNonNull(mc.level).random
            );
            poseStack.popPose();
        });
        bufferSource.endBatch();
        Lighting.setupFor3DItems();
    }
}
