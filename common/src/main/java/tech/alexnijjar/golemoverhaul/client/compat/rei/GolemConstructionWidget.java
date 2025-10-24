package tech.alexnijjar.golemoverhaul.client.compat.rei;

import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.client.compat.GolemConstructionRenderer;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.List;

public class GolemConstructionWidget extends Widget {

    private final GolemConstructionRenderer renderer;

    public GolemConstructionWidget(GolemConstructionRecipe recipe, int x, int y) {
        this.renderer = new GolemConstructionRenderer(recipe, x, y);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderer.render(graphics, mouseX, mouseY);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean isDragging) {
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
    }
}
