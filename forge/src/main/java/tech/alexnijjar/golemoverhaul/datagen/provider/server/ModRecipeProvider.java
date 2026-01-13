package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;
import tech.alexnijjar.golemoverhaul.datagen.builder.GolemConstructionRecipeBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        buildBlockRecipes(writer);
        buildItemRecipes(writer);
        buildGolemConstructionRecipes(writer);
    }

    private void buildBlockRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CANDLE_GOLEM_BLOCK.get())
                .pattern(" / ")
                .pattern("###")
                .pattern("###")
                .define('#', Items.HONEYCOMB)
                .define('/', Items.STRING)
                .showNotification(true)
                .unlockedBy("has_honeycomb", has(Items.HONEYCOMB))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CLAY_GOLEM_STATUE.get())
                .pattern("/#/")
                .pattern(" # ")
                .define('#', Items.CLAY_BALL)
                .define('/', Items.CLAY)
                .showNotification(true)
                .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
                .save(writer);
    }

    private void buildItemRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COAL_GOLEM.get(), 4)
                .pattern("###")
                .pattern("###")
                .pattern("# #")
                .define('#', ItemTags.COALS)
                .showNotification(true)
                .unlockedBy("has_coal", has(ItemTags.COALS))
                .save(writer);
    }

    private void buildGolemConstructionRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        GolemConstructionRecipeBuilder
                .golem(ModEntityTypes.NETHERITE_GOLEM.get(), ModItems.NETHERITE_GOLEM_SPAWN_EGG.get())
                .pattern("~^~")
                .pattern("###")
                .pattern("~#~")
                .define('^', Blocks.CARVED_PUMPKIN)
                .define('#', Blocks.ANCIENT_DEBRIS)
                .define('~', Blocks.AIR)
                .visualOnly(false)
                .withBlockScale(0.75f)
                .withEntityScale(0.45f)
                .save(writer, GolemOverhaul.asResource("golem_construction/netherite_golem"));

        GolemConstructionRecipeBuilder
                .golem(ModEntityTypes.HAY_GOLEM.get(), ModItems.HAY_GOLEM_SPAWN_EGG.get())
                .pattern("~^~")
                .pattern("/#/")
                .pattern("~/~")
                .define('^', Blocks.CARVED_PUMPKIN)
                .define('#', Blocks.HAY_BLOCK)
                .define('/', Blocks.OAK_FENCE)
                .define('~', Blocks.AIR)
                .visualOnly(false)
                .withBlockScale(0.75f)
                .withEntityScale(0.6f)
                .save(writer, GolemOverhaul.asResource("golem_construction/hay_golem"));

        GolemConstructionRecipeBuilder
                .golem(ModEntityTypes.KELP_GOLEM.get(), ModItems.KELP_GOLEM_SPAWN_EGG.get())
                .pattern("~~~")
                .pattern("#^#")
                .pattern("~#~")
                .define('^', Blocks.SEA_LANTERN)
                .define('#', Blocks.DRIED_KELP_BLOCK)
                .define('~', Blocks.AIR)
                .visualOnly(false)
                .withBlockScale(0.75f)
                .withEntityScale(0.5f)
                .save(writer, GolemOverhaul.asResource("golem_construction/kelp_golem"));

        GolemConstructionRecipeBuilder
                .golem(ModEntityTypes.CANDLE_GOLEM.get(), ModItems.CANDLE_GOLEM_SPAWN_EGG.get())
                .pattern("#")
                .define('#', ModBlocks.CANDLE_GOLEM_BLOCK.get())
                .visualOnly(true)
                .withBlockScale(1.5f)
                .withEntityScale(1.0f)
                .save(writer, GolemOverhaul.asResource("golem_construction/candle_golem"));

        GolemConstructionRecipeBuilder
                .golem(ModEntityTypes.TERRACOTTA_GOLEM.get(), ModItems.TERRACOTTA_GOLEM_SPAWN_EGG.get())
                .pattern("#")
                .define('#', ModBlocks.CLAY_GOLEM_STATUE.get())
                .visualOnly(true)
                .withBlockScale(1.5f)
                .withEntityScale(1.0f)
                .save(writer, GolemOverhaul.asResource("golem_construction/terracotta_golem"));
    }
}
