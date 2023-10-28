package tech.alexnijjar.golemoverhaul.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.web.annotations.Gradient;
import com.teamresourceful.resourcefulconfig.web.annotations.Link;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;

@Config("golemoverhaul")
@WebInfo(
    title = "Golem Overhaul",
    description = "Golem overhaul adds awesome Golems!",

    icon = "circle",
    gradient = @Gradient(value = "45deg", first = "#e8bff2", second = "#181a23"),

    links = {
        @Link(value = "https://discord.gg/sGwxnFV", icon = "gamepad-2", title = "Discord"),
        @Link(value = "https://github.com/bonsaistudi0s/golem-overhaul", icon = "github", title = "GitHub"),

        @Link(value = "https://www.curseforge.com/minecraft/mc-mods/golem-overhaul", icon = "curseforge", title = "CurseForge"),
        @Link(value = "https://modrinth.com/mod/golem-overhaul", icon = "modrinth", title = "Modrinth"),
    }
)
public final class GolemOverhaulConfig {
}
