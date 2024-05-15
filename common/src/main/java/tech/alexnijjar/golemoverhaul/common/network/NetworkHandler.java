package tech.alexnijjar.golemoverhaul.common.network;


import com.teamresourceful.resourcefullib.common.network.Network;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.network.packets.ServerboundGolemSummonPacket;

public class NetworkHandler {

    public static final Network CHANNEL = new Network(new ResourceLocation(GolemOverhaul.MOD_ID, "main"), 1);

    public static void init() {
        CHANNEL.register(ServerboundGolemSummonPacket.TYPE);
    }
}
