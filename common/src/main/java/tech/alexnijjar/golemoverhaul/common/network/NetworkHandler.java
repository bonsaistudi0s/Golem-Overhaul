package tech.alexnijjar.golemoverhaul.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.network.messages.ServerboundGolemSummonPacket;

public class NetworkHandler {
    public static final NetworkChannel CHANNEL = new NetworkChannel(GolemOverhaul.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ServerboundGolemSummonPacket.ID, ServerboundGolemSummonPacket.HANDLER, ServerboundGolemSummonPacket.class);
    }
}
