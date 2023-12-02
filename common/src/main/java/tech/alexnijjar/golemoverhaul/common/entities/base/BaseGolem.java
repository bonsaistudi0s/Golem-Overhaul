package tech.alexnijjar.golemoverhaul.common.entities.base;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;

public abstract class BaseGolem extends IronGolem implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackAnimationTick;

    public BaseGolem(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
        if (canFloatInWater()) {
            goalSelector.addGoal(0, new FloatGoal(this));
        }
        goalSelector.addGoal(1, new GolemMeleeAttackGoal(1.0, true));
        goalSelector.addGoal(2, new GolemMoveTowardsTargetGoal(0.9, 32.0F));
        if (villageBound()) {
            goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
            goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
        } else {
            goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6));
        }
        if (offersFlowers()) {
            goalSelector.addGoal(5, new OfferFlowerGoal(this));
        }
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (villageBound()) {
            this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        }
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, this::shouldAttack));
        targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public boolean hasAttackAnimation() {
        return true;
    }

    public int getAttackSwingTicks() {
        return 10;
    }

    public int getDeathAnimationTicks() {
        return 20;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasCustomDeathAnimation() {
        return false;
    }

    public Item getRepairItem() {
        return Items.IRON_INGOT;
    }

    public int getRepairItemHealAmount() {
        return 25;
    }

    public boolean offersFlowers() {
        return false;
    }

    public boolean villageBound() {
        return true;
    }

    public boolean doesSwingAttack() {
        return true;
    }

    public boolean canDoMeleeAttack() {
        return true;
    }

    public boolean canMoveTowardsTarget() {
        return true;
    }

    public boolean canFloatInWater() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    public boolean shouldAttack(LivingEntity entity) {
        return entity instanceof Enemy && !(entity instanceof Creeper);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        boolean result = super.doHurtTarget(target);
        attackAnimationTick = getAttackSwingTicks();
        return result;
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            attackAnimationTick = getAttackSwingTicks();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            this.attackAnimationTick--;
        }
    }

    public int getAttackAnimationTick() {
        return attackAnimationTick;
    }

    public void setAttackAnimationTick(int attackAnimationTick) {
        this.attackAnimationTick = attackAnimationTick;
    }

    @Override
    protected void tickDeath() {
        if (!hasCustomDeathAnimation()) {
            super.tickDeath();
            return;
        }

        deathTime++;
        if (deathTime >= getDeathAnimationTicks() && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, EntityEvent.POOF);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!stack.is(getRepairItem())) return InteractionResult.PASS;

        float health = this.getHealth();
        this.heal(getRepairItemHealAmount());
        if (this.getHealth() == health) {
            return InteractionResult.PASS;
        } else {
            float pitch = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0f, pitch);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
    }

    private class GolemMeleeAttackGoal extends MeleeAttackGoal {
        public GolemMeleeAttackGoal(double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(BaseGolem.this, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        public boolean canUse() {
            return canDoMeleeAttack() && super.canUse();
        }
    }

    private class GolemMoveTowardsTargetGoal extends MoveTowardsTargetGoal {
        public GolemMoveTowardsTargetGoal(double speedModifier, float within) {
            super(BaseGolem.this, speedModifier, within);
        }

        @Override
        public boolean canUse() {
            return canMoveTowardsTarget() && super.canUse();
        }
    }
}
