package tech.alexnijjar.golemoverhaul.common.entities.terracotta;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CactusTerracottaGolem extends TerracottaGolem implements Shearable {

    public CactusTerracottaGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ARMOR, 5.0)
            .add(Attributes.ARMOR_TOUGHNESS, 2.0)
            .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide()) return false;

        if (!source.is(DamageTypes.THORNS)) {
            if (source.getDirectEntity() instanceof LivingEntity entity) {
                entity.hurt(damageSources().thorns(this), 6);
            }
        }

        return super.hurt(source, amount);
    }

    @Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (itemStack.is(Items.SHEARS)) {
			if (!this.level().isClientSide) {
				shear(SoundSource.PLAYERS);
				itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				return InteractionResult.SUCCESS;
			} else return InteractionResult.CONSUME;
		} else return super.mobInteract(player, hand);
	}

    @Override
    public void shear(SoundSource source) {
        playSound(SoundEvents.SHEEP_SHEAR, 1.0f, 1.0f);
        if (!level().isClientSide()) {
            convertTo(ModEntityTypes.TERRACOTTA_GOLEM.get(), false);
            BehaviorUtils.throwItem(this, Items.CACTUS.getDefaultInstance(), Vec3.ZERO);
        }
    }

    @Override
    public boolean readyForShearing() {
        return true;
    }
}
