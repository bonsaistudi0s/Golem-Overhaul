package tech.alexnijjar.golemoverhaul.datagen.provider.client;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.CANDLE_GOLEM_BLOCK.get(),
                models().getExistingFile(modLoc("block/candle_golem_block")));

        horizontalBlock(ModBlocks.CLAY_GOLEM_STATUE.get(),
                models().getExistingFile(modLoc("block/clay_golem_statue")));
    }
}