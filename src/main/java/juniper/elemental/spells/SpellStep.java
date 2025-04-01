package juniper.elemental.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringIdentifiable;

public class SpellStep {
    public enum Direction implements StringIdentifiable {
        UP, DOWN, LEFT, RIGHT;

        public static final Codec<Direction> CODEC = StringIdentifiable.createCodec(Direction::values);

        @Override
        public String asString() {
            return toString();
        }
    }

    public static final Codec<SpellStep> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.INT.fieldOf("x").forGetter(step -> step.x), Codec.INT.fieldOf("y").forGetter(step -> step.y), Direction.CODEC.fieldOf("next").forGetter(step -> step.next))
                    .apply(instance, SpellStep::new));

    public final int x;
    public final int y;
    public Direction next;

    public SpellStep(int x, int y, Direction next) {
        this.x = x;
        this.y = y;
        this.next = next;
    }
}
