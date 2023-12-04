package tech.alexnijjar.golemoverhaul.common.entities.terracotta;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.MudBallProjectile;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class DeadBushTerracottaGolem extends TerracottaGolem implements RangedAttackMob, Shearable {
    public DeadBushTerracottaGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 30, 10.0F));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        var projectile = new MudBallProjectile(level(), this);
        projectile.setPos(getX(), getY(), getZ());

        double d = target.getX() - getX();
        double e = target.getY() - projectile.getY();
        double f = target.getZ() - getZ();
        double g = Math.sqrt(d * d + f * f) * 0.2f;
        projectile.shoot(d, e + g, f, 1f, 5.0f);

        level().addFreshEntity(projectile);
        playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1, 0.4f / (getRandom().nextFloat() * 0.4f + 0.8f));
        level().broadcastEntityEvent(this, (byte) 4);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            attackAnimationTick = getAttackAnimationTick();
        }
    }

    @Override
    public boolean canDoMeleeAttack() {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.SHEARS)) {
            if (!this.level().isClientSide) {
                shear(SoundSource.PLAYERS);
                itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void shear(SoundSource source) {
        playSound(SoundEvents.SHEEP_SHEAR, 1.0f, 1.0f);
        if (!level().isClientSide()) {
            convertTo(ModEntityTypes.TERRACOTTA_GOLEM.get(), false);
            BehaviorUtils.throwItem(this, Items.DEAD_BUSH.getDefaultInstance(), Vec3.ZERO);
        }
    }

    @Override
    public boolean readyForShearing() {
        return true;
    }
}
