package juniper.elemental.spells;

import java.util.HashMap;
import java.util.Map;

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
        RIGHT(0, 1, 0), UP(1, 0, -1), LEFT(2, -1, 0), DOWN(3, 0, 1),;

        public static final Codec<Direction> CODEC = StringIdentifiable.createCodec(Direction::values);
        public static final PacketCodec<ByteBuf, Direction> PACKET_CODEC = PacketCodecs.indexed(ValueLists.createIdToValueFunction(Direction::ordinal, Direction.values(), OutOfBoundsHandling.WRAP),
                Direction::ordinal);
        public final int id;
        public final int x;
        public final int y;

        Direction(int id, int x, int y) {
            this.id = id;
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

        public static Direction fromId(int id) {
            for (Direction dir : values()) {
                if (dir.id == id) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("no direction with id " + id);
        }
    }

    public static final Codec<SpellTile> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf("x").forGetter(tile -> tile.x), Codec.INT.fieldOf("y").forGetter(tile -> tile.y),
                    Direction.CODEC.fieldOf("next").forGetter(tile -> tile.next), SpellTileType.CODEC.fieldOf("type").forGetter(tile -> tile.type),
                    Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("properties").forGetter(tile -> tile.properties)).apply(instance, SpellTile::new));
    public static final PacketCodec<ByteBuf, SpellTile> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, tile -> tile.x, PacketCodecs.INTEGER, tile -> tile.y, Direction.PACKET_CODEC,
            tile -> tile.next, SpellTileType.PACKET_CODEC, tile -> tile.type, PacketCodecs.map(HashMap::new, PacketCodecs.STRING, PacketCodecs.DOUBLE), tile -> tile.properties, SpellTile::new);

    public final int x;
    public final int y;
    public Direction next;
    public SpellTileType type;
    public Map<String, Double> properties;

    public SpellTile(int x, int y, Direction next, SpellTileType type) {
        this(x, y, next, type, new HashMap<>());
    }

    public SpellTile(int x, int y, Direction next, SpellTileType type, Map<String, Double> properties) {
        this.x = x;
        this.y = y;
        this.next = next;
        this.type = type;
        this.properties = new HashMap<>(properties);
    }
}
