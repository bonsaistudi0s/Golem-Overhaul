package tech.alexnijjar.golemoverhaul.common.entities.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.entities.AdditionalBeeData;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HoneyGolem;
import tech.alexnijjar.golemoverhaul.mixins.common.BeeAccessor;

import java.util.UUID;

public class GoToHoneyGolemHiveGoal extends Goal {

    private final Bee bee;
    private final BeeAccessor beeAccessor;

    private int travellingTicks;

    @Nullable
    private HoneyGolem hive;

    public GoToHoneyGolemHiveGoal(Bee bee) {
        this.bee = bee;
        this.beeAccessor = (BeeAccessor) bee;
        bee.level().random.nextInt(10);
    }

    @Override
    public boolean canUse() {
        if (bee.isAngry() && !bee.hasRestriction() && beeAccessor.invokeWantsToEnterHive()) {
            HoneyGolem golem = hive == null ? this.findHive() : hive;
            return golem != null && !golem.isDeadOrDying() && golem.canPutBee();
        }
        return false;
    }

    @Override
    public void start() {
        this.travellingTicks = 0;
        this.hive = findHive();
    }

    @Override
    public void stop() {
        this.travellingTicks = 0;
        this.bee.getNavigation().stop();
        this.bee.getNavigation().resetMaxVisitedNodesMultiplier();
        this.hive = null;
    }

    @Override
    public void tick() {
        if (this.hive == null) return;
        if (bee.distanceTo(this.hive) < 2) {
            this.hive.putBee(bee);
            stop();
        } else {
            this.travellingTicks++;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
                stop();
            } else {
                bee.getNavigation().moveTo(this.hive, 1);
            }
        }
    }

    @Nullable
    private HoneyGolem findHive() {
        UUID owner = ((AdditionalBeeData) bee).golemoverhaul$getOwner();
        if (owner != null && bee.level() instanceof ServerLevel level) {
            if (level.getEntity(owner) instanceof HoneyGolem golem) {
                return golem;
            }
        }
        return null;
    }
}
