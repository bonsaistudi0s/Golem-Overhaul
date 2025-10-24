package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;

public class ModRecipeTypes {

    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_TYPE, GolemOverhaul.MOD_ID);

    public static final RegistryEntry<RecipeType<GolemConstructionRecipe>> GOLEM_CONSTRUCTION = RECIPE_TYPES.register("golem_construction", () -> GolemConstructionRecipe.Type.INSTANCE);
}