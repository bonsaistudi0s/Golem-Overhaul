package tech.alexnijjar.golemoverhaul.common.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import tech.alexnijjar.golemoverhaul.common.ModUtils;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;

import java.util.UUID;

public class CoalGolem extends BaseGolem {
    private static final EntityDataAccessor<Boolean> LIT = SynchedEntityData.defineId(CoalGolem.class, EntityDataSerializers.BOOLEAN);

    private boolean summoned;

    @Nullable
    private UUID summonerId;

    public CoalGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 1;
        setMaxUpStep(0);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        super.registerControllers(controllerRegistrar);
        controllerRegistrar.add(new AnimationController<>(this, "death_controller", 5, state -> {
            if (deathTime == 0) return PlayState.STOP;
            return state.setAndContinue(ConstantAnimations.DIE);
        }));
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0)
            .add(Attributes.MOVEMENT_SPEED, 0.35)
            .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LIT, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Lit", isLit());
        compound.putBoolean("Summoned", isSummoned());
        if (summonerId != null) compound.putUUID("SummonerId", summonerId);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setLit(compound.getBoolean("Lit"));
        setSummoned(compound.getBoolean("Summoned"));
        if (compound.hasUUID("SummonerId")) summonerId = compound.getUUID("SummonerId");
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        if (isSummoned()) return;
        super.dropAllDeathLoot(damageSource);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new LeapAtTargetGoal(this, 0.5f));
    }

    @Override
    public boolean canFloatInWater() {
        return true;
    }

    @Override
    public int getDeathAnimationTicks() {
        return 13;
    }

    @Override
    public boolean hasCustomDeathAnimation() {
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
        return Items.COAL;
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

    public boolean isSummoned() {
        return summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    public void setSummoner(UUID summonerId) {
        this.summonerId = summonerId;
    }

    @Override
    public boolean hasAttackAnimation() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isLit() ? ModSoundEvents.COAL_GOLEM_AMBIENT.get() : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return isLit() ? ModSoundEvents.COAL_GOLEM_HURT.get() : SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isLit() ? ModSoundEvents.COAL_GOLEM_DEATH.get() : SoundEvents.GENERIC_DEATH;
    }

    @Override
    public boolean playIronGolemStepSound() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }

    @Override
    public void extinguishFire() {
        super.extinguishFire();
        if (isLit()) {
            setLit(false);
            playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE);
            for (int i = 0; i < 20; i++) {
                level().addParticle(ParticleTypes.LARGE_SMOKE,
                    getX() + random.nextGaussian() * 0.3,
                    getY() + 0.5 + random.nextGaussian() * 0.3,
                    getZ() + random.nextGaussian() * 0.3,
                    0, 0, 0);
            }
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (super.doHurtTarget(target)) {
            if (isLit() && !level().isClientSide()) {
                target.setSecondsOnFire(5);
                kill();
                deathTime = 10;

                playSound(ModSoundEvents.COAL_GOLEM_EXPLODE.get());

                for (int i = 0; i < 10; i++) {
                    ModUtils.sendParticles((ServerLevel) level(), ParticleTypes.FLAME,
                        getX() + random.nextGaussian() * 0.3,
                        getY() + 0.5 + random.nextGaussian() * 0.3,
                        getZ() + random.nextGaussian() * 0.3,
                        1, 0, 0, 0, 0);

                }
            }
            return true;
        }
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
    public double getAttributeValue(Attribute attribute) {
        if (attribute == Attributes.ATTACK_DAMAGE && isLit()) {
            return 12;
        }
        return super.getAttributeValue(attribute);
    }

    @Override
    public IronGolem.Crackiness getCrackiness() {
        return Crackiness.NONE;
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            if (tickCount > 2400 && isSummoned()) {
                kill();
                playSound(ModSoundEvents.COAL_GOLEM_EXPLODE.get());
            }
            if (summonerId != null) {
                var summoner = ((ServerLevel) level()).getEntity(summonerId);
                if (summoner instanceof Mob mob) {
                    setTarget(mob.getTarget());
                }
            }
        }
        super.tick();
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (stack.is(Items.FLINT_AND_STEEL) && !isLit()) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            playSound(SoundEvents.FLINTANDSTEEL_USE);
            setLit(true);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public float getAttackRange() {
        return 6.0f;
    }
}
