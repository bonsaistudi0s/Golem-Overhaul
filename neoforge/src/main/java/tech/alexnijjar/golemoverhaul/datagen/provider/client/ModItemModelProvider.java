package tech.alexnijjar.golemoverhaul.datagen.provider.client;


import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModItems.SPAWN_EGGS.getEntries().stream().map(RegistryEntry::get).forEach(this::spawnEggItem);
        basicItem(ModItems.HONEY_BLOB.get());
    }

    public void spawnEggItem(Item item) {
        getBuilder(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).toString())
            .parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
    }
}
