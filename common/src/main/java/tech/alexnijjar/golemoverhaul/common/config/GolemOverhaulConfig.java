package tech.alexnijjar.golemoverhaul.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import com.teamresourceful.resourcefulconfig.web.annotations.Gradient;
import com.teamresourceful.resourcefulconfig.web.annotations.Link;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

@Config(GolemOverhaul.MOD_ID)
@WebInfo(
        icon = "circle",
        title = "Golem Overhaul",
        description = "Golem overhaul adds awesome Golems!",
        gradient = @Gradient(
                first = "#e8bff2",
                second = "#181a23",
                value = "45deg"
        ),
        links = {
                @Link(
                        value = "https://github.com/bonsaistudi0s/golem-overhaul",
                        icon = "github",
                        title = "GitHub"
                ),
                @Link(
                        value = "https://discord.gg/sGwxnFV",
                        icon = "gamepad-2",
                        title = "Discord"
                ),
                @Link(
                        value = "https://modrinth.com/mod/golem-overhaul",
                        icon = "modrinth",
                        title = "Modrinth"
                ),
                @Link(
                        value = "https://www.curseforge.com/minecraft/mc-mods/golem-overhaul",
                        icon = "curseforge",
                        title = "CurseForge"
                )
        }
)
public final class GolemOverhaulConfig {

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "allowSpawning",
            translation = "config.golemoverhaul.allowSpawning"
    )
    public static boolean allowSpawning = true;

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "spawnBarrelGolems",
            translation = "config.golemoverhaul.spawnBarrelGolems"
    )
    public static boolean spawnBarrelGolems = true;

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "spawnCoalGolems",
            translation = "config.golemoverhaul.spawnCoalGolems"
    )
    public static boolean spawnCoalGolems = true;

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "spawnHayGolems",
            translation = "config.golemoverhaul.spawnHayGolems"
    )
    public static boolean spawnHayGolems = true;

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "spawnHoneyGolems",
            translation = "config.golemoverhaul.spawnHoneyGolems"
    )
    public static boolean spawnHoneyGolems = true;

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "spawnSlimeGolems",
            translation = "config.golemoverhaul.spawnSlimeGolems"
    )
    public static boolean spawnSlimeGolems = true;

    @ConfigEntry(
            type = EntryType.BOOLEAN,
            id = "spawnTerracottaGolems",
            translation = "config.golemoverhaul.spawnTerracottaGolems"
    )
    public static boolean spawnTerracottaGolems = true;
}
