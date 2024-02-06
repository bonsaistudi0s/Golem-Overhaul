package tech.alexnijjar.golemoverhaul.common.entities.terracotta;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class TerracottaGolem extends BaseGolem {

    public TerracottaGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        xpReward = 6;
        setMaxUpStep(0);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    public boolean canFloatInWater() {
        return true;
    }

    @Override
    public boolean villageBound() {
        return false;
    }

    @Override
    public boolean doesSwingAttack() {
        return false;
    }

    @Override
    public Item getRepairItem() {
        return Items.CLAY;
    }

    @Override
    public int getRepairItemHealAmount() {
        return 4;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public boolean playIronGolemStepSound() {
        return false;
    }

    @Override
    public SoundEvent getDamageSound() {
        return SoundEvents.STONE_BREAK;
    }

    @Override
    public int getAttackSwingTicks() {
        return 12;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (level().isClientSide()) return super.mobInteract(player, hand);
        if (!getType().equals(ModEntityTypes.TERRACOTTA_GOLEM.get())) return super.mobInteract(player, hand);
        var stack = player.getItemInHand(hand);
        if (stack.is(Items.CACTUS)) {
            convertTo(ModEntityTypes.CACTUS_TERRACOTTA_GOLEM.get(), false);
        } else if (stack.is(Items.DEAD_BUSH)) {
            convertTo(ModEntityTypes.DEAD_BUSH_TERRACOTTA_GOLEM.get(), false);
        }

        return super.mobInteract(player, hand);
    }
}
