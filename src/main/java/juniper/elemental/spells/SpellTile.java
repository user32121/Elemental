package juniper.elemental.spells;

import org.joml.Vector2i;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.function.ValueLists.OutOfBoundsHandling;

public class SpellTile {
    public enum Direction implements StringIdentifiable {
        UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0);

        public static final Codec<Direction> CODEC = StringIdentifiable.createCodec(Direction::values);
        public static final PacketCodec<ByteBuf, Direction> PACKET_CODEC = PacketCodecs.indexed(ValueLists.createIdToValueFunction(Direction::ordinal, Direction.values(), OutOfBoundsHandling.WRAP),
                Direction::ordinal);
        public final int x;
        public final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector2i asVec2i() {
            return new Vector2i(x, y);
        }

        @Override
        public String asString() {
            return toString();
        }
    }

    public static final Codec<SpellTile> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf("x").forGetter(step -> step.x), Codec.INT.fieldOf("y").forGetter(step -> step.y),
                    Direction.CODEC.fieldOf("next").forGetter(step -> step.next), SpellTileType.CODEC.fieldOf("type").forGetter(step -> step.type)).apply(instance, SpellTile::new));
    public static final PacketCodec<ByteBuf, SpellTile> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, spell -> spell.x, PacketCodecs.INTEGER, spell -> spell.y, Direction.PACKET_CODEC,
            spell -> spell.next, SpellTileType.PACKET_CODEC, spell -> spell.type, SpellTile::new);

    public final int x;
    public final int y;
    public Direction next;
    public SpellTileType type;

    public SpellTile(int x, int y, Direction next, SpellTileType type) {
        this.x = x;
        this.y = y;
        this.next = next;
        this.type = type;
    }
}
