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

public record SpellStepType(String name, Identifier texture, BiConsumer<SpellState, SpellEntity> execute) implements StringIdentifiable {

    public static BiConsumer<SpellState, SpellEntity> NOP = (state, entity) -> {
    };
    public static BiConsumer<SpellState, SpellEntity> TODO = (state, entity) -> {
        Entity owner = entity.getOwner();
        if (owner instanceof PlayerEntity player) {
            player.sendMessage(Text.of("TODO"), false);
        }
    };
    public static final SpellStepType START = make("start", NOP);
    public static final SpellStepType[] ALL = new SpellStepType[] { START, make("nop", NOP), make("debug", TODO), make("add", TODO) };
    public static final Codec<SpellStepType> CODEC = StringIdentifiable.createBasicCodec(() -> ALL);
    public static final PacketCodec<ByteBuf, SpellStepType> PACKET_CODEC = PacketCodecs.codec(CODEC);

    private static SpellStepType make(String name, BiConsumer<SpellState, SpellEntity> execute) {
        return new SpellStepType(name, Identifier.of(Elemental.MOD_ID, "item/wand/" + name), execute);
    }

    @Override
    public String asString() {
        return name;
    }
}