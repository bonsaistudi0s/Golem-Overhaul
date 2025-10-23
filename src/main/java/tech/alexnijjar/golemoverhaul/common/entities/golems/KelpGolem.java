package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;
import tech.alexnijjar.golemoverhaul.common.recipes.SingleEntityInput;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;
import tech.alexnijjar.golemoverhaul.common.utils.ModUtils;

public class KelpGolem extends BaseGolem {

    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    private static final EntityDataAccessor<Boolean> ID_CHARGED = SynchedEntityData.defineId(KelpGolem.class,
            EntityDataSerializers.BOOLEAN);

    public KelpGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 14;
        setPathfindingMalus(PathType.WATER, 0);
        this.moveControl = new KelpGolemMoveControl();
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.085)
                .add(Attributes.ATTACK_DAMAGE, 8);
    }

    public static void trySpawnGolem(Level level, BlockPos pos) {
        GolemConstructionRecipe recipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.GOLEM_CONSTRUCTION.get(),
                new SingleEntityInput(ModEntityTypes.KELP_GOLEM.get()), level).orElseThrow().value();
        BlockPattern.BlockPatternMatch pattern = recipe.createPattern().find(level, pos);
        if (pattern == null)
            return;
        KelpGolem golem = ModEntityTypes.KELP_GOLEM.get().create(level);
        if (golem == null)
            return;
        ModUtils.spawnGolemInWorld(level, pattern, golem, pattern.getBlock(1, 2, 0).getPos());
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

        controllers.add(new AnimationController<>(this, "spin_controller", 0, state -> {
            if (!isCharged()) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }
            return state.setAndContinue(ConstantAnimations.SPIN);
        }));
    }

    @Override
    public AnimationController<?> getMovementController() {
        return super.getMovementController()
                .setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(),
                        ModSoundEvents.KELP_GOLEM_STEP.get(), getSoundSource(), 1, 1, false));
    }

    @Override
    public PlayState handleMovementController(AnimationState<BaseGolem> state) {
        boolean moving = state.getLimbSwingAmount() > 0.05 || state.getLimbSwingAmount() < -0.05;

        if (isInWater()) {
            state.getController().setAnimation(ConstantAnimations.SWIM);
            return PlayState.CONTINUE;
        }
        state.getController().setAnimation(moving ? ConstantAnimations.WALK
                : isInWater() ? ConstantAnimations.IDLE_WATER : ConstantAnimations.IDLE);

        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_CHARGED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Charged", this.isCharged());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCharged(compound.getBoolean("Charged"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0,
                new NearestAttackableTargetGoal<>(this, Mob.class, 3, true, false, this::shouldAttack));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1, 40));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean isCharged() {
        return this.entityData.get(ID_CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(ID_CHARGED, charged);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.KELP_GOLEM_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public boolean canFloatInWater() {
        return false;
    }

    @Override
    public Item getRepairItem() {
        return Items.KELP;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 5;
    }

    @Override
    public SoundEvent getRepairSound() {
        return ModSoundEvents.KELP_GOLEM_STEP.get();
    }

    @Override
    public int getAttackTicks() {
        return 18;
    }

    @Override
    public int getAttackDelayTicks() {
        return 6;
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    public void baseTick() {
        int airSupply = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(airSupply);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount % 60 == 0) {
            if (this.inConduitRange()) {
                this.setCharged(true);
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, true, true));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, true));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 1, true, true));
            } else {
                this.setCharged(false);
            }
        }
    }

    private boolean inConduitRange() {
        return this.hasEffect(MobEffects.CONDUIT_POWER);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(0.01f, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide()) {
            if (this.isEffectiveAi() && level().getBlockState(blockPosition().above(2)).is(Blocks.WATER)) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
        super.updateSwimming();
    }

    protected void handleAirSupply(int airSupply) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(airSupply - 1);
            if (this.getAirSupply() == -200) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(2.0, 0, 2.0);
    }

    private class KelpGolemMoveControl extends MoveControl {

        KelpGolemMoveControl() {
            super(KelpGolem.this);
        }

        @Override
        public void tick() {
            if (!isInWater()) {
                super.tick();
                return;
            }

            LivingEntity livingEntity = getTarget();
            if (livingEntity != null && livingEntity.getY() > getY()) {
                setDeltaMovement(getDeltaMovement().add(0, 0.002, 0));
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || getNavigation().isDone()) {
                setSpeed(0);
                return;
            }

            double x = this.wantedX - getX();
            double y = this.wantedY - getY();
            double z = this.wantedZ - getZ();
            double distance = Math.sqrt(x * x + y * y + z * z);
            y /= distance;
            float h = (float) (Mth.atan2(z, x) * 57.3) - 90;
            setYRot(this.rotlerp(getYRot(), h, 90));
            yBodyRot = getYRot();
            float speed = (float) (this.speedModifier * getAttributeValue(Attributes.MOVEMENT_SPEED));
            speed = Mth.lerp(0.125f, getSpeed(), speed);
            setSpeed(speed);
            speed *= 20;
            setDeltaMovement(getDeltaMovement().add(speed * x * 0.005, speed * y * 0.025, speed * z * 0.005));
        }
    }
}
