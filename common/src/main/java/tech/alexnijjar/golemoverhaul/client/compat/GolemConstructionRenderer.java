package tech.alexnijjar.golemoverhaul.client.compat;

import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GolemConstructionRenderer {

    public static final ResourceLocation TEXTURE = GolemOverhaul.asResource("textures/gui/container/golem_construction.png");

    private final FakeLevel fakeLevel;
    private final Entity entity;
    private final int x;
    private final int y;
    private final float blockScale;
    private final float entityScale;
    private final boolean single;
    private final MutableComponent blockTooltip = Component.empty();

    public GolemConstructionRenderer(GolemConstructionRecipe recipe, int x, int y) {
        this.x = x;
        this.y = y;
        this.blockScale = recipe.getBlockScale();
        this.entityScale = recipe.getEntityScale();

        Map<BlockPos, BlockState> blocks = new HashMap<>();
        this.fakeLevel = new FakeLevel(blocks);

        var firstPatternRow = recipe.getPattern().stream().findFirst();
        if (firstPatternRow.isEmpty()) throw new RuntimeException("Recipe contains empty pattern row");

        int width = firstPatternRow.get().length();
        int height = recipe.getPattern().size();
        single = width == 1 && height == 1;

        for (int i = 0; i < height; i++) {
            String row = recipe.getPattern().get(i);
            for (int j = 0; j < width; j++) {
                char c = row.charAt(j);
                ResourceKey<Block> key = recipe.getKey().get(String.valueOf(c));
                if (key == null) throw new IllegalStateException("Invalid key: " + c);
                if (!key.location().equals(BuiltInRegistries.BLOCK.getKey(Blocks.AIR))) {
                    BlockState state = BuiltInRegistries.BLOCK.getOrThrow(key).defaultBlockState();
                    blocks.put(BlockPos.containing(
                            width - j - 1,
                            height - i - 1,
                            0
                        ),
                        state);

                    this.blockTooltip.append(state.getBlock().getName());
                    if (i != height - 1) {
                        this.blockTooltip.append("\n");
                    }
                }
            }
        }

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(recipe.getEntity());
        if (type == null) throw new IllegalStateException("Invalid entity: " + recipe.getEntity());
        this.entity = type.create(Objects.requireNonNull(Minecraft.getInstance().level));
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        this.entity.setYHeadRot(0);
        graphics.blit(TEXTURE, this.x - 47, this.y - 58, 0, 0, 180, 76, 180, 76);

        // Render blocks
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(x, y, 100);
            pose.translate(10, -5, 0);
            pose.scale(-20, -20, -20);
            pose.scale(blockScale, blockScale, blockScale);

            if (single) {
                pose.translate(0.1, 0.2, 0);
            }

            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(Axis.XP.rotationDegrees(-30));
            pose.mulPose(Axis.YP.rotationDegrees(45));
            pose.translate(-0.5, -0.5, -0.5);
            this.fakeLevel.renderBlocks(graphics.pose());
        }

        // Render entity
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(95, -7, 0);
            Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ((float) Math.toRadians(30), (float) Math.toRadians(135), (float) Math.PI);
            InventoryScreen.renderEntityInInventory(graphics, this.x, this.y, (int)(32 * entityScale), ARMOR_STAND_ANGLE, null, (LivingEntity) this.entity);
        }

        // Handle tooltips
        var screen = Minecraft.getInstance().screen;
        if (screen == null) return;

        if (mouseX >= this.x + 68 && mouseX < this.x + 120 && mouseY >= this.y - 48 && mouseY < this.y + 8) {
            screen.setTooltipForNextRenderPass(Tooltip.create(this.entity.getName()), DefaultTooltipPositioner.INSTANCE, true);
        } else if (mouseX >= this.x - 34 && mouseX < this.x + 18 && mouseY >= this.y - 48 && mouseY < this.y + 8) {
            screen.setTooltipForNextRenderPass(Tooltip.create(blockTooltip), DefaultTooltipPositioner.INSTANCE, true);
        }
    }

    public int getWidth() {
        return 180;
    }

    public int getHeight() {
        return 76;
    }
}