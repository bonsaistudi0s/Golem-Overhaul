package tech.alexnijjar.golemoverhaul.client.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeTypes;

public class GolemOverhaulReiPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new GolemConstructionCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(GolemConstructionRecipe.class, ModRecipeTypes.GOLEM_CONSTRUCTION.get(), GolemConstructionDisplay::new);
    }
}
