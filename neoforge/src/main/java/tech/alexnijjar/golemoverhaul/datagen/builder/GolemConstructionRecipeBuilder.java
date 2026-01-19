package tech.alexnijjar.golemoverhaul.datagen.builder;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.datagen.CodecRecipeBuilder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GolemConstructionRecipeBuilder extends CodecRecipeBuilder {

    private final GolemConstructionRecipe recipe;

    @SuppressWarnings("unchecked")
    public GolemConstructionRecipeBuilder(
        List<String> pattern,
        Map<String, ?> key,
        ResourceKey<EntityType<?>> entity,
        ResourceKey<Item> result,
        boolean visualOnly,
        float blockScale,
        float entityScale
    ) {
        Map<String, Either<ResourceKey<Block>, TagKey<Block>>> recipeKey = new HashMap<>();
        key.forEach((k, v) -> {
            if (v instanceof ResourceKey<?> resourceKey && resourceKey.isFor(Registries.BLOCK)) {
                recipeKey.put(k, Either.left((ResourceKey<Block>) resourceKey));
            } else if (v instanceof TagKey<?> tagKey && tagKey.isFor(Registries.BLOCK)) {
                recipeKey.put(k, Either.right((TagKey<Block>) tagKey));
            } else {
                throw new IllegalArgumentException("Invalid type for key " + k + ": " + v.getClass().getName());
            }
        });
        recipe = new GolemConstructionRecipe(pattern, recipeKey, entity, result, visualOnly, blockScale, entityScale);
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
