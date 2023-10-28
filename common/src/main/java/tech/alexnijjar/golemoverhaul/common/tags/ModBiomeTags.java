package tech.alexnijjar.golemoverhaul.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

public class ModBiomeTags {

    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(GolemOverhaul.MOD_ID, name));
    }
}
