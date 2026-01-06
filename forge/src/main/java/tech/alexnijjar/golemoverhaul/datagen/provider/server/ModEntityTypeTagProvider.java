package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.tags.ModEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagProvider extends TagsProvider<EntityType<?>> {

    public ModEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENTITY_TYPE, completableFuture, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(TagEntry.element(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.NETHERITE_GOLEM.get())));
        tag(ModEntityTypeTags.HONEY_IMMUNE)
            .add(TagEntry.element(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.HONEY_GOLEM.get())))
            .add(TagEntry.element(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE)));
    }
}
