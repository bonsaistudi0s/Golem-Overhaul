package tech.alexnijjar.golemoverhaul.common.entities.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.entities.AdditionalBeeData;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HoneyGolem;

import java.util.UUID;

public class HealHoneyGolemGoal extends Goal {

    private final Bee bee;
    @Nullable
    private HoneyGolem target;

    public HealHoneyGolemGoal(Bee bee) {
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        if (bee.isAlive() && !bee.isAngry()) {
            HoneyGolem golem = findTarget();
            return golem != null && golem.getHealth() < golem.getMaxHealth();
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        target = null;
        bee.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null || bee.getTarget() != null) {
            stop();
            return;
        }

        if (bee.distanceTo(target) < 2 && bee.tickCount % 40 == 0) {
            target.healFromNectar();
        } else {
            bee.getNavigation().moveTo(target, 1);
        }
    }

    @Nullable
    private HoneyGolem findTarget() {
        if (target != null) return target;

        UUID owner = ((AdditionalBeeData) bee).golemoverhaul$getOwner();
        if (owner != null && bee.level() instanceof ServerLevel level) {
            if (level.getEntity(owner) instanceof HoneyGolem golem) {
                this.target = golem;
            } else {
                ((AdditionalBeeData) bee).golemoverhaul$setOwner(null);
            }
        } else {
            target = bee.level().getEntitiesOfClass(HoneyGolem.class, bee.getBoundingBox().inflate(128))
                .stream().min((g1, g2) -> Double.compare(bee.distanceTo(g1), bee.distanceTo(g2)))
                .orElse(null);
        }
        return target;
    }
}
