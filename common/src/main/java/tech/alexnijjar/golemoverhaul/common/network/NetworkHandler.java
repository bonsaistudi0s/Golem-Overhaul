package tech.alexnijjar.golemoverhaul.common.network;


import com.teamresourceful.resourcefullib.common.network.NetworkChannel;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.network.messages.ServerboundGolemSummonPacket;

public class NetworkHandler {
    public static final NetworkChannel CHANNEL = new NetworkChannel(GolemOverhaul.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.register(ServerboundGolemSummonPacket.TYPE);
    }
}
