package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {

    @Shadow
    @Final
    protected Class<T> targetType;

    @Shadow
    @Nullable
    protected LivingEntity target;

    @Shadow
    protected TargetingConditions targetConditions;

    @Shadow
    protected abstract AABB getTargetSearchArea(double targetDistance);

    private NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    // Make mobs that normally attack iron golems attack Golem Overhaul golems as well.
    @Inject(
        method = "findTarget",
        at = @At("TAIL")
    )
    private void golemoverhaul$findTarget(CallbackInfo ci) {
        if (this.targetType == IronGolem.class && target == null) {
            this.target = this.mob
                .level()
                .getNearestEntity(
                    this.mob.level().getEntities(this.mob, this.getTargetSearchArea(this.getFollowDistance()),
                            entity -> entity instanceof BaseGolem golem && golem.canTarget())
                        .stream()
                        .map(e -> (LivingEntity) e)
                        .toList(),
                    this.targetConditions,
                    this.mob,
                    this.mob.getX(),
                    this.mob.getEyeY(),
                    this.mob.getZ()
                );
        }
    }
}
