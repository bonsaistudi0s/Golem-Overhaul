package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagProvider extends TagsProvider<EntityType<?>> {

    public ModEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENTITY_TYPE, completableFuture, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
    }
}
