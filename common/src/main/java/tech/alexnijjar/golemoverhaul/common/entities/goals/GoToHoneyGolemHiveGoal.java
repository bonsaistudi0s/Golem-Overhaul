package tech.alexnijjar.golemoverhaul.common.entities.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.entities.HoneyGolem;
import tech.alexnijjar.golemoverhaul.mixins.common.BeeInvoker;

public class GoToHoneyGolemHiveGoal extends Goal {
    private final Bee bee;
    @Nullable
    private HoneyGolem target;

    public GoToHoneyGolemHiveGoal(Bee bee) {
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        return !bee.isAngry()
            && !bee.hasRestriction()
            && ((BeeInvoker) bee).invokeWantsToEnterHive();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse()
            && target != null
            && target.isAlive()
            && target.canPutBee()
            && !bee.isRemoved();
    }

    @Override
    public void start() {
        target = bee.level().getNearestEntity(HoneyGolem.class,
            TargetingConditions.DEFAULT,
            bee,
            bee.getX(),
            bee.getY(),
            bee.getZ(),
            bee.getBoundingBox().inflate(16.0));
    }

    @Override
    public void stop() {
        super.stop();
        target = null;
        bee.getNavigation().stop();
        bee.setStayOutOfHiveCountdown(400);
    }

    @Override
    public void tick() {
        if (target == null || !target.canPutBee()) return;
        if (bee.distanceTo(target) < 2.0) {
            target.putBee(bee);
        } else {
            bee.getNavigation().moveTo(target, 1.0);
        }
    }
}
