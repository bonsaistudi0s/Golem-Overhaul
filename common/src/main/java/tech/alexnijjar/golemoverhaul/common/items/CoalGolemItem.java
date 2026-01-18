package tech.alexnijjar.golemoverhaul.common.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@MethodsReturnNonnullByDefault
public class CoalGolemItem extends Item {

    public CoalGolemItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int remainingTime) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        boolean shouldLight = false;
        var otherHand = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND :
                InteractionHand.MAIN_HAND;
        var stackInOtherHand = player.getItemInHand(otherHand);
        if (stackInOtherHand.getItem() instanceof FlintAndSteelItem) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FLINTANDSTEEL_USE,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!level.isClientSide()) {
                stackInOtherHand.hurtAndBreak(1, player, LivingEntity.getSlotForHand(otherHand));
            }
            shouldLight = true;
        } else if (stackInOtherHand.getItem() instanceof FireChargeItem) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!level.isClientSide()) {
                stackInOtherHand.shrink(1);
            }
            shouldLight = true;
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW,
                SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (level.isClientSide()) {
            return;
        }

        var timeHeld = this.getUseDuration(stack, player) - remainingTime;
        var power = getPowerForTime(timeHeld);
        int throwCount;
        if (power < 0.25F) {
            throwCount = 1;
        } else if (power < 0.5F) {
            throwCount = 2;
        } else if (power < 0.75F) {
            throwCount = 3;
        } else {
            throwCount = 4;
        }
        throwCount = Math.min(throwCount, stack.getCount());

        var lookAngle = player.getLookAngle();
        for (var i = 0; i < throwCount; i++) {
            var coalGolem = ModEntityTypes.COAL_GOLEM.get().create(level);
            if (coalGolem == null) {
                return;
            }

            double offsetX = 0.0;
            double offsetY = 0.0;
            double offsetZ = 0.0;

            if (throwCount > 1) {
                offsetX = (level.getRandom().nextDouble() - 0.5) * 0.5;
                offsetY = (level.getRandom().nextDouble() - 0.5) * 0.5;
                offsetZ = (level.getRandom().nextDouble() - 0.5) * 0.5;
            }

            if (shouldLight) {
                coalGolem.setLit(true);
            }

            coalGolem.setPos(player.getX() + lookAngle.x + offsetX, player.getEyeY() + lookAngle.y + offsetY,
                    player.getZ() + lookAngle.z + offsetZ);
            coalGolem.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.25f, 2);

            level.addFreshEntity(coalGolem);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(throwCount);
        }

        player.getCooldowns().addCooldown(this, 10);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    public static float getPowerForTime(int timeHeld) {
        float pow = (float) timeHeld / 20.0F;
        if ((pow = (pow * pow + pow * 2.0F) / 3.0F) > 1.0F) {
            pow = 1.0F;
        }
        return pow;
    }

    public static class DispenseItemBehaviour extends DefaultDispenseItemBehavior {

        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            var level = source.level();
            var position = DispenserBlock.getDispensePosition(source);
            var direction = source.state().getValue(DispenserBlock.FACING);
            var targetPos = source.pos().relative(direction);
            var targetBlockState = level.getBlockState(targetPos);

            var coalGolem = ModEntityTypes.COAL_GOLEM.get().create(level);
            if (coalGolem == null) {
                return stack;
            }

            if (targetBlockState.is(Blocks.FIRE)) {
                coalGolem.setLit(true);
            }

            coalGolem.setPos(position.x(), position.y(), position.z());
            coalGolem.shoot(direction.getStepX(), (float) direction.getStepY() + 0.1F, direction.getStepZ(), 1.25f, 10);

            level.addFreshEntity(coalGolem);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.level().levelEvent(1002, source.pos(), 0);
        }
    }
}
