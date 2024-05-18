package tech.alexnijjar.golemoverhaul.common.entities.golems;

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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;
import tech.alexnijjar.golemoverhaul.common.utils.ModUtils;

import java.util.UUID;

public class CoalGolem extends BaseGolem {

    public static final int DEATH_TICKS = 13;
    public static final int MAX_SUMMON_TICKS = 20 * 120;

    private static final EntityDataAccessor<Boolean> ID_LIT = SynchedEntityData.defineId(CoalGolem.class, EntityDataSerializers.BOOLEAN);

    private boolean summoned;

    @Nullable
    private UUID summonerId;

    public CoalGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 1;
        this.setPathfindingMalus(PathType.LAVA, 0);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10)
            .add(Attributes.MOVEMENT_SPEED, 0.35)
            .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "death_controller", 5, state -> {
            if (deathTime == 0) return PlayState.STOP;
            return state.setAndContinue(ConstantAnimations.DIE);
        }));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LIT, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Lit", this.isLit());
        if (summonerId != null) compound.putUUID("SummonerId", summonerId);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLit(compound.getBoolean("Lit"));
        if (compound.hasUUID("SummonerId")) this.setSummoner(compound.getUUID("SummonerId"));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new LeapAtTargetGoal(this, 0.5f));
    }

    @Override
    public boolean canTarget() {
        return isLit() || isSummoned();
    }

    public boolean isLit() {
        return this.entityData.get(ID_LIT);
    }

    public void setLit(boolean lit) {
        this.entityData.set(ID_LIT, lit);

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(lit ? 12 : 2);
        updateAttackGoals();
    }

    public boolean isSummoned() {
        return this.summoned;
    }

    public void setSummoner(@Nullable UUID summoner) {
        this.summoned = summoner != null;
        this.summonerId = summoner;
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        if (isSummoned()) return;
        super.dropAllDeathLoot(damageSource);
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        if (isLit() || deathTime >= DEATH_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.removeAndAddDeathParticles();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isLit() ? ModSoundEvents.COAL_GOLEM_AMBIENT.get() : super.getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return isLit() ? ModSoundEvents.COAL_GOLEM_HURT.get() : super.getHurtSound(damageSource);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isLit() ? ModSoundEvents.COAL_GOLEM_DEATH.get() : super.getDeathSound();
    }

    @Override
    public Crackiness.Level getCrackiness() {
        return Crackiness.Level.NONE;
    }

    @Override
    public boolean shouldAttack(LivingEntity entity) {
        if (entity instanceof Creeper) return isLit();
        return super.shouldAttack(entity);
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }

    @Override
    public int getAttackTicks() {
        return 10;
    }

    @Override
    public Item getRepairItem() {
        return Items.COAL;
    }

    @Override
    public float getRepairItemHealAmount() {
        return getMaxHealth();
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
                target.igniteForSeconds(5);
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
    public void tick() {
        if (level() instanceof ServerLevel level) {
            if (tickCount > MAX_SUMMON_TICKS && isSummoned()) {
                kill();
                playSound(ModSoundEvents.COAL_GOLEM_EXPLODE.get());
            }
            if (summonerId != null) {
                Entity summoner = level.getEntity(summonerId);
                if (summoner instanceof Mob mob) {
                    setTarget(mob.getTarget());
                }
            }
        }
        super.tick();
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (super.mobInteract(player, hand).consumesAction()) return InteractionResult.SUCCESS;
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);

        if (isLit()) {
            if (stack.isEmpty()) {
                extinguishFire();
                return InteractionResult.SUCCESS;
            }
        } else {
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
        return super.getAttackBoundingBox().inflate(1, 0, 1);
    }
}