package juniper.elemental.spells;

import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import juniper.elemental.Elemental;
import juniper.elemental.entities.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public record SpellTileType(String name, Identifier texture, BiConsumer<SpellState, SpellEntity> execute) implements StringIdentifiable {

    public static BiConsumer<SpellState, SpellEntity> NOP = (state, entity) -> {
    };
    public static BiConsumer<SpellState, SpellEntity> TODO = (state, entity) -> {
        Entity owner = entity.getOwner();
        if (owner instanceof PlayerEntity player) {
            player.sendMessage(Text.of("TODO"), false);
        }
    };
    public static final SpellTileType START = make("start", NOP);
    public static final SpellTileType[] ALL = new SpellTileType[] { START, make("nop", NOP), make("debug", TODO), make("add", TODO) };
    public static final Codec<SpellTileType> CODEC = StringIdentifiable.createBasicCodec(() -> ALL);
    public static final PacketCodec<ByteBuf, SpellTileType> PACKET_CODEC = PacketCodecs.codec(CODEC);

    private static SpellTileType make(String name, BiConsumer<SpellState, SpellEntity> execute) {
        return new SpellTileType(name, Identifier.of(Elemental.MOD_ID, "item/wand/" + name), execute);
    }

    @Override
    public String asString() {
        return name;
    }
}