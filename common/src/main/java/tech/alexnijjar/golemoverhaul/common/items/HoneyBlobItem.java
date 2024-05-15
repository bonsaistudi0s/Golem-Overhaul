package tech.alexnijjar.golemoverhaul.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.HoneyBlobProjectile;

public class HoneyBlobItem extends Item {

    public HoneyBlobItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var stack = player.getItemInHand(usedHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SLIME_ATTACK, SoundSource.NEUTRAL,
            0.5f,
            0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
        );
        if (!level.isClientSide) {
            var projectile = new HoneyBlobProjectile(level, player);
            Vec3 lookAngle = player.getLookAngle();
            projectile.setPos(
                player.getX() + lookAngle.x,
                player.getEyeY() + lookAngle.y,
                player.getZ() + lookAngle.z);

            projectile.setBaseDamage(10);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.25f, 2);
            level.addFreshEntity(projectile);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
