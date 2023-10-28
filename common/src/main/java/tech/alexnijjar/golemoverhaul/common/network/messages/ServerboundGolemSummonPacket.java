package tech.alexnijjar.golemoverhaul.common.network.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;

public class ServerboundGolemSummonPacket implements Packet<ServerboundGolemSummonPacket> {

    public static final ResourceLocation ID = new ResourceLocation(GolemOverhaul.MOD_ID, "golem_summon");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ServerboundGolemSummonPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<ServerboundGolemSummonPacket> {
        @Override
        public void encode(ServerboundGolemSummonPacket message, FriendlyByteBuf buffer) {}

        @Override
        public ServerboundGolemSummonPacket decode(FriendlyByteBuf buffer) {
            return new ServerboundGolemSummonPacket();
        }

        @Override
        public PacketContext handle(ServerboundGolemSummonPacket message) {
            return (player, level) -> {
                if (player.getVehicle() instanceof NetheriteGolem golem) {
                    golem.startSummon();
                }
            };
        }
    }
}
