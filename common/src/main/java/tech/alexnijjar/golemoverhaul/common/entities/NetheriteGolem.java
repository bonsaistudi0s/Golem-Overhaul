package tech.alexnijjar.golemoverhaul.common.entities;

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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class NetheriteGolem extends BaseGolem implements PlayerRideableJumping {
    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(NetheriteGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GILDED = SynchedEntityData.defineId(NetheriteGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SUMMONING_TICKS = SynchedEntityData.defineId(NetheriteGolem.class, EntityDataSerializers.INT);

    private int summonCooldown;

    public NetheriteGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 24;
        setMaxUpStep(1);
        setPathfindingMalus(BlockPathTypes.WATER, -1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        super.registerControllers(controllerRegistrar);
        controllerRegistrar.add(new AnimationController<>(this, "death_controller", 5, state -> {
            if (deathTime == 0) return PlayState.STOP;
            return state.setAndContinue(ConstantAnimations.DIE);
        }));

        controllerRegistrar.add(new AnimationController<>(this, "summon_controller", 0, state -> {
            if (getSummoningTicks() == 0) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }
            return state.setAndContinue(ConstantAnimations.SUMMON);
        }));
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 320.0)
            .add(Attributes.ARMOR, 10.0)
            .add(Attributes.ARMOR_TOUGHNESS, 5.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.ATTACK_DAMAGE, 18.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CHARGED, false);
        entityData.define(GILDED, false);
        entityData.define(SUMMONING_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Charged", isCharged());
        compound.putBoolean("Gilded", isGilded());
        compound.putInt("SummoningTicks", getSummoningTicks());
        compound.putInt("SummonCooldown", summonCooldown);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setCharged(compound.getBoolean("Charged"));
        setGilded(compound.getBoolean("Gilded"));
        setSummoningTicks(compound.getInt("SummoningTicks"));
        summonCooldown = compound.getInt("SummonCooldown");
    }

    @Override
    public int getAttackSwingTicks() {
        return 25;
    }

    @Override
    public int getDeathAnimationTicks() {
        return 50;
    }

    @Override
    public boolean hasCustomDeathAnimation() {
        return true;
    }

    @Override
    public Item getRepairItem() {
        return Items.NETHERITE_SCRAP;
    }

    @Override
    public int getRepairItemHealAmount() {
        return 80;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean isSensitiveToWater() {
        return isCharged();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return effectInstance.getEffect().isBeneficial();
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (stack.is(Items.LAVA_BUCKET) && !isCharged()) {
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            }
            playSound(SoundEvents.BUCKET_EMPTY_LAVA);
            setCharged(true);
            return InteractionResult.SUCCESS;
        } else if (stack.is(Items.BUCKET) && isCharged()) {
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, Items.LAVA_BUCKET.getDefaultInstance());
            }
            playSound(SoundEvents.BUCKET_FILL_LAVA);
            playSound(SoundEvents.FIRE_EXTINGUISH);
            setCharged(false);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.GOLD_INGOT) && !isGilded()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            playSound(SoundEvents.ARMOR_EQUIP_GOLD);
            setGilded(true);
            return InteractionResult.SUCCESS;
        } else if (stack.is(Items.SHEARS) && isGilded()) {
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                BehaviorUtils.throwItem(this, Items.GOLD_INGOT.getDefaultInstance(), player.position());
            }
            playSound(SoundEvents.SNOW_GOLEM_SHEAR);
            playSound(SoundEvents.ARMOR_EQUIP_GOLD);
            setGilded(false);
            return InteractionResult.SUCCESS;
        }

        if (!stack.is(Items.LAVA_BUCKET)
            && !stack.is(Items.NETHERITE_SCRAP)
            && !stack.is(Items.BUCKET)
            && !stack.is(Items.GOLD_INGOT)
            && !stack.is(Items.SHEARS)
            && !level().isClientSide()) {
            player.setYRot(getYRot());
            player.setXRot(getXRot());
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    public boolean isCharged() {
        return entityData.get(CHARGED);
    }

    public boolean isGilded() {
        return entityData.get(GILDED);
    }

    public void setCharged(boolean charged) {
        entityData.set(CHARGED, charged);
    }

    public void setGilded(boolean gilded) {
        entityData.set(GILDED, gilded);
    }

    public int getSummoningTicks() {
        return entityData.get(SUMMONING_TICKS);
    }

    public void setSummoningTicks(int summoningTicks) {
        entityData.set(SUMMONING_TICKS, summoningTicks);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            Vec3 lookAngle = getLookAngle();
            target.setDeltaMovement(target.getDeltaMovement().add(lookAngle.x * 0.4, 0.2, lookAngle.z * 0.4));
            doAoeAttack(target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 2);
            return true;
        }
        return false;
    }

    public void doAoeAttack(@Nullable Entity target, float damage, float radius) {
        for (var entity : level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
            if (entity != target && entity != this) {
                if (target != null && entity instanceof IronGolem) continue;
                if (target != null && entity instanceof Player) continue;
                if (target != null && entity instanceof OwnableEntity) continue;
                if (entity.equals(getFirstPassenger())) continue;
                entity.hurt(damageSources().mobAttack(this), damage);
                Vec3 lookAngle = getLookAngle();
                entity.setDeltaMovement(entity.getDeltaMovement().add(lookAngle.x * 0.6, 0.4, lookAngle.z * 0.6));
            }
        }
    }


    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.0f;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        callback.accept(
            passenger,
            getX(),
            getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset() + 0.2 + (getSummoningTicks() < 47 && getSummoningTicks() > 14 ? -0.25 : 0),
            getZ()
        );
        if (passenger instanceof LivingEntity) {
            ((LivingEntity) passenger).yBodyRot = this.yBodyRot;
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 vec2 = getRiddenRotation(player);
        setRot(vec2.y, vec2.x);
        yRotO = yBodyRot = yHeadRot = getYRot();
    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float g = player.zza;
        if (g <= 0.0F) {
            g *= 0.25F;
        }

        return new Vec3(f, 0.0, g);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        if (getSummoningTicks() > 0) return 0;
        return (float) getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.5f;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            if (this.getFirstPassenger() instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        return null;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        setAttackAnimationTick(getAttackSwingTicks());
    }

    @Override
    public boolean canJump() {
        return getAttackAnimationTick() == 0;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        if (!level().isClientSide()) {
            doAoeAttack(null, 4 + jumpPower / 6f, 1.5f + (jumpPower / 50f));
            playSound(SoundEvents.IRON_GOLEM_ATTACK);
        }
        setAttackAnimationTick(getAttackSwingTicks());
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            int summoningTicks = getSummoningTicks();
            if (summoningTicks > 0) {
                if (summoningTicks == 40) {
                    spawnCoalGolems();
                }
                setSummoningTicks(summoningTicks - 1);
                navigation.stop();
                setTarget(null);
            } else if (summonCooldown > 0) {
                summonCooldown--;
            } else if (summonCooldown == 0 && getTarget() != null && getFirstPassenger() == null && random.nextInt(100) == 0) {
                startSummon();
            }
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        if (level().isClientSide() && isCharged()) {
            Vec3 lookAngle = getLookAngle();
            this.level().addParticle(ParticleTypes.FLAME,
                getRandomX(0.3) + lookAngle.x * 0.5,
                getY() + 0.8,
                getRandomZ(0.3) + lookAngle.z * 0.5,
                lookAngle.x * 0.05, 0.05, lookAngle.z * 0.05);
        }
        super.aiStep();
    }

    @Override
    public boolean hasAttackAnimation() {
        return getSummoningTicks() == 0;
    }

    public void startSummon() {
        if (summonCooldown > 0) return;
        if (!isCharged()) return;
        setSummoningTicks(60);
        summonCooldown = 1200;
    }

    public void spawnCoalGolems() {
        playSound(SoundEvents.FIRECHARGE_USE);
        for (int i = 0; i < 5; i++) {
            var golem = ModEntityTypes.COAL_GOLEM.get().create(level());
            if (golem == null) return;
            Vec3 lookAngle = getLookAngle();
            golem.setPos(getX() + lookAngle.x * 0.5, getY() + 0.35, getZ() + lookAngle.z * 0.5);
            golem.setLit(true);
            golem.setSummoned(true);
            level().addFreshEntity(golem);
            golem.setTarget(getTarget());
        }

        for (int i = 0; i < 10; i++) {
            this.level().addParticle(ParticleTypes.LARGE_SMOKE,
                this.getRandomX(0.5),
                this.getRandomY() - 0.75,
                this.getRandomZ(0.5),
                (this.random.nextDouble() - 0.5) * 0.5, -this.random.nextDouble(),
                (this.random.nextDouble() - 0.5) * 0.5);
        }
    }
}
