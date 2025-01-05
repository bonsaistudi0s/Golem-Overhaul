package tech.alexnijjar.golemoverhaul.client.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;

import java.util.Objects;

public record GolemConstructionCategory(IGuiHelper guiHelper) implements IRecipeCategory<GolemConstructionRecipe> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "golem_construction");
    public static final RecipeType<GolemConstructionRecipe> RECIPE = new RecipeType<>(ID, GolemConstructionRecipe.class);

    @Override
    public RecipeType<GolemConstructionRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public Component getTitle() {
        return ConstantComponents.GOLEM_CONSTRUCTION_CATEGORY;
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableItemStack(ModItems.CLAY_GOLEM_STATUE.get().getDefaultInstance());
    }

    @Override
    public int getWidth() {
        return 180;
    }

    @Override
    public int getHeight() {
        return 76;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GolemConstructionRecipe recipe, IFocusGroup group) {
        recipe.key().values()
            .stream()
            .map(BuiltInRegistries.BLOCK::get)
            .filter(Objects::nonNull)
            .map(Block::asItem)
            .filter(item -> item != Items.AIR)
            .forEach(item -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(Ingredient.of(item)));

        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredients(Ingredient.of(Objects.requireNonNull(BuiltInRegistries.ITEM.get(recipe.item()))));
    }

    @Override
    public void draw(GolemConstructionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        new GolemConstructionDrawable(mouseX, mouseY, recipe, 47, 58).draw(guiGraphics);
    }
}
