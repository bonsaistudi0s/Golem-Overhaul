package tech.alexnijjar.golemoverhaul.client.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.List;
import java.util.Objects;

public record GolemConstructionDisplay(GolemConstructionRecipe recipe) implements Display {

    @Override
    public List<EntryIngredient> getInputEntries() {
        return recipe.getKey().values()
            .stream()
            .map(BuiltInRegistries.BLOCK::get)
            .filter(Objects::nonNull)
            .map(Block::asItem)
            .filter(item -> item != Items.AIR)
            .map(EntryIngredients::of)
            .toList();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(Objects.requireNonNull(BuiltInRegistries.ITEM.get(recipe.getItem()))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return GolemConstructionCategory.ID;
    }
}
