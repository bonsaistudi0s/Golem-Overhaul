package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;

import java.util.List;

public class BarrelGolem extends BaseGolem {

    private static final EntityDataAccessor<Boolean> ID_OPEN = SynchedEntityData.defineId(BarrelGolem.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ID_DAY_START_TICKS = SynchedEntityData.defineId(BarrelGolem.class,
            EntityDataSerializers.INT);

    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(2, 0, 2);

    public static final ResourceKey<LootTable> BARTERING_LOOT = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "gameplay/barrel_golem_bartering"));

    public static final byte CHANGE_STATE_EVENT_ID = 8;
    public static final byte BARTER_EVENT_ID = 9;

    public static final int WAKE_UP_TICKS = 62;
    public static final int BARTERING_TICKS = 78;

    private int changeStateTicks;
    private int barteringTicks;

    private int openUpTicks;

    @Nullable
    private Player barteringTarget;

    public BarrelGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 10;
        setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.MOVEMENT_SPEED, 0.31)
                .add(Attributes.ARMOR, 6)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> type, LevelAccessor level,
                                             MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!GolemOverhaulConfig.spawnBarrelGolems || !GolemOverhaulConfig.allowSpawning)
            return false;
        return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);

        controllers.add(new AnimationController<>(this, "open_controller", state -> {
            if (isBartering()) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }

            if (isWakingUp()) {
                return state.setAndContinue(ConstantAnimations.WAKE_UP);
            }

            if (this.openUpTicks > 0) {
                return state.setAndContinue(ConstantAnimations.OPEN);
            }

            if (level().isNight() || !this.isOpen()) {
                return state.setAndContinue(ConstantAnimations.HIDE);
            }

            state.resetCurrentAnimation();
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "barter_controller", 5, state -> {
            if (this.isBartering()) {
                return state.setAndContinue(ConstantAnimations.BARTER);
            }
            state.resetCurrentAnimation();
            return PlayState.STOP;
        }).setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(),
                ModSoundEvents.BARREL_GOLEM_BARTER.get(), getSoundSource(), 1, 1, false)));
    }

    @Override
    public PlayState getMoveAnimation(AnimationState<BaseGolem> state, boolean moving) {
        if (!this.isOpen())
            return PlayState.STOP;
        if (isBartering()) {
            state.resetCurrentAnimation();
            return PlayState.STOP;
        }

        return state.setAndContinue(
                moving ? ConstantAnimations.WALK : isOpen() ? ConstantAnimations.IDLE : ConstantAnimations.IDLE_HIDDEN);
    }

    @Override
    public PlayState getAttackAnimation(AnimationState<? extends BaseGolem> state) {
        return PlayState.STOP;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_OPEN, true);
        builder.define(ID_DAY_START_TICKS, WAKE_UP_TICKS);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive() && !this.isOpen();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Open", this.isOpen());
        compound.putInt("ChangeStateTicks", this.changeStateTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOpen(compound.getBoolean("Open"), false);
        this.changeStateTicks = compound.getInt("ChangeStateTicks");
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BarrelGolemPanicGoal(1));
        this.goalSelector.addGoal(2, new BarrelGolemFindNearestEmeraldGoal());
    }

    @Override
    public boolean canTarget() {
        return false;
    }

    public boolean isOpen() {
        return this.entityData.get(ID_OPEN);
    }

    public boolean isBartering() {
        return this.barteringTicks > 0;
    }

    public int getBarteringTicks() {
        return this.barteringTicks;
    }

    public int getDayStartTicks() {
        return this.entityData.get(ID_DAY_START_TICKS);
    }

    public void setDayStartTicks(int ticks) {
        this.entityData.set(ID_DAY_START_TICKS, ticks);
    }

    public void setOpen(boolean open, boolean playSound) {
        if (!open && isOnFire())
            return;
        if (!level().isClientSide() && playSound && this.isOpen() != open) {
            if (open) {
                playSound(SoundEvents.BARREL_OPEN);
            } else {
                playSound(SoundEvents.BARREL_CLOSE);
            }
        }

        this.entityData.set(ID_OPEN, open);
        openUpTicks = open ? 10 : 0;

        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(open ? 0 : 1);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SHULKER_HURT_CLOSED;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR;
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == CHANGE_STATE_EVENT_ID) {
            this.changeStateTicks = this.getRandomChangeInterval();
        } else if (id == BARTER_EVENT_ID) {
            this.barteringTicks = BARTERING_TICKS;
        }
    }

    @Override
    public boolean canRepair(ItemStack stack) {
        return stack.is(ItemTags.PLANKS);
    }

    @Override
    public float getRepairItemHealAmount() {
        return 5;
    }

    @Override
    public SoundEvent getRepairSound() {
        return SoundEvents.AXE_STRIP;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor,
                                                  DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType,
                                                  @Nullable SpawnGroupData spawnGroupData) {
        setOpen(level().getSkyDarken() < 4, false);
        changeStateTicks = this.getRandomChangeInterval();
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isOpen()) {
            if (source.getDirectEntity() instanceof AbstractArrow arrow && !arrow.isOnFire()) {
                return false;
            }

            if (!source.is(DamageTypeTags.IS_FIRE)) {
                amount /= 10;
            } else {
                amount *= 2;
                if (!this.isOpen())
                    this.setOpen(true, true);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isBartering() || !this.isOpen();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!level().isClientSide()) {
            if (level().getSkyDarken() < 4) {
                setDayStartTicks(getDayStartTicks() + 1);
            } else {
                setDayStartTicks(0);
            }
        }

        this.changeStateTicks = Math.max(0, this.changeStateTicks - 1);
        this.barteringTicks = Math.max(0, this.barteringTicks - 1);
        this.openUpTicks = Math.max(0, this.openUpTicks - 1);

        if (!level().isClientSide()) {
            if (this.changeStateTicks == 0 && level().isDay() && !isWakingUp()) {
                this.setOpen(!this.isOpen(), true);
                this.changeStateTicks = this.getRandomChangeInterval();
                this.level().broadcastEntityEvent(this, CHANGE_STATE_EVENT_ID);
            }

            if (!isOpen() || isBartering()) {
                this.navigation.stop();
            }

            if (!level().isDay() && isOpen()) {
                this.setOpen(false, true);
            } else if (finishedWakeUp() && !isOpen()) {
                this.setOpen(true, false);
            }

            if (this.barteringTicks == 24) {
                throwItems(getBarterResponseItems());
                ExperienceOrb orb = new ExperienceOrb(level(), this.getX(), this.getY(), this.getZ(),
                        this.getRandom().nextInt(2) + 2);
                level().addFreshEntity(orb);
                if (this.barteringTarget != null) {
                    Vec3 targetPos = this.barteringTarget.position().subtract(this.position());
                    targetPos = targetPos.normalize().multiply(0.3, 0.3, 0.3);
                    orb.setDeltaMovement(targetPos);
                }
                this.barteringTarget = null;
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                this.setXRot(0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (!this.isOpen() || this.isBartering()) {
                this.yBodyRot = this.yHeadRot;
            }
        } else {
            ItemStack stack = getMainHandItem();
            if (stack.is(Items.EMERALD) && !isBartering() && isOpen()) {
                this.barter();
            }
        }
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        ItemStack equippedStack = this.equipItemIfPossible(stack.copy());
        if (!equippedStack.isEmpty()) {
            this.onItemPickup(itemEntity);
            this.take(itemEntity, 1);
            stack.shrink(1);
            if (stack.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setExtendedLifetime();
            }
        }

        if (itemEntity.getOwner() instanceof Player player) {
            this.barteringTarget = player;
        }
    }

    private boolean canBarterWith(ItemStack stack) {
        return stack.is(Items.EMERALD) && getMainHandItem().isEmpty() && isOpen() && !isBartering();
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && canBarterWith(stack);
    }

    @Override
    protected @NotNull Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    public boolean isWakingUp() {
        int ticks = getDayStartTicks();
        return ticks > 0 && ticks < WAKE_UP_TICKS;
    }

    private boolean finishedWakeUp() {
        return getDayStartTicks() == WAKE_UP_TICKS;
    }

    /**
     * @return 40-80 seconds.
     */
    private int getRandomChangeInterval() {
        return 800 + this.getRandom().nextInt(800);
    }

    public void barter() {
        if (this.isBartering())
            return;
        this.level().broadcastEntityEvent(this, BARTER_EVENT_ID);
        this.changeStateTicks = this.getRandomChangeInterval();
        this.barteringTicks = BARTERING_TICKS;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level().isClientSide() && canBarterWith(stack)) {
            this.barteringTarget = player;
            this.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private List<ItemStack> getBarterResponseItems() {
        if (level() instanceof ServerLevel level) {
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(BARTERING_LOOT);
            return lootTable.getRandomItems(new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, this).create(LootContextParamSets.PIGLIN_BARTER));
        }
        return List.of();
    }

    private void throwItems(List<ItemStack> stacks) {
        if (this.barteringTarget != null) {
            throwItemsTowardPlayer(this.barteringTarget, stacks);
        } else {
            throwItemsTowardRandomPos(stacks);
        }
    }

    private void throwItemsTowardRandomPos(List<ItemStack> stacks) {
        throwItemsTowardPos(stacks, getRandomNearbyPos());
    }

    private void throwItemsTowardPlayer(Player player, List<ItemStack> stacks) {
        throwItemsTowardPos(stacks, player.position());
    }

    private void throwItemsTowardPos(List<ItemStack> stacks, Vec3 pos) {
        for (var stack : stacks) {
            BehaviorUtils.throwItem(this, stack, pos.add(0.0, 1.0, 0.0));
        }
    }

    private Vec3 getRandomNearbyPos() {
        Vec3 vec3 = LandRandomPos.getPos(this, 4, 2);
        return vec3 == null ? this.position() : vec3;
    }

    private class BarrelGolemPanicGoal extends PanicGoal {

        public BarrelGolemPanicGoal(double speedModifier) {
            super(BarrelGolem.this, speedModifier);
        }

        @Override
        public boolean canUse() {
            return isOpen() && super.canUse();
        }

        @Override
        public void stop() {
            super.stop();
            setOpen(false, true);
        }
    }

    private class BarrelGolemFindNearestEmeraldGoal extends Goal {

        private ItemEntity nearest;

        @Override
        public boolean canUse() {
            if (isOpen() && !isBartering()) {
                ItemEntity nearest = level()
                        .getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(16),
                                stack -> wantsToPickUp(stack.getItem()))
                        .stream()
                        .findFirst()
                        .orElse(null);
                if (nearest != null) {
                    this.nearest = nearest;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return isOpen() && !isBartering() && this.nearest != null && !nearest.isRemoved();
        }

        @Override
        public void tick() {
            navigation.moveTo(nearest, 0.7);
            lookControl.setLookAt(nearest, 30, 30);
        }
    }
}
