package tech.alexnijjar.golemoverhaul.common.entities.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.entities.AdditionalBeeData;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HoneyGolem;
import tech.alexnijjar.golemoverhaul.mixins.common.BeeAccessor;

public class LocateHoneyGolemHiveGoal extends Goal {

    private final Bee bee;
    private final BeeAccessor beeAccessor;

    public LocateHoneyGolemHiveGoal(Bee bee) {
        this.bee = bee;
        this.beeAccessor = (BeeAccessor) bee;
    }

    @Override
    public boolean canUse() {
        return beeAccessor.getRemainingCooldownBeforeLocatingNewHive() < 20 &&
            !((AdditionalBeeData) bee).golemoverhaul$hasGolemHive() &&
            beeAccessor.invokeWantsToEnterHive();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        beeAccessor.setRemainingCooldownBeforeLocatingNewHive(200);

        HoneyGolem golem = findHive();
        if (golem != null) {
            ((AdditionalBeeData) bee).golemoverhaul$setOwner(golem.getUUID());
        }
    }

    @Nullable
    private HoneyGolem findHive() {
        return bee.level().getEntitiesOfClass(HoneyGolem.class, bee.getBoundingBox().inflate(128))
            .stream().min((g1, g2) -> Double.compare(bee.distanceTo(g1), bee.distanceTo(g2)))
            .orElse(null);
    }
}
