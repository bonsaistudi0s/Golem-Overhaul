package tech.alexnijjar.golemoverhaul.common.entities.projectiles;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import tech.alexnijjar.golemoverhaul.common.entities.candle.CandleGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CandleFlameProjectile extends Fireball {

    public CandleFlameProjectile(EntityType<? extends Fireball> type, Level level) {
        super(type, level);
    }

    public CandleFlameProjectile(Level level, LivingEntity owner) {
        super(ModEntityTypes.CANDLE_FLAME.get(), level);
        setOwner(owner);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        Vec3 currentVelocity = this.getDeltaMovement();
        super.tick();
        this.setDeltaMovement(currentVelocity);
        if (level().isClientSide() && tickCount % 5 == 0) spawnParticles();
        if (level().isClientSide()) return;

        if (tickCount > 100) {
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
        target.hurt(damageSources().fireball(this, target), 1.0f);
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
