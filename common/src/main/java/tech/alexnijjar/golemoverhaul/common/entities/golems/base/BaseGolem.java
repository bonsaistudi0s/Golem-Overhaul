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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;

public abstract class BaseGolem extends AbstractGolem implements GeoEntity {

    public static final byte ATTACK_EVENT_ID = 4;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final BaseGolemMeleeAttackGoal meleeAttackGoal = new BaseGolemMeleeAttackGoal(this, 1, true);
    private final HurtByTargetGoal hurtByTargetGoal = new HurtByTargetGoal(this, BaseGolem.class);
    private final NearestAttackableTargetGoal<Mob> attackTargetGoal = new NearestAttackableTargetGoal<>(this, Mob.class,
            5, true, false, this::shouldAttack);

    protected int attackAnimationTicks;
    protected int attackDelayTicks = -1;

    private final boolean canBeCreatedByPlayer;
    private final boolean canSpawnNaturally;

    protected boolean isPlayerCreated = false;

    protected BaseGolem(EntityType<? extends AbstractGolem> type, Level level, boolean canBeCreatedByPlayer,
                        boolean canSpawnNaturally) {
        super(type, level);
        this.canBeCreatedByPlayer = canBeCreatedByPlayer;
        this.canSpawnNaturally = canSpawnNaturally;
        if (this.canBeCreatedByPlayer && !this.canSpawnNaturally) {
            this.isPlayerCreated = true;
        }
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

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target)
                && !(target instanceof BaseGolem || target instanceof IronGolem || target instanceof SnowGolem)
                && !(this.isPlayerCreated() && target instanceof Player);
    }

    public final void updateAttackGoals() {
        this.goalSelector.removeGoal(this.meleeAttackGoal);
        this.targetSelector.removeGoal(this.hurtByTargetGoal);
        this.targetSelector.removeGoal(this.attackTargetGoal);

        if (canMeleeAttack()) {
            this.goalSelector.addGoal(2, this.meleeAttackGoal);
        }
        if (canTarget()) {
            this.targetSelector.addGoal(1, this.hurtByTargetGoal);
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

    public IronGolem.Crackiness getCrackiness() {
        float fraction = this.getHealth() / this.getMaxHealth();
        if (fraction > 0.66)
            return IronGolem.Crackiness.NONE;
        if (fraction > 0.33)
            return IronGolem.Crackiness.MEDIUM;
        return IronGolem.Crackiness.HIGH;
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
        this.remove(RemovalReason.KILLED);
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
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    public boolean isPlayerCreated() {
        return this.isPlayerCreated;
    }

    public void setPlayerCreated() {
        this.isPlayerCreated = true;
    }

    private boolean needsToPersistPlayerCreatedFlag() {
        return this.canBeCreatedByPlayer && this.canSpawnNaturally;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.needsToPersistPlayerCreatedFlag()) {
            compound.putBoolean("PlayerCreated", this.isPlayerCreated);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (this.needsToPersistPlayerCreatedFlag()) {
            if (compound.contains("PlayerCreated")) {
                this.isPlayerCreated = compound.getBoolean("PlayerCreated");
            } else {
                this.isPlayerCreated = false;
            }
        }
        this.updateAttackGoals();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                                                  @Nullable CompoundTag dataTag) {
        this.updateAttackGoals();
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    // This replicates the exact melee reach check from 1.21

    private static final double DEFAULT_ATTACK_REACH = Math.sqrt(2.04F) - (double) 0.6F;

    protected AABB getAttackBoundingBox() {
        var vehicle = this.getVehicle();
        AABB aabb;
        if (vehicle != null) {
            var vehicleAabb = vehicle.getBoundingBox();
            var selfAabb = this.getBoundingBox();
            aabb = new AABB(
                    Math.min(selfAabb.minX, vehicleAabb.minX), selfAabb.minY, Math.min(selfAabb.minZ, vehicleAabb.minZ),
                    Math.max(selfAabb.maxX, vehicleAabb.maxX), selfAabb.maxY, Math.max(selfAabb.maxZ, vehicleAabb.maxZ)
            );
        } else {
            aabb = this.getBoundingBox();
        }

        return aabb.inflate(DEFAULT_ATTACK_REACH, 0.0D, DEFAULT_ATTACK_REACH);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return this.getAttackBoundingBox().intersects(entity.getBoundingBox());
    }

    private static class BaseGolemMeleeAttackGoal extends MeleeAttackGoal {

        public BaseGolemMeleeAttackGoal(BaseGolem mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (mob.isWithinMeleeAttackRange(enemy) && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(enemy);
            }
        }
    }
}
