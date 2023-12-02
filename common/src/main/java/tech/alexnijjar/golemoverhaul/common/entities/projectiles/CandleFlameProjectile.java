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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.UUID;

public class CandleFlameProjectile extends Fireball {
    @Nullable
    private LivingEntity target;
    private UUID targetId;

    public CandleFlameProjectile(EntityType<? extends Fireball> type, Level level) {
        super(type, level);
    }

    public CandleFlameProjectile(Level level, LivingEntity target) {
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
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Target")) {
            targetId = compound.getUUID("Target");
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (target != null) moveTowardsTarget(target);
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

        if (tickCount > 100 || target == null || target.isRemoved()) {
            remove(RemovalReason.DISCARDED);
        }

        if (target != null && distanceTo(target) < 1.0f) {
            target.setSecondsOnFire(5);
            target.hurt(damageSources().onFire(), 3.0f);
            playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f);
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        remove(RemovalReason.DISCARDED);
    }

    private void spawnParticles() {
        for (int i = 0; i < 8; i++) {
            double x = this.getX() + this.random.nextDouble() * 0.5;
            double y = this.getY() + this.random.nextDouble() * 0.5;
            double z = this.getZ() + this.random.nextDouble() * 0.5;
            level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    private void moveTowardsTarget(LivingEntity target) {
        double x = target.getX() - this.getX();
        double y = target.getY() + 0.5f - this.getY();
        double z = target.getZ() - this.getZ();
        double distance = Math.sqrt(x * x + y * y + z * z);
        double speed = 0.2;
        this.setDeltaMovement(x / distance * speed, y / distance * speed, z / distance * speed);
    }
}
