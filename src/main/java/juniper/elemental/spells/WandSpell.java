package juniper.elemental.spells;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2i;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class WandSpell {
    public static final Codec<WandSpell> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(SpellStep.CODEC.listOf().fieldOf("steps").forGetter(spell -> List.copyOf(spell.steps.values()))).apply(instance, WandSpell::new));
    public static final PacketCodec<ByteBuf, WandSpell> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public Map<Vector2i, SpellStep> steps;

    public WandSpell() {
        this.steps = new HashMap<>();
    }

    public WandSpell(List<SpellStep> steps) {
        this();
        for (SpellStep step : steps) {
            this.steps.put(new Vector2i(step.x, step.y), step);
        }
    }
}
