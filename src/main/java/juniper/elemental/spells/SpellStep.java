package juniper.elemental.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.function.ValueLists.OutOfBoundsHandling;

public class SpellStep {
    public enum Direction implements StringIdentifiable {
        UP, LEFT, DOWN, RIGHT;

        public static final Codec<Direction> CODEC = StringIdentifiable.createCodec(Direction::values);
        public static final PacketCodec<ByteBuf, Direction> PACKET_CODEC = PacketCodecs.indexed(ValueLists.createIdToValueFunction(Direction::ordinal, Direction.values(), OutOfBoundsHandling.WRAP),
                Direction::ordinal);

        @Override
        public String asString() {
            return toString();
        }
    }

    public static final Codec<SpellStep> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.INT.fieldOf("x").forGetter(step -> step.x), Codec.INT.fieldOf("y").forGetter(step -> step.y), Direction.CODEC.fieldOf("next").forGetter(step -> step.next),
                    SpellStepType.CODEC.fieldOf("type").forGetter(step -> step.type))
                    .apply(instance, SpellStep::new));
    public static final PacketCodec<ByteBuf, SpellStep> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, spell -> spell.x, PacketCodecs.INTEGER, spell -> spell.y, Direction.PACKET_CODEC,
            spell -> spell.next, SpellStepType.PACKET_CODEC, spell -> spell.type, SpellStep::new);

    public final int x;
    public final int y;
    public Direction next;
    public SpellStepType type;

    public SpellStep(int x, int y, Direction next, SpellStepType type) {
        this.x = x;
        this.y = y;
        this.next = next;
        this.type = type;
    }
}
