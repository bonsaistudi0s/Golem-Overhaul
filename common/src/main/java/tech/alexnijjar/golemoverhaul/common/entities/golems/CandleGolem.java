package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.CandleFlameProjectile;

public class CandleGolem extends BaseGolem implements RangedAttackMob {

    private static final float HEALTH_LOSS_PER_SHOT = 0.02f;

    private static final EntityDataAccessor<Boolean> ID_LIT = SynchedEntityData.defineId(CandleGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ID_SITTING = SynchedEntityData.defineId(CandleGolem.class, EntityDataSerializers.BOOLEAN);

    private final RangedAttackGoal rangedAttackGoal = new CandleGolemRangedAttackGoal(this, 1, 20, 15);

    public CandleGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 4;
        this.navigation = new CandleGolemGroundPathNavigation();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 15)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 3);
    }

    @Override
    public PlayState getMoveAnimation(AnimationState<BaseGolem> state, boolean moving) {
        return state.setAndContinue(moving ?
            ConstantAnimations.WALK :
            isSitting() ?
                ConstantAnimations.SITTING_IDLE :
                ConstantAnimations.IDLE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LIT, false);
        builder.define(ID_SITTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Lit", this.isLit());
        compound.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLit(compound.getBoolean("Lit"));
        this.setSitting(compound.getBoolean("Sitting"));
    }

    @Override
    public boolean canMeleeAttack() {
        return !canBeLit();
    }

    @Override
    public boolean canTarget() {
        return isLit() || !canBeLit();
    }

    public boolean isLit() {
        return canBeLit() && this.entityData.get(ID_LIT);
    }

    public void setLit(boolean lit) {
        this.entityData.set(ID_LIT, lit);

        this.goalSelector.removeGoal(this.rangedAttackGoal);
        if (lit) {
            this.goalSelector.addGoal(2, this.rangedAttackGoal);
        }
        this.updateAttackGoals();
    }

    public boolean isSitting() {
        return this.entityData.get(ID_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(ID_SITTING, sitting);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(sitting ? 1 : 0);
    }

    public boolean canBeLit() {
        return getCrackiness() != Crackiness.Level.HIGH;
    }

    @Override
    public boolean hasAttackAnimation() {
        return false;
    }

    @Override
    public void lavaHurt() {
        super.lavaHurt();
        setLit(true);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        if (damageSource.is(DamageTypeTags.IS_FIRE)) setLit(true);
        if (isSitting()) {
            damageAmount *= 0.1f;
        }
        super.actuallyHurt(damageSource, damageAmount);
        updateAttackGoals();
    }

    @Override
    public void extinguishFire() {
        super.extinguishFire();
        setLit(false);
        playSound(SoundEvents.FIRE_EXTINGUISH, 1, 2);
    }

    @Override
    public int getAttackTicks() {
        return 10;
    }

    @Override
    public Item getRepairItem() {
        return Items.HONEYCOMB;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 5;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        Projectile projectile = new CandleFlameProjectile(level(), CandleGolem.this);
        projectile.setPos(getX(), getY(), getZ());

        double x = target.getX() - getX();
        double y = target.getY() - projectile.getY();
        double z = target.getZ() - getZ();
        double distance = Math.sqrt(x * x + z * z) * 0.2;
        projectile.shoot(x, y + distance, z, 0.4f, 5);

        level().addFreshEntity(projectile);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.BLAZE_SHOOT, getSoundSource(), 0.3f, random.nextFloat() * 0.4f + 0.8f);
        setHealth(getHealth() - HEALTH_LOSS_PER_SHOT);
    }

    @Override
    public void tick() {
        if (isSitting()) {
            setDeltaMovement(0, getDeltaMovement().y(), 0);
            navigation.stop();
        }
        super.tick();
        if (level().isClientSide()) {
            if (isLit() && tickCount % 10 == 0) {
                level().addParticle(ParticleTypes.FLAME, getX(), getY() + 0.4, getZ(), 0, 0.02, 0);
            }
        } else if (isInWaterOrRain()) {
            setLit(false);
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (super.mobInteract(player, hand).consumesAction()) return InteractionResult.SUCCESS;
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown() && stack.isEmpty()) {
            setSitting(!isSitting());
            return InteractionResult.SUCCESS;
        }

        if (isLit()) {
            if (stack.isEmpty()) {
                extinguishFire();
                return InteractionResult.SUCCESS;
            }
        } else if (canBeLit()) {
            if (stack.is(Items.FLINT_AND_STEEL)) {
                stack.hurtAndBreak(1, player, getSlotForHand(hand));
                playSound(SoundEvents.FLINTANDSTEEL_USE);
                setLit(true);
                return InteractionResult.SUCCESS;
            } else if (stack.is(Items.FIRE_CHARGE)) {
                stack.shrink(1);
                playSound(SoundEvents.FIRECHARGE_USE);
                setLit(true);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(0.25f, 0, 0.25f);
    }

    private class CandleGolemRangedAttackGoal extends RangedAttackGoal {

        public CandleGolemRangedAttackGoal(RangedAttackMob rangedAttackMob, double speedModifier, int attackInterval, float attackRadius) {
            super(rangedAttackMob, speedModifier, attackInterval, attackRadius);
        }

        @Override
        public boolean canUse() {
            return canBeLit() && super.canUse();
        }
    }

    private class CandleGolemGroundPathNavigation extends GroundPathNavigation {

        public CandleGolemGroundPathNavigation() {
            super(CandleGolem.this, level());
        }

        @Override
        protected boolean canUpdatePath() {
            return !isSitting() && super.canUpdatePath();
        }

        @Override
        public boolean moveTo(@Nullable Path path, double speed) {
            return !isSitting() && super.moveTo(path, speed);
        }
    }
}