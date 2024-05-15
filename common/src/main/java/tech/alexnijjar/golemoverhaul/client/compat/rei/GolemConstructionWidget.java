package tech.alexnijjar.golemoverhaul.client.compat.rei;

import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tech.alexnijjar.golemoverhaul.client.compat.FakeLevel;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GolemConstructionWidget extends Widget {

    private final FakeLevel fakeLevel;
    private final Entity entity;
    private final int x;
    private final int y;

    public GolemConstructionWidget(GolemConstructionRecipe recipe, int x, int y) {
        this.x = x;
        this.y = y;

        Map<BlockPos, BlockState> blocks = new HashMap<>();
        this.fakeLevel = new FakeLevel(blocks);

        int width = recipe.pattern().getFirst().length();
        int height = recipe.pattern().size();

        for (int i = 0; i < height; i++) {
            String row = recipe.pattern().get(i);
            for (int j = 0; j < width; j++) {
                char c = row.charAt(j);
                ResourceKey<Block> key = recipe.key().get(String.valueOf(c));
                if (key == null) throw new IllegalStateException("Invalid key: " + c);
                if (!key.location().equals(BuiltInRegistries.BLOCK.getKey(Blocks.AIR))) {
                    BlockState state = BuiltInRegistries.BLOCK.getOrThrow(key).defaultBlockState();
                    blocks.put(BlockPos.containing(
                            width - j - 1,
                            height - i - 1,
                            0
                        ),
                        state);
                }
            }
        }

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(recipe.entity());
        if (type == null) throw new IllegalStateException("Invalid entity: " + recipe.entity());
        this.entity = type.create(Objects.requireNonNull(Minecraft.getInstance().level));
    }

    // TODO Fix scaling
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(x, y, 100);
            pose.translate(10, 20, 0);
            pose.scale(-20, -20, -20);
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(Axis.XP.rotationDegrees(-30));
            pose.mulPose(Axis.YP.rotationDegrees(45));
            pose.translate(-0.5, -0.5, -0.5);
            this.fakeLevel.renderBlocks(graphics.pose());

            pose.mulPose(Axis.YP.rotationDegrees(180));
            pose.scale(1.5f, 1.5f, 1.5f);
            pose.translate(2, 0, 2);
        }

        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(80, 27, 0);
            Vector3f ARMOR_STAND_TRANSLATION = new Vector3f();
            Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ((float) Math.toRadians(30), (float) Math.toRadians(135), (float) Math.PI);
            InventoryScreen.renderEntityInInventory(graphics, this.x, this.y, 32, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, (LivingEntity) this.entity);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }
}
