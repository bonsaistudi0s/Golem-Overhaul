package tech.alexnijjar.golemoverhaul.common.constants;

import software.bernie.geckolib.core.animation.RawAnimation;

public class ConstantAnimations {

    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.golem.idle");
    public static final RawAnimation SITTING_IDLE = RawAnimation.begin().thenLoop("animation.golem.sitting_idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.golem.walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.golem.run");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.golem.attack");
    public static final RawAnimation ATTACK_LEFT = RawAnimation.begin().thenPlay("animation.golem.attack.left");
    public static final RawAnimation ATTACK_RIGHT = RawAnimation.begin().thenPlay("animation.golem.attack.right");
    public static final RawAnimation DIE = RawAnimation.begin().thenPlayAndHold("animation.golem.die");
    public static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("animation.golem.summon");
    public static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.golem.swim");
    public static final RawAnimation FALL = RawAnimation.begin().thenLoop("animation.golem.fall");

    public static final RawAnimation IDLE_WATER = RawAnimation.begin().thenLoop("animation.golem.idle_water");
    public static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.golem.spin");

    public static final RawAnimation WAKE_UP = RawAnimation.begin().thenPlay("animation.golem.wake_up");
    public static final RawAnimation IDLE_HIDDEN = RawAnimation.begin().thenLoop("animation.golem.idle_hidden");
    public static final RawAnimation HIDE = RawAnimation.begin().thenPlay("animation.golem.hide");
    public static final RawAnimation BARTER = RawAnimation.begin().thenPlay("animation.golem.barter");
    public static final RawAnimation OPEN = RawAnimation.begin().thenPlay("animation.golem.open");
}
