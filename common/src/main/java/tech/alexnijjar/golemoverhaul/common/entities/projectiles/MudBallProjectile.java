package tech.alexnijjar.golemoverhaul.common.entities.projectiles;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.alexnijjar.golemoverhaul.common.entities.golems.TerracottaGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class MudBallProjectile extends ThrowableItemProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MudBallProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public MudBallProjectile(Level level, LivingEntity owner) {
        super(ModEntityTypes.MUD_BALL.get(), level);
        setOwner(owner);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Nullable
    private ParticleOptions getParticle() {
        ItemStack stack = getItem();
        return stack.isEmpty() ? null : new ItemParticleOption(ParticleTypes.ITEM, stack);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particle = getParticle();
            if (particle == null) return;
            for (int i = 0; i < 8; ++i) {
                level().addParticle(particle, getX(), getY(), getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof TerracottaGolem) return;
        Entity entity = result.getEntity();
        entity.hurt(damageSources().thrown(this, getOwner()), 4);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide()) {
            level().broadcastEntityEvent(this, (byte) 3);
            playSound(SoundEvents.MUD_BREAK, 1, 1);
            discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.MUD;
    }
}
