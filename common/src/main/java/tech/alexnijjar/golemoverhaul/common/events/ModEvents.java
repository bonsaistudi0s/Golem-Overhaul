package tech.alexnijjar.golemoverhaul.common.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import tech.alexnijjar.golemoverhaul.common.entities.IShearable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;
import tech.alexnijjar.golemoverhaul.common.entities.golems.KelpGolem;

import java.util.Random;

public class ModEvents {

    public static void init() {
        registerShearInteractions();
        registerKelpGolemPlacementDetection();
        registerHayGolemTramplePrevention();
    }

    private static void registerShearInteractions() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (!(entity instanceof IShearable target)) {
                return EventResult.pass();
            }

            if (entity.level().isClientSide) {
                return EventResult.pass();
            }

            var stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof ShearsItem)) {
                return EventResult.pass();
            }

            if (!target.isShearable()) {
                return EventResult.interruptFalse();
            }

            var drops = target.onSheared();
            drops.forEach(dropStack -> {
                var droppedItem = entity.spawnAtLocation(dropStack, 1.0F);
                if (droppedItem != null) {
                    var rand = new Random();
                    droppedItem.setDeltaMovement(droppedItem.getDeltaMovement().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                }
            });

            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

            return EventResult.interruptTrue();
        });
    }

    private static void registerKelpGolemPlacementDetection() {
        BlockEvent.PLACE.register((level, blockPos, blockState, entity) -> {
            if (!(level instanceof ServerLevel serverLevel)) {
                return EventResult.pass();
            }

            if (blockState.is(Blocks.DRIED_KELP_BLOCK) || blockState.is(Blocks.SEA_LANTERN)) {
                // delay the check to the next tick due to a bug in Architectury (13.0.8)
                //  on Fabric, the blockstate at blockPos in the level will still be air
                //  which causes the BlockPattern to not match
                serverLevel.getServer().execute(() -> KelpGolem.trySpawnGolem(level, blockPos));
            }

            return EventResult.pass();
        });
    }

    private static void registerHayGolemTramplePrevention() {
        InteractionEvent.FARMLAND_TRAMPLE.register((level, blockPos, blockState, v, entity) -> {
            if (level.isClientSide()) {
                return EventResult.pass();
            }

            var bounds = blockState.getCollisionShape(level, blockPos).bounds().move(blockPos).inflate(10);

            if (!level.getEntitiesOfClass(HayGolem.class, bounds).isEmpty()) {
                return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });
    }
}
