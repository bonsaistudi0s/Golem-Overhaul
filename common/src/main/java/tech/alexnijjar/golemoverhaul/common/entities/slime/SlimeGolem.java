package tech.alexnijjar.golemoverhaul.common.entities.slime;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class SlimeGolem extends BaseGolem {

    public SlimeGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 0;
        setMaxUpStep(0);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 50.0)
            .add(Attributes.MOVEMENT_SPEED, 0.35)
            .add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.ATTACK_KNOCKBACK, 3.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, 5, state -> {
            boolean moving = state.getLimbSwingAmount() > 0.1 || state.getLimbSwingAmount() < -0.1;
            return state.setAndContinue(moving ?
                ConstantAnimations.WALK :
                ConstantAnimations.IDLE);
        }));

        controllerRegistrar.add(new AnimationController<>(this, "attack_controller", 0, state -> {
            if (!hasAttackAnimation()) return PlayState.STOP;
            if (attackAnimationTick == 0) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }
            return state.setAndContinue(ConstantAnimations.ATTACK_RIGHT);
        }));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new SlimeGolemMeleeAttackGoal());
    }

    @Override
    public boolean canFloatInWater() {
        return true;
    }

    @Override
    public boolean villageBound() {
        return false;
    }

    @Override
    public boolean doesSwingAttack() {
        return false;
    }

    @Override
    public Item getRepairItem() {
        return Items.SLIME_BALL;
    }

    @Override
    public int getRepairItemHealAmount() {
        return 10;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SLIME_DEATH;
    }

    @Override
    public boolean playIronGolemStepSound() {
        return false;
    }

    @Override
    public IronGolem.Crackiness getCrackiness() {
        return Crackiness.NONE;
    }

    @Override
    public int getAttackSwingTicks() {
        return 22;
    }

    @Override
    public boolean canDoMeleeAttack() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (getType().equals(ModEntityTypes.BABY_SLIME_GOLEM.get())) {
            super.remove(reason);
            return;
        }

        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            var slime = new BabySlimeGolem(ModEntityTypes.BABY_SLIME_GOLEM.get(), level());
            float f = (float)i / 4.0F;
            float g = ((float) (i % 2) - 0.5F) * f;
            float h = ((float) (i / 2) - 0.5F) * f;
            slime.moveTo(this.getX() + (double) g, this.getY() + 0.5, this.getZ() + (double) h, this.random.nextFloat() * 360.0F, 0.0F);
            level().addFreshEntity(slime);
        }
        super.remove(reason);
    }

    private class SlimeGolemMeleeAttackGoal extends MeleeAttackGoal {
        public SlimeGolemMeleeAttackGoal() {
            super(SlimeGolem.this, 1.0, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            float range = getBbWidth() - 0.1f;
            return range * 2.0f * range * 2.0f + attackTarget.getBbWidth();
        }
    }
}
