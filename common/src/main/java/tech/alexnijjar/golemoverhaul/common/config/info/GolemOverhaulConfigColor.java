package tech.alexnijjar.golemoverhaul.common.config.info;

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColorGradient;

public class GolemOverhaulConfigColor implements ResourcefulConfigColorGradient {

    public static final GolemOverhaulConfigColor INSTANCE = new GolemOverhaulConfigColor();

    @Override
    public String first() {
        return "#e8bff2";
    }

    @Override
    public String second() {
        return "#181a23";
    }

    @Override
    public String degree() {
        return "45deg";
    }
}
