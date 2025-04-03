package juniper.elemental.init;

import juniper.elemental.network.SaveSpellPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ElementalPackets {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(SaveSpellPayload.PAYLOAD_ID, SaveSpellPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SaveSpellPayload.PAYLOAD_ID, SaveSpellPayload::handle);
    }
}
