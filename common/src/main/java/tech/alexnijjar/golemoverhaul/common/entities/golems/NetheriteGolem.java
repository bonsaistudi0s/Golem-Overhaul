package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.golemoverhaul.common.entities.IShearable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.recipes.GolemConstructionRecipe;
import tech.alexnijjar.golemoverhaul.common.recipes.SingleEntityInput;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;
import tech.alexnijjar.golemoverhaul.common.utils.ModUtils;

import java.util.List;

public class NetheriteGolem extends BaseGolem implements IShearable, PlayerRideableJumping {

    public static final byte SUMMON_EVENT_ID = 8;
    public static final int SUMMON_TICKS_LENGTH = 60;
    public static final int DEATH_TICKS = 50;
    public static final int SUMMONING_COOLDOWN_TICKS_LENGTH = 20 * 60;

    private static final EntityDataAccessor<Boolean> ID_CHARGED = SynchedEntityData.defineId(NetheriteGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ID_GILDED = SynchedEntityData.defineId(NetheriteGolem.class, EntityDataSerializers.BOOLEAN);

    private int summoningTicks;
    private int summonCooldown;

    private int lastJumpPower;

    public NetheriteGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 24;
        setPathfindingMalus(BlockPathTypes.WATER, -1);
        setMaxUpStep(maxUpStep() + 0.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 320)
            .add(Attributes.ARMOR, 20)
            .add(Attributes.ARMOR_TOUGHNESS, 8)
            .add(Attributes.MOVEMENT_SPEED, 0.14)
            .add(Attributes.ATTACK_KNOCKBACK, 2)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1)
            .add(Attributes.ATTACK_DAMAGE, 20);
    }

    public static void trySpawnGolem(Level level, BlockPos pos) {
        GolemConstructionRecipe recipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.GOLEM_CONSTRUCTION.get(), new SingleEntityInput(ModEntityTypes.NETHERITE_GOLEM.get()), level).orElseThrow();
        BlockPattern.BlockPatternMatch pattern = recipe.createPattern().find(level, pos);
        if (pattern == null) return;
        NetheriteGolem golem = ModEntityTypes.NETHERITE_GOLEM.get().create(level);
        if (golem == null) return;
        ModUtils.spawnGolemInWorld(level, pattern, golem, pattern.getBlock(1, 2, 0).getPos());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(this.getMovementController());

        controllers.add(new AnimationController<>(this, "attack_controller", 0, state -> {
            if (!hasAttackAnimation()) return PlayState.STOP;
            if (attackAnimationTicks == 0) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }
            return getAttackAnimation(state);
        }).setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(), ModSoundEvents.NETHERITE_GOLEM_HIT.get(), getSoundSource(), 1, 1, false)));

        controllers.add(new AnimationController<>(this, "death_controller", 0, state -> {
            if (deathTime == 0) return PlayState.STOP;
            return state.setAndContinue(ConstantAnimations.DIE);
        }).setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(), ModSoundEvents.NETHERITE_GOLEM_DEATH.get(), getSoundSource(), 1, 1, false)));

        controllers.add(new AnimationController<>(this, "summon_controller", 0, state -> {
            if (getSummoningTicks() == 0) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }
            return state.setAndContinue(ConstantAnimations.SUMMON);
        }).setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(), ModSoundEvents.NETHERITE_GOLEM_SUMMON.get(), getSoundSource(), 1, 1, false)));
    }

    @Override
    public AnimationController<?> getMovementController() {
        return super.getMovementController()
            .setSoundKeyframeHandler(event -> level().playLocalSound(blockPosition(), ModSoundEvents.NETHERITE_GOLEM_STEP.get(), getSoundSource(), 1, 1, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_CHARGED, false);
        this.entityData.define(ID_GILDED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("charged", this.isCharged());
        compound.putBoolean("gilded", this.isGilded());
        compound.putInt("summoning_ticks", this.getSummoningTicks());
        compound.putInt("summon_cooldown", this.getSummonCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCharged(compound.getBoolean("charged"));
        this.setGilded(compound.getBoolean("gilded"));
        this.setSummoningTicks(compound.getInt("summoning_ticks"));
        this.setSummonCooldown(compound.getInt("summon_cooldown"));
    }

    public boolean isCharged() {
        return this.entityData.get(ID_CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(ID_CHARGED, charged);
    }

    public boolean isGilded() {
        return this.entityData.get(ID_GILDED);
    }

    public void setGilded(boolean gilded) {
        this.entityData.set(ID_GILDED, gilded);
    }

    public int getSummoningTicks() {
        return this.summoningTicks;
    }

    public void setSummoningTicks(int summoningTicks) {
        this.summoningTicks = summoningTicks;
    }

    public int getSummonCooldown() {
        return this.summonCooldown;
    }

    public void setSummonCooldown(int summonCooldown) {
        this.summonCooldown = summonCooldown;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.NETHERITE_GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.NETHERITE_GOLEM_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == SUMMON_EVENT_ID) {
            this.summoningTicks = SUMMON_TICKS_LENGTH;
        }
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
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
    protected void tickDeath() {
        deathTime++;
        if (deathTime >= DEATH_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.removeAndAddDeathParticles();
        }
    }

    @Override
    public boolean canFloatInWater() {
        return false;
    }

    @Override
    public boolean hasAttackAnimation() {
        return getSummoningTicks() == 0;
    }

    @Override
    public int getAttackTicks() {
        return 25;
    }

    @Override
    public int getAttackDelayTicks() {
        return 14;
    }

    @Override
    public Item getRepairItem() {
        return Items.NETHERITE_SCRAP;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 80;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (super.mobInteract(player, hand).consumesAction()) return InteractionResult.SUCCESS;
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);

        if (isCharged()) {
            if (stack.is(Items.BUCKET)) {
                setCharged(false);
                return InteractionResult.SUCCESS;
            }
        } else {
            if (stack.is(Items.LAVA_BUCKET)) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                }
                playSound(SoundEvents.BUCKET_EMPTY_LAVA);
                setCharged(true);
                return InteractionResult.SUCCESS;
            }
        }

        if (!isGilded() && stack.is(Items.GOLD_INGOT)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            playSound(SoundEvents.ARMOR_EQUIP_GOLD);
            setGilded(true);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.SHEARS)) {
            return InteractionResult.PASS;
        }

        if (player.getVehicle() == null) {
            player.setYRot(getYRot());
            player.setXRot(getXRot());
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull List<ItemStack> onSheared() {
        playSound(SoundEvents.SNOW_GOLEM_SHEAR);
        playSound(SoundEvents.ARMOR_EQUIP_GOLD);
        setGilded(false);
        return List.of(Items.GOLD_INGOT.getDefaultInstance());
    }

    @Override
    public boolean isShearable() {
        return isGilded();
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(1, 0, 1);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity entity) {
            entity.yBodyRot = this.yBodyRot;
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
        return new Vec2(entity.getXRot() * 0.5f, entity.getYRot());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float x = player.xxa * 0.25f;
        float z = player.zza;
        if (z <= 0) {
            z *= 0.25f;
        }

        return new Vec3(x, 0, z);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        if (getSummoningTicks() > 0) return 0;
        return (float) getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.45f;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            if (this.getFirstPassenger() instanceof LivingEntity entity) {
                return entity;
            }
        }

        return null;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
    }

    @Override
    public boolean canJump() {
        return !this.isAttacking();
    }

    @Override
    public void handleStartJump(int jumpPower) {
        this.startAttacking();
        this.sendAttackEvent();
        this.lastJumpPower = jumpPower;
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public void actuallyAttackAfterDelay(LivingEntity target) {
        if (this.lastJumpPower == 0) {
            Vec3 lookAngle = getLookAngle();
            target.addDeltaMovement(new Vec3(lookAngle.x * 0.4, 0.5, lookAngle.z * 0.4));
            super.actuallyAttackAfterDelay(target);
        }
    }

    @Override
    public void performAdditionalAttacks(@Nullable LivingEntity target) {
        if (this.lastJumpPower > 0) {
            int attackBonus = this.lastJumpPower;
            int rangeBonus = this.lastJumpPower;
            float y = 0.5f;
            if (lastJumpPower == 100) {
                attackBonus = 175;
                rangeBonus = 150;
                y = 0.75f;
                if (level() instanceof ServerLevel level) {
                    Vec3 lookAngle = getLookAngle();
                    ModUtils.sendParticles(level, ParticleTypes.EXPLOSION, getX() + lookAngle.x * 0.5, getY() + 0.5, getZ() + lookAngle.z * 0.5, 10, 0.5, 0.5, 0.5, 0);
                }
            }
            doAoeAttack(null, 4 + (attackBonus / 6f), 1.5f + (rangeBonus / 40f), y);
            this.lastJumpPower = 0;
        } else {
            doAoeAttack(target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 0.5f, 0.5f);
        }
    }

    public void doAoeAttack(@Nullable LivingEntity target, float damage, float radius, float y) {
        for (var entity : level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
            if (entity != this) {
                if (target != null && entity instanceof AbstractGolem) continue;
                if (target != null && entity instanceof Player) continue;
                if (target != null && entity instanceof OwnableEntity) continue;
                if (entity.equals(getFirstPassenger())) continue;
                entity.hurt(damageSources().mobAttack(this), damage);
                Vec3 lookAngle = getLookAngle();
                entity.addDeltaMovement(new Vec3(lookAngle.x * 0.4, y, lookAngle.z * 0.4));
            }
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isVehicle();
    }

    @Override
    public void aiStep() {
        this.summoningTicks = Math.max(0, this.summoningTicks - 1);
        this.summonCooldown = Math.max(0, this.summonCooldown - 1);

        if (!level().isClientSide()) {
            if (summoningTicks > 0) {
                if (summoningTicks == 40) {
                    spawnCoalGolems();
                }
                navigation.stop();
                setTarget(null);
            }

            if (!isAttacking() && summonCooldown == 0 && getTarget() != null && getFirstPassenger() == null && random.nextInt(100) == 0) {
                summon();
            }
        }

        if (level().isClientSide() && isCharged()) {
            spawnFireParticles();
        }

        super.aiStep();
    }

    private void spawnFireParticles() {
        Vec3 lookAngle = getLookAngle();
        this.level().addParticle(ParticleTypes.FLAME,
            getRandomX(0.3) + lookAngle.x * 0.5,
            getY() + 0.8,
            getRandomZ(0.3) + lookAngle.z * 0.5,
            lookAngle.x * 0.05, 0.05, lookAngle.z * 0.05);
    }

    public void summon() {
        if (summonCooldown > 0) return;
        if (!isCharged()) return;
        this.summoningTicks = SUMMON_TICKS_LENGTH;
        this.summonCooldown = SUMMONING_COOLDOWN_TICKS_LENGTH;
        this.level().broadcastEntityEvent(this, SUMMON_EVENT_ID);
    }

    public void spawnCoalGolems() {
        playSound(SoundEvents.FIRECHARGE_USE);
        for (int i = 0; i < 5; i++) {
            CoalGolem golem = ModEntityTypes.COAL_GOLEM.get().create(level());
            if (golem == null) return;
            Vec3 lookAngle = getLookAngle();
            golem.setPos(getX() + lookAngle.x * 0.5, getY() + 0.35, getZ() + lookAngle.z * 0.5);
            golem.setLit(true);
            golem.setSummoner(getUUID());
            level().addFreshEntity(golem);
            golem.setTarget(getTarget());
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
