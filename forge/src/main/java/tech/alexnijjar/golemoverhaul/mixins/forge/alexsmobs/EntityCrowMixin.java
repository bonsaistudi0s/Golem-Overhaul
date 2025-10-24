package tech.alexnijjar.golemoverhaul.mixins.forge.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;

import javax.annotation.Nullable;
import java.util.EnumSet;

@Mixin(EntityCrow.class)
public abstract class EntityCrowMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        //noinspection DataFlowIssue
        var mob = (EntityCrow) (Object) this;

        mob.goalSelector.addGoal(1, new AIAvoidHayGolems(mob));
    }

    private static class AIAvoidHayGolems extends Goal {

        private final EntityCrow mob;

        @Nullable
        private HayGolem toAvoid;

        private final float maxDist = 16.0F;
        private int runDelay = 70;
        private Vec3 flightTarget;

        private final TargetingConditions avoidEntityTargeting;

        private AIAvoidHayGolems(EntityCrow mob) {
            this.mob = mob;
            this.avoidEntityTargeting = TargetingConditions.forCombat().range(maxDist);
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canContinueToUse() {
            return this.toAvoid != null && this.isCloseToTarget(16.0F);
        }

        public boolean isCloseToTarget(double dist) {
            if (this.toAvoid == null)
                return false;

            return mob.distanceToSqr(toAvoid.position()) < dist * dist;
        }

        public boolean canUse() {
            if (mob.isTame()) {
                return false;
            } else if (this.runDelay > 0) {
                --this.runDelay;
                return false;
            } else {
                this.runDelay = 70 + mob.getRandom().nextInt(150);
                var maxDistVertically = 12.0F;
                this.toAvoid = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(HayGolem.class, this.mob.getBoundingBox().inflate(maxDist, maxDistVertically, maxDist), (arg) -> true), this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
                return this.toAvoid != null;
            }
        }

        public void start() {
            if (toAvoid == null)
                return;

            mob.fleePumpkinFlag = 200;
            var vec = mob.getBlockInViewAway(toAvoid.position(), 10.0F);
            if (vec != null) {
                this.flightTarget = vec;
                mob.setFlying(true);
                mob.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.0F);
            }

        }

        public void tick() {
            if (toAvoid == null)
                return;

            if (!this.isCloseToTarget(16.0F))
                return;

            mob.fleePumpkinFlag = 200;
            if (this.flightTarget == null || mob.distanceToSqr(this.flightTarget) < (double)2.0F) {
                var vec = mob.getBlockInViewAway(toAvoid.position(), 10.0F);
                if (vec != null) {
                    this.flightTarget = vec;
                    mob.setFlying(true);
                }
            }

            if (this.flightTarget != null) {
                mob.getMoveControl().setWantedPosition(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.0F);
            }
        }

        public void stop() {
            this.flightTarget = null;
        }
    }
}