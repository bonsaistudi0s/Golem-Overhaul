package tech.alexnijjar.golemoverhaul.common.entities.projectiles;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import tech.alexnijjar.golemoverhaul.common.entities.golems.CandleGolem;
import tech.alexnijjar.golemoverhaul.common.entities.golems.TerracottaGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CandleFlameProjectile extends ThrowableItemProjectile {

    public CandleFlameProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public CandleFlameProjectile(Level level, LivingEntity owner) {
        super(ModEntityTypes.CANDLE_FLAME.get(), level);
        setOwner(owner);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof CandleGolem) return;
        if (result.getEntity() instanceof TerracottaGolem) return;
        entity.hurt(damageSources().thrown(this, getOwner()), 3);
        entity.igniteForSeconds(5);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide()) {
            playSound(SoundEvents.SHULKER_BULLET_HIT, 1, 1);
            discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected double getDefaultGravity() {
        return super.getDefaultGravity();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() && tickCount % 5 == 0) spawnParticles();
        if (!level().isClientSide() && tickCount > 100) {
            discard();
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            double x = this.getX() + this.random.nextDouble() * 0.5;
            double y = this.getY() + this.random.nextDouble() * 0.5;
            double z = this.getZ() + this.random.nextDouble() * 0.5;
            level().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }
}
