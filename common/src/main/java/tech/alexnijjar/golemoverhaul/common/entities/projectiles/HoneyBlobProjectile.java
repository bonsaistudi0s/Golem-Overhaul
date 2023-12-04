package tech.alexnijjar.golemoverhaul.common.entities.projectiles;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.alexnijjar.golemoverhaul.common.entities.HoneyGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class HoneyBlobProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HoneyBlobProjectile(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public HoneyBlobProjectile(Level level, LivingEntity owner) {
        super(ModEntityTypes.HONEY_BLOB.get(), level);
        setOwner(owner);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    private ParticleOptions getParticle() {
        return new ItemParticleOption(ParticleTypes.ITEM, Items.HONEY_BLOCK.getDefaultInstance());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                level().addParticle(getParticle(), getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof HoneyGolem) return;
        if (getOwner() != null && result.getEntity().equals(getOwner())) return;
        int arrowCount = -1;
        if (result.getEntity() instanceof LivingEntity livingEntity) {
            arrowCount = livingEntity.getArrowCount();
        }
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.setArrowCount(arrowCount);
        }
        Entity entity = result.getEntity();
        entity.hurt(damageSources().thrown(this, getOwner()), 6);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                getOwner() instanceof Player ? 180 : 60,
                2));
        }
        if (!level().isClientSide()) {
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.HONEY_BLOCK_BREAK;
    }

    @Override
    public void tick() {
        super.tick();
        if (inGround) discard();
    }
}
