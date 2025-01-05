package tech.alexnijjar.golemoverhaul.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeTypes;

import java.util.Objects;

@JeiPlugin
public class GolemOverhaulJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new GolemConstructionCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);

        registration.addRecipes(GolemConstructionCategory.RECIPE, level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.GOLEM_CONSTRUCTION.get())
            .stream()
            .map(RecipeHolder::value)
            .toList()
        );
    }
}
