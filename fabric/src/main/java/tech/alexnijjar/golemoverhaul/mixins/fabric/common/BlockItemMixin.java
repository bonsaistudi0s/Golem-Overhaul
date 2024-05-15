package tech.alexnijjar.golemoverhaul.mixins.fabric.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.KelpGolem;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "place", at = @At("RETURN"))
    private void cadmus$place(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getLevel() instanceof ServerLevel level) {
            ItemStack stack = context.getItemInHand();
            if (stack.is(Items.SEA_LANTERN)) KelpGolem.trySpawnGolem(level, context.getClickedPos());
            if (stack.is(Items.DRIED_KELP_BLOCK)) KelpGolem.trySpawnGolem(level, context.getClickedPos());
        }
    }
}
