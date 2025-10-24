package tech.alexnijjar.golemoverhaul.client.forge;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class GolemOverhaulClientImpl {

    public static void registerBlockRenderType(Supplier<Block> block, RenderType type) {
        // no-op, Forge automatically registers the block with the correct render layer it reads from the model's json
    }
}
