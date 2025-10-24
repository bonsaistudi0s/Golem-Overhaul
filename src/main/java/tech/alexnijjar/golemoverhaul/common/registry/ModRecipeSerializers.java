package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

public class ModRecipeSerializers {

    public static final ResourcefulRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, GolemOverhaul.MOD_ID);

    public static final RegistryEntry<CodecRecipeSerializer<GolemConstructionRecipe>> GOLEM_CONSTRUCTION = RECIPE_SERIALIZERS.register("golem_construction", () ->
        new CodecRecipeSerializer<>(ModRecipeTypes.GOLEM_CONSTRUCTION.get(), GolemConstructionRecipe.CODEC, GolemConstructionRecipe.NETWORK_CODEC));
}
