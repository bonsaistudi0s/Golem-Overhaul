package tech.alexnijjar.golemoverhaul.common.entities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.HoneyBlobProjectile;

public class HoneyGolem extends BaseGolem implements RangedAttackMob {

    public HoneyGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 8;
        setMaxUpStep(0);
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
            return state.setAndContinue(ConstantAnimations.ATTACK);
        }));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.35, 20, 15.0F));
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 6.0);
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
        return Items.HONEY_BOTTLE;
    }

    @Override
    public int getRepairItemHealAmount() {
        return 20;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.HONEY_BLOCK_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HONEY_BLOCK_BREAK;
    }

    @Override
    public boolean playDefaultStepSound() {
        return false;
    }

    @Override
    public SoundEvent getDamageSound() {
        return SoundEvents.CORAL_BLOCK_BREAK;
    }

    @Override
    public int getAttackAnimationTick() {
        return 15;
    }

    @Override
    public boolean canDoMeleeAttack() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            attackAnimationTick = getAttackAnimationTick();
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        var projectile = new HoneyBlobProjectile(level(), this);
        projectile.setPos(getX(), getY(), getZ());

        double d = target.getX() - getX();
        double e = target.getY() - projectile.getY();
        double f = target.getZ() - getZ();
        double g = Math.sqrt(d * d + f * f) * 0.2f;
        projectile.shoot(d, e + g + 0.8, f, 1.2f, 3.0f);

        level().addFreshEntity(projectile);
        playSound(SoundEvents.HONEY_BLOCK_STEP, 1, 0.4f / (getRandom().nextFloat() * 0.4f + 0.8f));
        level().broadcastEntityEvent(this, (byte) 4);
    }
}
