package tech.alexnijjar.golemoverhaul.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.info.GolemOverhaulConfigInfo;

@Config(GolemOverhaul.MOD_ID)
@ConfigInfo.Provider(GolemOverhaulConfigInfo.class)
public final class GolemOverhaulConfig {

    @ConfigEntry(
        id = "allowSpawning",
        type = EntryType.BOOLEAN,
        translation = "config.golemoverhaul.allowSpawning"
    )
    public static boolean allowSpawning = true;

    @ConfigEntry(
        id = "spawnBarrelGolems",
        type = EntryType.BOOLEAN,
        translation = "config.endermanoverhaul.spawnBarrelGolems"
    )
    public static boolean spawnBarrelGolems = true;

    @ConfigEntry(
        id = "spawnCoalGolems",
        type = EntryType.BOOLEAN,
        translation = "config.endermanoverhaul.spawnCoalGolems"
    )
    public static boolean spawnCoalGolems = true;

    @ConfigEntry(
        id = "spawnHayGolems",
        type = EntryType.BOOLEAN,
        translation = "config.endermanoverhaul.spawnHayGolems"
    )
    public static boolean spawnHayGolems = true;

    @ConfigEntry(
        id = "spawnHoneyGolems",
        type = EntryType.BOOLEAN,
        translation = "config.endermanoverhaul.spawnHoneyGolems"
    )
    public static boolean spawnHoneyGolems = true;

    @ConfigEntry(
        id = "spawnSlimeGolems",
        type = EntryType.BOOLEAN,
        translation = "config.endermanoverhaul.spawnSlimeGolems"
    )
    public static boolean spawnSlimeGolems = true;

    @ConfigEntry(
        id = "spawnTerracottaGolems",
        type = EntryType.BOOLEAN,
        translation = "config.endermanoverhaul.spawnTerracottaGolems"
    )
    public static boolean spawnTerracottaGolems = true;
}
