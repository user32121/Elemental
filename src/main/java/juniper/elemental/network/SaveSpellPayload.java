package juniper.elemental.network;

import io.netty.buffer.ByteBuf;
import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalItems;
import juniper.elemental.spells.WandSpell;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SaveSpellPayload(WandSpell spell) implements CustomPayload {
    public static final Identifier ID = Identifier.of(Elemental.MOD_ID, "save_spell");
    public static final Id<SaveSpellPayload> PAYLOAD_ID = new Id<>(SaveSpellPayload.ID);
    public static final PacketCodec<ByteBuf, SaveSpellPayload> CODEC = PacketCodec.tuple(WandSpell.PACKET_CODEC, SaveSpellPayload::spell, SaveSpellPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }

    public static void handle(SaveSpellPayload payload, Context ctx) {
        PlayerInventory inv = ctx.player().getInventory();
        ItemStack stack = inv.getStack(inv.selectedSlot);
        ElementalItems.WAND.setSpell(stack, payload.spell());
    }
}
