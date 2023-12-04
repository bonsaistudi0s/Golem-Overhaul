package tech.alexnijjar.golemoverhaul.common.network.messages;


import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;

import java.util.function.Consumer;

public class ServerboundGolemSummonPacket implements Packet<ServerboundGolemSummonPacket> {

    public static final ServerboundPacketType<ServerboundGolemSummonPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundGolemSummonPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundGolemSummonPacket> {

        @Override
        public Class<ServerboundGolemSummonPacket> type() {
            return ServerboundGolemSummonPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(GolemOverhaul.MOD_ID, "golem_summon");
        }

        @Override
        public void encode(ServerboundGolemSummonPacket message, FriendlyByteBuf buffer) {}

        @Override
        public ServerboundGolemSummonPacket decode(FriendlyByteBuf buffer) {
            return new ServerboundGolemSummonPacket();
        }

        @Override
        public Consumer<Player> handle(ServerboundGolemSummonPacket message) {
            return player -> {
                if (player.getVehicle() instanceof NetheriteGolem golem) {
                    golem.startSummon();
                }
            };
        }
    }
}
