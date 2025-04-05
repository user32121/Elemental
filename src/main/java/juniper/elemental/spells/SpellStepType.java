package juniper.elemental.spells;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import juniper.elemental.Elemental;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public record SpellStepType(String name, Identifier texture) implements StringIdentifiable {
    public static final SpellStepType[] ALL = new SpellStepType[] { make("nop"), make("debug"), make("add") };
    public static final Codec<SpellStepType> CODEC = StringIdentifiable.createBasicCodec(() -> ALL);
    public static final PacketCodec<ByteBuf, SpellStepType> PACKET_CODEC = PacketCodecs.codec(CODEC);

    private static SpellStepType make(String name) {
        return new SpellStepType(name, Identifier.of(Elemental.MOD_ID, "item/wand/" + name));
    }

    @Override
    public String asString() {
        return name;
    }
}