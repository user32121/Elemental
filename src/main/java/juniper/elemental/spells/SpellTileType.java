package juniper.elemental.spells;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;

public record SpellTileType(String name, Identifier texture, BiConsumer<SpellState, SpellEntity> execute, List<Pair<String, SpellProperty>> properties) implements StringIdentifiable {
    public enum SpellProperty {
        INTEGER;
    }

    private static List<SpellTileType> makeAll() {
        List<SpellTileType> all = new ArrayList<>();
        all.add(START);
        all.add(make("nop", NOP, List.of()));
        all.add(make("debug", TODO, List.of()));
        all.add(make("add", TODO, List.of(new Pair<>("value", SpellProperty.INTEGER))));
        return all;
    }

    public static BiConsumer<SpellState, SpellEntity> NOP = (state, entity) -> {
    };
    public static BiConsumer<SpellState, SpellEntity> TODO = (state, entity) -> {
        Entity owner = entity.getOwner();
        if (owner instanceof PlayerEntity player) {
            player.sendMessage(Text.of("TODO"), false);
        }
    };
    public static final SpellTileType START = make("start", NOP, List.of());
    public static final List<SpellTileType> ALL = makeAll();
    public static final Codec<SpellTileType> CODEC = StringIdentifiable.createBasicCodec(() -> ALL.toArray(SpellTileType[]::new));
    public static final PacketCodec<ByteBuf, SpellTileType> PACKET_CODEC = PacketCodecs.codec(CODEC);

    private static SpellTileType make(String name, BiConsumer<SpellState, SpellEntity> execute, List<Pair<String, SpellProperty>> properties) {
        return new SpellTileType(name, Identifier.of(Elemental.MOD_ID, "item/wand/" + name), execute, properties);
    }

    @Override
    public String asString() {
        return name;
    }
}