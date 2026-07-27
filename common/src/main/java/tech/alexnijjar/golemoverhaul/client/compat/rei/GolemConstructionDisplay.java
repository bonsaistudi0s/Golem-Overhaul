package tech.alexnijjar.golemoverhaul.client.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record GolemConstructionDisplay(GolemConstructionRecipe recipe) implements Display {

    public GolemConstructionDisplay(RecipeHolder<GolemConstructionRecipe> recipe) {
        this(recipe.value());
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return recipe.key().values()
            .stream()
            .flatMap(either -> either.map(
                resourceKey -> Stream.of(Objects.requireNonNull(BuiltInRegistries.BLOCK.get(resourceKey))),
                tagKey -> BuiltInRegistries.BLOCK.getTag(tagKey)
                    .map(holders -> holders.stream().map(Holder::value))
                    .orElse(Stream.empty())
            ))
            .map(Block::asItem)
            .filter(item -> item != Items.AIR)
            .distinct()
            .map(EntryIngredients::of)
            .toList();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(Objects.requireNonNull(BuiltInRegistries.ITEM.get(recipe.item()))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return GolemConstructionCategory.ID;
    }
}
