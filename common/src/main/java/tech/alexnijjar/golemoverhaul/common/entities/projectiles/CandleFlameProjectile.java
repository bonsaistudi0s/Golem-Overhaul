package tech.alexnijjar.golemoverhaul.common.entities.projectiles;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.entities.candle.CandleGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.UUID;

public class CandleFlameProjectile extends Fireball {
    @Nullable
    private LivingEntity owner;
    @Nullable
    private LivingEntity target;
    private UUID targetId;
    private UUID ownerId;

    public CandleFlameProjectile(EntityType<? extends Fireball> type, Level level) {
        super(type, level);
    }

    public CandleFlameProjectile(Level level, LivingEntity owner, LivingEntity target) {
        super(ModEntityTypes.CANDLE_FLAME.get(), level);
        this.target = target;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.target != null) {
            compound.putUUID("Target", target.getUUID());
        }
        if (this.owner != null) {
            compound.putUUID("Owner", owner.getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Target")) {
            targetId = compound.getUUID("Target");
        }
        if (compound.hasUUID("Owner")) {
            ownerId = compound.getUUID("Owner");
        }
    }

    @Override
    public void tick() {
        Vec3 currentVelocity = this.getDeltaMovement();
        super.tick();
        this.setDeltaMovement(currentVelocity);
        if (level().isClientSide() && tickCount % 5 == 0) spawnParticles();
        if (level().isClientSide()) return;

        if (target == null && targetId != null) {
            var possibleTarget = ((ServerLevel) level()).getEntity(targetId);
            if (possibleTarget instanceof LivingEntity) {
                target = (LivingEntity) possibleTarget;
            } else {
                targetId = null;
            }
        }
        if (owner == null && ownerId != null) {
            var possibleOwner = ((ServerLevel) level()).getEntity(ownerId);
            if (possibleOwner instanceof LivingEntity) {
                owner = (LivingEntity) possibleOwner;
            } else {
                ownerId = null;
            }
        }

        if (tickCount > 100 || target == null || target.isRemoved()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        var target = result.getEntity();
        if (target instanceof CandleGolem) return;
        super.onHitEntity(result);
        target.setSecondsOnFire(5);
        target.hurt(damageSources().fireball(this, owner), 5.0f);
        playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f);
        remove(RemovalReason.DISCARDED);
    }

    private void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            double x = this.getX() + this.random.nextDouble() * 0.5;
            double y = this.getY() + this.random.nextDouble() * 0.5;
            double z = this.getZ() + this.random.nextDouble() * 0.5;
            level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }
    }
}
