package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.Locale;

public class SlimeGolem extends BaseGolem {

    private static final EntityDataAccessor<Byte> ID_SIZE = SynchedEntityData.defineId(SlimeGolem.class, EntityDataSerializers.BYTE);
    public static final EntityDimensions SMALL_DIMENSIONS = EntityDimensions.scalable(0.5f, 0.5f);

    @NotNull
    private RawAnimation attackArm = this.getRandomArmAnimation();

    public SlimeGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, Size.LARGE.health)
            .add(Attributes.MOVEMENT_SPEED, Size.LARGE.speed)
            .add(Attributes.ATTACK_KNOCKBACK, Size.LARGE.knockback)
            .add(Attributes.ATTACK_DAMAGE, Size.LARGE.attackDamage);
    }

    @Override
    public AnimationController<?> getMovementController() {
        return super.getMovementController()
            .setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(), this.getStepSound(), getSoundSource(), 0.3f, 1, false));
    }

    public static boolean checkSlimeSpawnRules(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!GolemOverhaulConfig.spawnSlimeGolems || !GolemOverhaulConfig.allowSpawning) return false;

        if (MobSpawnType.isSpawner(spawnType)) {
            return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
        }

        if (level.getBiome(pos).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS)
            && pos.getY() > 50
            && pos.getY() < 70
            && random.nextFloat() < 0.5f
            && random.nextFloat() < level.getMoonBrightness()
            && level.getMaxLocalRawBrightness(pos) <= random.nextInt(8)) {
            return checkMobSpawnRules(type, level, spawnType, pos, random);
        }

        if (!(level instanceof WorldGenLevel)) return false;

        // Slime chunk spawning taken from Slime#checkSlimeSpawnRules
        ChunkPos chunkpos = new ChunkPos(pos);
        boolean isSlimeChunk = WorldgenRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, ((WorldGenLevel) level).getSeed(), 987234911L).nextInt(10) == 0;
        if (random.nextInt(10) == 0 && isSlimeChunk && pos.getY() < 40) {
            return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
        }

        return false;
    }

    @Override
    public PlayState getAttackAnimation(AnimationState<? extends BaseGolem> state) {
        return state.setAndContinue(this.attackArm);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_SIZE, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Size", this.getSize().name().toLowerCase(Locale.ROOT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSize(Size.valueOf(compound.getString("Size").toUpperCase(Locale.ROOT)), false);
    }

    public Size getSize() {
        return Size.values()[this.entityData.get(ID_SIZE)];
    }

    public void setSize(Size size, boolean resetHealth) {
        this.entityData.set(ID_SIZE, (byte) size.ordinal());

        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(size.health);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(size.speed);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(size.knockback);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(size.attackDamage);
        if (resetHealth) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = size.xpReward;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return getSize().isLarge() ? SoundEvents.SLIME_HURT : SoundEvents.SLIME_HURT_SMALL;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return getSize().isLarge() ? SoundEvents.SLIME_DEATH : SoundEvents.SLIME_DEATH_SMALL;
    }

    private SoundEvent getStepSound() {
        return getSize().isLarge() ? SoundEvents.SLIME_HURT : SoundEvents.SLIME_HURT_SMALL;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (super.doHurtTarget(target)) {
            this.playSound(getSize().isLarge() ? SoundEvents.SLIME_SQUISH : SoundEvents.SLIME_SQUISH_SMALL, 1, 1);
            return true;
        }
        return false;
    }

    @Override
    public int getAttackTicks() {
        return 22;
    }

    @Override
    public Item getRepairItem() {
        return Items.SLIME_BALL;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 10;
    }

    /**
     * Copy of Slime#remove but modified for the Slime Golem.
     */
    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide && this.getSize().isLarge() && this.isDeadOrDying()) {
            Component name = this.getCustomName();
            boolean noAi = this.isNoAi();
            float width = this.getDimensions(this.getPose()).width();
            float halfWidth = width / 2;
            int amount = 2 + this.random.nextInt(3);

            for (int i = 0; i < amount; ++i) {
                float x = ((float) (i % 2) - 0.5f) * halfWidth;
                float z = ((float) (i / 2) - 0.5f) * halfWidth;
                SlimeGolem slime = ModEntityTypes.SLIME_GOLEM.get().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }

                    slime.setCustomName(name);
                    slime.setNoAi(noAi);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(Size.SMALL, true);
                    slime.moveTo(this.getX() + x, this.getY() + 0.5, this.getZ() + z, this.random.nextFloat() * 360, 0);
                    this.level().addFreshEntity(slime);
                }
            }
        }

        super.remove(reason);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setSize(level.getRandom().nextBoolean() ? Size.LARGE : Size.SMALL, true);
        return super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return getSize().isLarge() ? super.getDefaultDimensions(pose) : SMALL_DIMENSIONS;
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == ATTACK_EVENT_ID) {
            this.attackArm = this.getRandomArmAnimation();
        }
    }

    private RawAnimation getRandomArmAnimation() {
        return getRandom().nextBoolean() ? ConstantAnimations.ATTACK_RIGHT : ConstantAnimations.ATTACK_LEFT;
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(0.5f, 0, 0.5f);
    }

    public enum Size {
        LARGE(50, 6, 0.24, 2, 0),
        SMALL(20, 3, 0.19, 1, 6),
        ;

        private final int health;
        private final int attackDamage;
        private final double speed;
        private final double knockback;
        private final int xpReward;

        Size(int health, int attackDamage, double speed, double knockback, int xpReward) {
            this.health = health;
            this.attackDamage = attackDamage;
            this.speed = speed;
            this.knockback = knockback;
            this.xpReward = xpReward;
        }

        public boolean isLarge() {
            return this == LARGE;
        }
    }
}