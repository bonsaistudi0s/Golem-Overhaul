package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.IShearable;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.entities.AdditionalBeeData;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.HoneyBlobProjectile;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;
import tech.alexnijjar.golemoverhaul.mixins.common.BeeAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoneyGolem extends BaseGolem implements RangedAttackMob, IShearable {

    public static final byte NECTAR_PARTICLES_EVENT_ID = 8;

    public static final int RANGED_ATTACK_DELAY_TICKS = 6;

    public static final EntityDataAccessor<Byte> ID_HONEY_LEVEL = SynchedEntityData.defineId(HoneyGolem.class, EntityDataSerializers.BYTE);

    private final List<BeeData> bees = new ArrayList<>();

    private int attackAnimationDelay = -1;

    public HoneyGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.ATTACK_DAMAGE, 6);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!GolemOverhaulConfig.spawnHoneyGolems || !GolemOverhaulConfig.allowSpawning) return false;
        return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_HONEY_LEVEL, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("HoneyLevel", this.getHoneyLevel());
        ListTag beeTag = new ListTag();
        for (BeeData bee : bees) {
            CompoundTag tag = new CompoundTag();
            tag.put("EntityData", bee.tag);
            tag.putInt("TicksInHive", bee.ticks);
            tag.putInt("MinOccupationTicks", bee.minOccupationTicks);
            beeTag.add(tag);
        }
        compound.put("Bees", beeTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHoneyLevel(compound.getByte("HoneyLevel"));
        ListTag beeTag = compound.getList("Bees", Tag.TAG_COMPOUND);
        for (int i = 0; i < beeTag.size(); i++) {
            CompoundTag tag = beeTag.getCompound(i);
            bees.add(new BeeData(
                tag.getCompound("EntityData"),
                tag.getInt("TicksInHive"),
                tag.getInt("MinOccupationTicks")));
        }
    }

    @Override
    public boolean canMeleeAttack() {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1, 20, 15));
    }

    public byte getHoneyLevel() {
        return this.entityData.get(ID_HONEY_LEVEL);
    }

    public void setHoneyLevel(byte honeyLevel) {
        this.entityData.set(ID_HONEY_LEVEL, honeyLevel);
    }

    public boolean isFullOfHoney() {
        return getHoneyLevel() >= 4;
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
    public int getAttackTicks() {
        return 15;
    }

    @Override
    public Item getRepairItem() {
        return Items.HONEY_BOTTLE;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 20;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (attackAnimationDelay == -1) {
            sendAttackEvent();
            attackAnimationDelay = RANGED_ATTACK_DELAY_TICKS;
        }
    }

    public void actuallyShoot(LivingEntity target) {
        if (target == null) return;
        Projectile projectile = new HoneyBlobProjectile(level(), this);
        projectile.setPos(getX(), getY(), getZ());

        double x = target.getX() - getX();
        double y = target.getY() - projectile.getY();
        double z = target.getZ() - getZ();
        double distance = Math.sqrt(x * x + z * z) * 0.2;
        projectile.shoot(x, y + distance + 0.8, z, 1.2f, 3);

        level().addFreshEntity(projectile);
        playSound(SoundEvents.SLIME_ATTACK, 1, 0.4f / (getRandom().nextFloat() * 0.4f + 0.8f));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        final int count = 2 + level.getRandom().nextInt(4);
        for (int i = 0; i < count; i++) {
            Bee bee = Objects.requireNonNull(EntityType.BEE.create(level()));
            ((AdditionalBeeData) bee).golemoverhaul$setOwner(this.getUUID());
            this.bees.add(new BeeData(bee.saveWithoutId(new CompoundTag()), 0, 2400));
        }
        this.setHoneyLevel((byte) count);
        return super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    protected void customServerAiStep() {
        if (!bees.isEmpty() && level().getRandom().nextDouble() < 0.005) {
            playSound(SoundEvents.BEEHIVE_WORK);
        }

        if (!level().isNight() && !level().isRaining()) {
            for (int i = 0; i < bees.size(); i++) {
                BeeData bee = bees.get(i);
                bee.ticks++;
                if (bee.ticks >= bee.minOccupationTicks) {
                    releaseBee(i);
                    i--;
                }
            }
        }

        if (!bees.isEmpty() && tickCount % 200 == 0) {
            this.heal(1);
        }

        attackAnimationDelay = Math.max(-1, attackAnimationDelay - 1);
        if (attackAnimationDelay == 0) {
            actuallyShoot(getTarget());
            attackAnimationDelay = -1;
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (level().isClientSide()) return;
        if (!bees.isEmpty()) {
            this.releaseAllBees();
        }
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        if (!isFullOfHoney()) return List.of();
        playSound(SoundEvents.BEEHIVE_SHEAR);

        if (!level().isClientSide()) {
            setHoneyLevel((byte) 0);
        }
        return List.of(
            new ItemStack(ModItems.HONEY_BLOB.get(), 5 + level.random.nextInt(8)),
            new ItemStack(Items.HONEYCOMB, 3)
        );
    }

    @Override
    public boolean isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        return isFullOfHoney();
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == NECTAR_PARTICLES_EVENT_ID) {
            for (int i = 0; i < 8; ++i) {
                level().addParticle(ParticleTypes.FALLING_NECTAR,
                    getX() + getRandom().nextGaussian() * 0.25,
                    getY() + 0.5,
                    getZ() + getRandom().nextGaussian() * 0.25,
                    0, 0, 0);
            }
        }
    }

    public void healFromNectar() {
        this.heal(5);
        level().broadcastEntityEvent(this, NECTAR_PARTICLES_EVENT_ID);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!bees.isEmpty() && source.getEntity() instanceof LivingEntity entity) {
            if (entity instanceof Player player && player.isCreative()) return super.hurt(source, amount);
            this.releaseAllBees().forEach(bee -> bee.setTarget(entity));
        }
        return super.hurt(source, amount);
    }

    public boolean canPutBee() {
        return bees.size() < 5;
    }

    public void putBee(Bee bee) {
        if (!canPutBee()) return;
        ((AdditionalBeeData) bee).golemoverhaul$setOwner(this.getUUID());
        bees.add(new BeeData(bee.saveWithoutId(new CompoundTag()), 0, 2400));
        if (bee.hasNectar() && !isFullOfHoney()) {
            setHoneyLevel((byte) (getHoneyLevel() + 1));
            if (getHealth() < getMaxHealth()) {
                heal(10);
                level().broadcastEntityEvent(this, NECTAR_PARTICLES_EVENT_ID);
            }
        }
        bee.dropOffNectar();
        bee.discard();
        playSound(SoundEvents.BEEHIVE_ENTER);
    }

    private Bee releaseBee(int index) {
        BeeData data = bees.get(index);
        Bee bee = Objects.requireNonNull(EntityType.BEE.create(level()));
        bee.load(data.tag);
        bee.setPos(getX(), getY(), getZ());
        bee.dropOffNectar();
        bee.setHealth(bee.getMaxHealth());
        level().addFreshEntity(bee);
        bee.setStayOutOfHiveCountdown(400);
        playSound(SoundEvents.BEEHIVE_EXIT);
        bees.remove(index);
        return bee;
    }

    public List<Bee> releaseAllBees() {
        List<Bee> removedBees = new ArrayList<>();
        int size = this.bees.size();
        for (int i = 0; i < size; i++) {
            Bee bee = releaseBee(0);
            ((BeeAccessor) bee).setRemainingCooldownBeforeLocatingNewFlower(400);
            removedBees.add(bee);
        }
        return removedBees;
    }

    private static final class BeeData {

        private final CompoundTag tag;
        private int ticks;
        private final int minOccupationTicks;

        private BeeData(CompoundTag tag, int ticks, int minOccupationTicks) {
            this.tag = tag;
            this.ticks = ticks;
            this.minOccupationTicks = minOccupationTicks;
        }
    }
}