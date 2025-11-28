package tech.alexnijjar.golemoverhaul.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeSerializers;

import java.util.*;
import java.util.function.Consumer;

public class GolemConstructionRecipeBuilder implements RecipeBuilder {
    private final EntityType<?> entity;
    private final Item item;
    private final List<String> pattern = new ArrayList<>();
    private final Map<Character, Block> key = new LinkedHashMap<>();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    private float blockScale = 1.0f;
    private float entityScale = 1.0f;
    private boolean visualOnly = false;

    private GolemConstructionRecipeBuilder(EntityType<?> entity, Item item) {
        this.entity = entity;
        this.item = item;
    }

    public static GolemConstructionRecipeBuilder golem(EntityType<?> entity, Item item) {
        return new GolemConstructionRecipeBuilder(entity, item);
    }

    public GolemConstructionRecipeBuilder pattern(String line) {
        this.pattern.add(line);
        return this;
    }

    public GolemConstructionRecipeBuilder define(Character symbol, Block block) {
        this.key.put(symbol, block);
        return this;
    }

    public GolemConstructionRecipeBuilder withBlockScale(float scale) {
        this.blockScale = scale;
        return this;
    }

    public GolemConstructionRecipeBuilder withEntityScale(float scale) {
        this.entityScale = scale;
        return this;
    }

    public GolemConstructionRecipeBuilder visualOnly(boolean visualOnly) {
        this.visualOnly = visualOnly;
        return this;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String criterionName, @NotNull CriterionTriggerInstance criterionTrigger) {
        this.advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.item;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation recipeId) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern defined for golem recipe " + recipeId);
        }
        if (this.key.isEmpty()) {
            throw new IllegalStateException("No key defined for golem recipe " + recipeId);
        }

        this.advancement.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);

        consumer.accept(new Result(
                recipeId,
                this.entity,
                this.item,
                this.pattern,
                this.key,
                this.blockScale,
                this.entityScale,
                this.visualOnly,
                this.advancement,
                ResourceLocation.fromNamespaceAndPath(recipeId.getNamespace(), "recipes/" + recipeId.getPath())
        ));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final EntityType<?> entity;
        private final Item item;
        private final List<String> pattern;
        private final Map<Character, Block> key;
        private final float blockScale;
        private final float entityScale;
        private final boolean visualOnly;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, EntityType<?> entity, Item item, List<String> pattern, Map<Character, Block> key, float blockScale, float entityScale, boolean visualOnly, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.entity = entity;
            this.item = item;
            this.pattern = pattern;
            this.key = key;
            this.blockScale = blockScale;
            this.entityScale = entityScale;
            this.visualOnly = visualOnly;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("blockScale", this.blockScale);
            json.addProperty("entityScale", this.entityScale);
            if (this.visualOnly) {
                json.addProperty("visualOnly", true);
            }

            json.addProperty("entity", Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(this.entity)).toString());
            json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.item)).toString());

            var patternLines = new JsonArray();
            for (var patternLine : this.pattern) {
                patternLines.add(patternLine);
            }
            json.add("pattern", patternLines);

            var keyObject = new JsonObject();
            for (var entry : this.key.entrySet()) {
                keyObject.addProperty(String.valueOf(entry.getKey()), Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(entry.getValue())).toString());
            }
            json.add("key", keyObject);
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return ModRecipeSerializers.GOLEM_CONSTRUCTION.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
