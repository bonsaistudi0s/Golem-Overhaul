package tech.alexnijjar.golemoverhaul.common.constants;

import software.bernie.geckolib.core.animation.RawAnimation;

public class ConstantAnimations {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.golem.idle");
    public static final RawAnimation SITTING_IDLE = RawAnimation.begin().thenLoop("animation.golem.sitting_idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.golem.walk");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.golem.attack");
    public static final RawAnimation DIE = RawAnimation.begin().thenPlayAndHold("animation.golem.die");
    public static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("animation.golem.summon");
    public static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.golem.swim");

    public static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.golem.spin");
}
