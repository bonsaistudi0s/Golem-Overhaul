package tech.alexnijjar.golemoverhaul.common.constants;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ConstantComponents {

    public static final Component NETHERITE_GOLEM_SUMMON_KEY = Component.translatable("key.golemoverhaul.netherite_golem_summon");
    public static final Component GOLEM_OVERHAUL_CATEGORY = Component.translatable("key.categories.golemoverhaul");
    public static final Component GOLEM_CONSTRUCTION_CATEGORY = Component.translatable("text.golemoverhaul.golem_construction");

    public static final Component CANDLE_GOLEM_TOOLTIP = Component.translatable("tooltip.golemoverhaul.candle_golem").copy().withStyle(ChatFormatting.GRAY);
    public static final Component CLAY_GOLEM_STATUE_TOOLTIP = Component.translatable("tooltip.golemoverhaul.clay_golem_statue").copy().withStyle(ChatFormatting.GRAY);
}
