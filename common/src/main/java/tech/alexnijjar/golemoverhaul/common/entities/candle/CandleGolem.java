package tech.alexnijjar.golemoverhaul.common.entities.candle;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.CandleFlameProjectile;

public class CandleGolem extends BaseGolem implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> LIT = SynchedEntityData.defineId(CandleGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(CandleGolem.class, EntityDataSerializers.BOOLEAN);

    public CandleGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 4;
        setMaxUpStep(0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, 5, state -> {
            boolean moving = state.getLimbSwingAmount() > 0.1 || state.getLimbSwingAmount() < -0.1;
            return state.setAndContinue(moving ?
                ConstantAnimations.WALK :
                isSitting() ?
                    ConstantAnimations.SITTING_IDLE :
                    ConstantAnimations.IDLE);
        }));
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 15.0)
            .add(Attributes.MOVEMENT_SPEED, 0.22)
            .add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 40, 10.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LIT, true);
        entityData.define(SITTING, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Lit", isLit());
        compound.putBoolean("Sitting", isSitting());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setLit(compound.getBoolean("Lit"));
        setSitting(compound.getBoolean("Sitting"));
    }

    @Override
    public boolean canFloatInWater() {
        return true;
    }

    @Override
    public boolean canDoMeleeAttack() {
        return !isLit() || isSitting();
    }

    @Override
    public boolean canMoveTowardsTarget() {
        return !isSitting();
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
        return Items.HONEYCOMB;
    }

    @Override
    public int getRepairItemHealAmount() {
        return 10;
    }

    @Override
    public boolean shouldAttack(LivingEntity entity) {
        if (entity instanceof Creeper) return isLit();
        return super.shouldAttack(entity);
    }

    public boolean isLit() {
        return entityData.get(LIT);
    }

    public void setLit(boolean lit) {
        entityData.set(LIT, lit);
    }

    public boolean isSitting() {
        return entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        entityData.set(SITTING, sitting);
    }

    @Override
    public boolean hasAttackAnimation() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public boolean playDefaultStepSound() {
        return false;
    }

    @Override
    public void lavaHurt() {
        super.lavaHurt();
        setLit(true);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE)) setLit(true);
        return super.hurt(source, amount);
    }

    @Override
    public Crackiness getCrackiness() {
        return Crackiness.NONE;
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
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        if (damageSource.is(DamageTypeTags.IS_FIRE)) setLit(true);
        if (isSitting()) {
            damageAmount *= 0.2f;
        }
        super.actuallyHurt(damageSource, damageAmount);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (isSitting()) return;
        super.knockback(strength, x, z);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        var stack = player.getItemInHand(hand);
        if (stack.is(Items.FLINT_AND_STEEL) && !isLit()) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            playSound(SoundEvents.FLINTANDSTEEL_USE);
            setLit(true);
            return InteractionResult.SUCCESS;
        } else if (isLit() && player.isShiftKeyDown()) {
            setLit(false);
            playSound(SoundEvents.FIRE_EXTINGUISH);
        } else {
            setSitting(!isSitting());
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        var projectile = new CandleFlameProjectile(level(), CandleGolem.this);
        projectile.setPos(getX(), getY(), getZ());

        double d = target.getX() - getX();
        double e = target.getY() - projectile.getY();
        double f = target.getZ() - getZ();
        double g = Math.sqrt(d * d + f * f) * 0.2f;
        projectile.shoot(d, e + g, f, 0.2f, 5.0f);

        level().addFreshEntity(projectile);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.BLAZE_SHOOT, getSoundSource(), 0.3f, random.nextFloat() * 0.4f + 0.8f);
        setHealth(getHealth() - 0.02f);
    }
}
