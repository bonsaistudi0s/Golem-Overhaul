package tech.alexnijjar.golemoverhaul.datagen.builder;

import com.teamresourceful.resourcefullib.common.datagen.CodecRecipeBuilder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GolemConstructionRecipeBuilder extends CodecRecipeBuilder {

    private final GolemConstructionRecipe recipe;

    public GolemConstructionRecipeBuilder(
        List<String> pattern,
        Map<String, ResourceKey<Block>> key,
        ResourceKey<EntityType<?>> entity,
        ResourceKey<Item> result,
        boolean visualOnly,
        float blockScale,
        float entityScale
    ) {
        recipe = new GolemConstructionRecipe(pattern, key, entity, result, visualOnly, blockScale, entityScale);
    }

    @Override
    public @NotNull Item getResult() {
        return Objects.requireNonNull(BuiltInRegistries.ITEM.get(recipe.item()));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        var builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        recipeOutput.accept(id, recipe, builder
            .build(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/golem_construction/" + id.getPath())));
    }
}
