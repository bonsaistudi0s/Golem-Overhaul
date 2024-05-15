package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;
import tech.alexnijjar.golemoverhaul.datagen.builder.GolemConstructionRecipeBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        new GolemConstructionRecipeBuilder(
            List.of(
                "~^~",
                "/#/",
                "~/~"
            ),
            Map.of(
                "^", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.CARVED_PUMPKIN)),
                "#", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.NETHERITE_BLOCK)),
                "/", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.ANCIENT_DEBRIS)),
                "~", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.AIR))
            ),
            ResourceKey.create(Registries.ENTITY_TYPE, ModEntityTypes.NETHERITE_GOLEM.getId()),
            ResourceKey.create(Registries.ITEM, ModItems.NETHERITE_GOLEM_SPAWN_EGG.getId())
        ).save(output, new ResourceLocation(GolemOverhaul.MOD_ID, "golem_construction/netherite_golem"));

        new GolemConstructionRecipeBuilder(
            List.of(
                "~^~",
                "/#/",
                "~/~"
            ),
            Map.of(
                "^", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.CARVED_PUMPKIN)),
                "#", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.HAY_BLOCK)),
                "/", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.OAK_FENCE)),
                "~", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.AIR))
            ),
            ResourceKey.create(Registries.ENTITY_TYPE, ModEntityTypes.HAY_GOLEM.getId()),
            ResourceKey.create(Registries.ITEM, ModItems.HAY_GOLEM_SPAWN_EGG.getId())
        ).save(output, new ResourceLocation(GolemOverhaul.MOD_ID, "golem_construction/hay_golem"));

        new GolemConstructionRecipeBuilder(
            List.of(
                "~~~",
                "#^#",
                "~#~"
            ),
            Map.of(
                "^", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.SEA_LANTERN)),
                "#", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.DRIED_KELP_BLOCK)),
                "~", ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(Blocks.AIR))
            ),
            ResourceKey.create(Registries.ENTITY_TYPE, ModEntityTypes.KELP_GOLEM.getId()),
            ResourceKey.create(Registries.ITEM, ModItems.KELP_GOLEM_SPAWN_EGG.getId())
        ).save(output, new ResourceLocation(GolemOverhaul.MOD_ID, "golem_construction/kelp_golem"));
    }
}