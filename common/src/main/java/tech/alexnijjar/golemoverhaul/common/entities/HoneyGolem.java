package tech.alexnijjar.golemoverhaul.common.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.HoneyBlobProjectile;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;

import java.util.ArrayList;
import java.util.List;

public class HoneyGolem extends BaseGolem implements RangedAttackMob, Shearable {
    public static final EntityDataAccessor<Byte> HONEY_LEVEL = SynchedEntityData.defineId(HoneyGolem.class, EntityDataSerializers.BYTE);
    private final List<BeeData> bees = new ArrayList<>();

    public HoneyGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 8;
        setMaxUpStep(0);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HONEY_LEVEL, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.35, 20, 7.5f));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("HoneyLevel", honeyLevel());
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
        setHoneyLevel(compound.getByte("HoneyLevel"));
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
    public boolean hurt(DamageSource source, float amount) {
        if (!bees.isEmpty() && source.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player p && p.isCreative()) return super.hurt(source, amount);
            for (int i = 0; i < bees.size(); i++) {
                var bee = removeBee();
                if (bee != null) {
                    bee.setTarget(livingEntity);
                }
            }
        }
        return super.hurt(source, amount);
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
    public boolean playIronGolemStepSound() {
        return false;
    }

    @Override
    public SoundEvent getDamageSound() {
        return SoundEvents.CORAL_BLOCK_BREAK;
    }

    @Override
    public int getAttackSwingTicks() {
        return 15;
    }

    @Override
    public boolean canDoMeleeAttack() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 2) {
            for (int i = 0; i < 8; ++i) {
                level().addParticle(ParticleTypes.FALLING_NECTAR,
                    getX() + getRandom().nextGaussian() * 0.25,
                    getY() + 0.5,
                    getZ() + getRandom().nextGaussian() * 0.25,
                    0.0, 0.0, 0.0);
            }
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.SHEARS)) {
            if (!this.level().isClientSide) {
                shear(SoundSource.PLAYERS);
                itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            } else return InteractionResult.CONSUME;
        } else return super.mobInteract(player, hand);
    }

    @Override
    public void shear(SoundSource source) {
        if (!isFullOfHoney()) return;
        playSound(SoundEvents.BEEHIVE_SHEAR, 1.0f, 1.0f);
        if (!level().isClientSide()) {
            BehaviorUtils.throwItem(this,
                new ItemStack(ModItems.HONEY_BLOB.get(),
                    level().random.nextBoolean() ? 2 : 1),
                Vec3.ZERO);
            setHoneyLevel((byte) 0);
        }
    }

    @Override
    public boolean readyForShearing() {
        return isFullOfHoney();
    }

    public byte honeyLevel() {
        return entityData.get(HONEY_LEVEL);
    }

    public void setHoneyLevel(byte honeyLevel) {
        entityData.set(HONEY_LEVEL, honeyLevel);
    }

    public boolean isFullOfHoney() {
        return honeyLevel() >= 4;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        if (!bees.isEmpty() && level().getRandom().nextDouble() < 0.005) {
            playSound(SoundEvents.BEEHIVE_WORK, 1.0f, 1.0f);
        }

        if (!level().isNight() && !level().isRaining()) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < bees.size(); i++) {
                BeeData bee = bees.get(i);
                if (bee.ticks >= bee.minOccupationTicks) {
                    removeBee();
                } else {
                    bee.ticks++;
                }
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (level().isClientSide()) return;
        for (int i = 0; i < bees.size(); i++) removeBee();
    }

    public boolean canPutBee() {
        return bees.size() < 3;
    }

    public void putBee(Bee bee) {
        if (!canPutBee()) return;
        CompoundTag tag = new CompoundTag();
        bees.add(new BeeData(bee.saveWithoutId(tag), 0, 2400));
        if (bee.hasNectar() && !isFullOfHoney()) {
            setHoneyLevel((byte) (honeyLevel() + 1));
            if (getHealth() < getMaxHealth()) {
                heal(10);
                level().broadcastEntityEvent(this, (byte) 2);
            }
        }
        bee.dropOffNectar();
        bee.discard();
        playSound(SoundEvents.BEEHIVE_ENTER, 1.0f, 1.0f);
    }

    @Nullable
    public Bee removeBee() {
        if (bees.isEmpty()) return null;
        BeeData data = bees.remove(0);
        Bee bee = EntityType.BEE.create(level());
        if (bee == null) return null;
        bee.load(data.tag);
        bee.setPos(getX(), getY(), getZ());
        bee.dropOffNectar();
        level().addFreshEntity(bee);
        bee.setStayOutOfHiveCountdown(400);
        playSound(SoundEvents.BEEHIVE_EXIT, 1.0f, 1.0f);
        return bee;
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
