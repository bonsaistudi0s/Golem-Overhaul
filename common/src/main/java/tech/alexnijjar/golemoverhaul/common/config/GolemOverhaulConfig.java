package tech.alexnijjar.golemoverhaul.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.info.GolemOverhaulConfigInfo;

@Config(GolemOverhaul.MOD_ID)
@ConfigInfo.Provider(GolemOverhaulConfigInfo.class)
public final class GolemOverhaulConfig {

    @ConfigEntry(
        id = "allowSpawning",
        translation = "config.golemoverhaul.allowSpawning"
    )
    public static boolean allowSpawning = true;

    @ConfigEntry(
        id = "spawnBarrelGolems",
        translation = "config.golemoverhaul.spawnBarrelGolems"
    )
    public static boolean spawnBarrelGolems = true;

    @ConfigEntry(
        id = "spawnCoalGolems",
        translation = "config.golemoverhaul.spawnCoalGolems"
    )
    public static boolean spawnCoalGolems = true;

    @ConfigEntry(
        id = "spawnHayGolems",
        translation = "config.golemoverhaul.spawnHayGolems"
    )
    public static boolean spawnHayGolems = true;

    @ConfigEntry(
        id = "spawnHoneyGolems",
        translation = "config.golemoverhaul.spawnHoneyGolems"
    )
    public static boolean spawnHoneyGolems = true;

    @ConfigEntry(
        id = "spawnSlimeGolems",
        translation = "config.golemoverhaul.spawnSlimeGolems"
    )
    public static boolean spawnSlimeGolems = true;

    @ConfigEntry(
        id = "spawnTerracottaGolems",
        translation = "config.golemoverhaul.spawnTerracottaGolems"
    )
    public static boolean spawnTerracottaGolems = true;
}
