package tech.alexnijjar.golemoverhaul.client.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import tech.alexnijjar.golemoverhaul.client.compat.GolemConstructionRenderer;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

public class GolemConstructionDrawable implements IDrawable {

    private final double mouseX;
    private final double mouseY;
    private final GolemConstructionRenderer renderer;

    public GolemConstructionDrawable(double mouseX, double mouseY, GolemConstructionRecipe recipe, int x, int y) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.renderer = new GolemConstructionRenderer(recipe, x, y);
    }

    @Override
    public int getWidth() {
        return renderer.getWidth();
    }

    @Override
    public int getHeight() {
        return renderer.getHeight();
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        renderer.render(graphics, (int) mouseX, (int) mouseY);
    }
}
