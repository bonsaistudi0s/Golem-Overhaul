package tech.alexnijjar.golemoverhaul.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

public class ModBiomeTags {

    public static final TagKey<Biome> COAL_GOLEM_SPAWNS = tag("coal_golem_spawns");
    public static final TagKey<Biome> HONEY_GOLEM_SPAWNS = tag("honey_golem_spawns");
    public static final TagKey<Biome> TERRACOTTA_GOLEM_SPAWNS = tag("terracotta_golem_spawns");

    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(GolemOverhaul.MOD_ID, name));
    }
}
