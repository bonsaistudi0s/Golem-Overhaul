package tech.alexnijjar.golemoverhaul.common.config.info;

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColor;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink;
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue;

public class GolemOverhaulConfigInfo implements ResourcefulConfigInfo {

    public static final TranslatableValue TITLE = new TranslatableValue(
        "Golem Overhaul",
        "config.golemoverhaul.title"
    );

    public static final TranslatableValue TITLE_CLIENT = new TranslatableValue(
        "Golem Overhaul Client",
        "config.golemoverhaul.client.title"
    );

    public static final TranslatableValue DESCRIPTION = new TranslatableValue(
        "Golem overhaul adds awesome Golems!",
        "config.golemoverhaul.description"
    );

    private final boolean isClient;

    public GolemOverhaulConfigInfo(String id) {
        this.isClient = id.endsWith("-client");
    }

    @Override
    public TranslatableValue title() {
        return isClient ? TITLE_CLIENT : TITLE;
    }

    @Override
    public TranslatableValue description() {
        return DESCRIPTION;
    }

    @Override
    public String icon() {
        return "circle";
    }

    @Override
    public ResourcefulConfigColor color() {
        return GolemOverhaulConfigColor.INSTANCE;
    }

    @Override
    public ResourcefulConfigLink[] links() {
        return GolemOverhaulConfigLinks.LINKS;
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
