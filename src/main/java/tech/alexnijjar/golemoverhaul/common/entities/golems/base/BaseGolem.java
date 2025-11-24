package tech.alexnijjar.golemoverhaul.common.entities.golems.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;

public abstract class BaseGolem extends AbstractGolem implements GeoEntity {

    public static final byte ATTACK_EVENT_ID = 4;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1, true);
    private final HurtByTargetGoal hurtByTargetGoal = new HurtByTargetGoal(this, BaseGolem.class);
    private final NearestAttackableTargetGoal<Mob> attackTargetGoal = new NearestAttackableTargetGoal<>(this, Mob.class,
            5, true, false, this::shouldAttack);

    protected int attackAnimationTicks;
    protected int attackDelayTicks = -1;

    protected BaseGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.updateAttackGoals();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(this.getMovementController());

        controllers.add(new AnimationController<>(this, "attack_controller", 0, state -> {
            if (!hasAttackAnimation())
                return PlayState.STOP;
            if (attackAnimationTicks == 0) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }
            return getAttackAnimation(state);
        }));
    }

    public AnimationController<?> getMovementController() {
        return new AnimationController<>(this, this::handleMovementController);
    }

    public PlayState handleMovementController(AnimationState<BaseGolem> state) {
        boolean moving = state.getLimbSwingAmount() > 0.05 || state.getLimbSwingAmount() < -0.05;
        return getMoveAnimation(state, moving);
    }

    public PlayState getMoveAnimation(AnimationState<BaseGolem> state, boolean moving) {
        return state.setAndContinue(moving ? ConstantAnimations.WALK : ConstantAnimations.IDLE);
    }

    public PlayState getAttackAnimation(AnimationState<? extends BaseGolem> state) {
        return state.setAndContinue(ConstantAnimations.ATTACK);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void registerGoals() {
        if (canFloatInWater()) {
            goalSelector.addGoal(0, new FloatGoal(this));
        }
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public final void updateAttackGoals() {
        this.goalSelector.removeGoal(this.meleeAttackGoal);
        this.targetSelector.removeGoal(this.hurtByTargetGoal);
        this.targetSelector.removeGoal(this.attackTargetGoal);

        if (canMeleeAttack()) {
            this.goalSelector.addGoal(1, this.meleeAttackGoal);
        }
        if (canTarget()) {
            this.targetSelector.addGoal(2, this.hurtByTargetGoal);
            this.targetSelector.addGoal(3, this.attackTargetGoal);
        }
    }

    public boolean canMeleeAttack() {
        return canTarget();
    }

    public boolean canTarget() {
        return true;
    }

    public boolean shouldAttack(LivingEntity entity) {
        return entity instanceof Enemy && !(entity instanceof Creeper);
    }

    public Crackiness.Level getCrackiness() {
        float fraction = this.getHealth() / this.getMaxHealth();
        if (fraction > 0.66)
            return Crackiness.Level.NONE;
        if (fraction > 0.33)
            return Crackiness.Level.MEDIUM;
        return Crackiness.Level.HIGH;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (isAttacking())
            return false;
        this.startAttacking();
        this.sendAttackEvent();
        return hasDelayedAttack() || super.doHurtTarget(target);
    }

    public void startAttacking() {
        if (isAttacking())
            return;
        this.attackAnimationTicks = getAttackTicks();
        if (hasDelayedAttack()) {
            this.attackDelayTicks = getAttackDelayTicks();
        }
    }

    public boolean isAttacking() {
        return attackAnimationTicks > 0 || attackDelayTicks > 0;
    }

    public void sendAttackEvent() {
        level().broadcastEntityEvent(this, ATTACK_EVENT_ID);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == ATTACK_EVENT_ID) {
            this.startAttacking();
        }
    }

    /**
     * Attacks the target after a specified delay.
     */
    private void tickAttackDelay() {
        LivingEntity target = getTarget();
        if (this.attackDelayTicks == 0) {
            if (target != null) {
                if (this.isWithinMeleeAttackRange(target)) {
                    this.actuallyAttackAfterDelay(target);
                }
            }
            this.performAdditionalAttacks(target);
            this.attackDelayTicks = -1;
        }
    }

    public void actuallyAttackAfterDelay(LivingEntity target) {
        super.doHurtTarget(target);
    }

    public void performAdditionalAttacks(LivingEntity target) {
    }

    public boolean canFloatInWater() {
        return true;
    }

    public boolean hasAttackAnimation() {
        return true;
    }

    public int getAttackTicks() {
        return 0;
    }

    public int getAttackDelayTicks() {
        return 0;
    }

    public final boolean hasDelayedAttack() {
        return getAttackDelayTicks() > 0;
    }

    public Item getRepairItem() {
        return Items.AIR;
    }

    public abstract float getRepairItemHealAmount();

    public boolean canRepair(ItemStack stack) {
        return stack.is(getRepairItem());
    }

    public SoundEvent getRepairSound() {
        return SoundEvents.IRON_GOLEM_REPAIR;
    }

    public final void removeAndAddDeathParticles() {
        this.level().broadcastEntityEvent(this, (byte) 60);
        this.remove(Entity.RemovalReason.KILLED);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        attackAnimationTicks = Math.max(0, attackAnimationTicks - 1);
        attackDelayTicks = Math.max(-1, attackDelayTicks - 1);
        if (hasDelayedAttack()) {
            tickAttackDelay();
        }
    }

    /**
     * A modified version of IronGolem#mobInteract that takes a custom repair entity
     * and heal amount.
     */
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!canRepair(stack))
            return InteractionResult.PASS;

        float health = this.getHealth();
        this.heal(getRepairItemHealAmount());
        if (this.getHealth() == health)
            return InteractionResult.PASS;

        float pitch = 1 + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
        this.playSound(getRepairSound(), 1, pitch);
        stack.consume(1, player);
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.updateAttackGoals();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
            MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.updateAttackGoals();
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }
}
