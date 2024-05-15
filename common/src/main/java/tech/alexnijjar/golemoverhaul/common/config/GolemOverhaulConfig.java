package tech.alexnijjar.golemoverhaul.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.info.GolemOverhaulConfigInfo;

@Config(GolemOverhaul.MOD_ID)
@ConfigInfo.Provider(GolemOverhaulConfigInfo.class)
public final class GolemOverhaulConfig {
}
