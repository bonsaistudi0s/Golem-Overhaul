package tech.alexnijjar.golemoverhaul.datagen.provider.client;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;
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
        basicItem(ModItems.COAL_GOLEM.get());

        blockItemModel(ModBlocks.CANDLE_GOLEM_BLOCK.get());
        blockItemModel(ModBlocks.CLAY_GOLEM_STATUE.get());
    }

    public void blockItemModel(Block block) {
        var blockName = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath();
        getBuilder(blockName).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + blockName)));
    }
}
