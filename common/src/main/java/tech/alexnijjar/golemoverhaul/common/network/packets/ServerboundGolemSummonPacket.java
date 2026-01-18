package tech.alexnijjar.golemoverhaul.common.network.packets;


import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.DatalessPacketType;
import net.minecraft.world.entity.player.Player;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.golems.NetheriteGolem;

import java.util.function.Consumer;

public class ServerboundGolemSummonPacket implements Packet<ServerboundGolemSummonPacket> {

    public static final ServerboundPacketType<ServerboundGolemSummonPacket> TYPE
            = new DatalessPacketType.Server<>(ServerboundGolemSummonPacket.class, GolemOverhaul.asResource("golem_summon"), ServerboundGolemSummonPacket::new) {
        @Override
        public Consumer<Player> handle(ServerboundGolemSummonPacket serverboundGolemSummonPacket) {
            return player -> {
                if (player.getVehicle() instanceof NetheriteGolem golem) {
                    golem.summon();
                }
            };
        }
    };

    @Override
    public PacketType<ServerboundGolemSummonPacket> type() {
        return TYPE;
    }
}
