package tech.alexnijjar.golemoverhaul.client.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class GolemConstructionCategory implements DisplayCategory<GolemConstructionDisplay> {

    public static final CategoryIdentifier<GolemConstructionDisplay> ID = CategoryIdentifier.of(GolemOverhaul.MOD_ID, "golem_construction");

    @Override
    public CategoryIdentifier<? extends GolemConstructionDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return ConstantComponents.GOLEM_CONSTUCTION_CATEGORY;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.CLAY_GOLEM_STATUE.get());
    }

    @Override
    public int getDisplayWidth(GolemConstructionDisplay display) {
        return 184;
    }

    @Override
    public int getDisplayHeight() {
        return 117;
    }

    @Override
    public List<Widget> setupDisplay(GolemConstructionDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(new GolemConstructionWidget(display.recipe(), bounds.x + 47, bounds.y + 58));
        widgets.add(Widgets.createArrow(new Point(bounds.x + 70, bounds.y + 50)));

        return widgets;
    }
}
