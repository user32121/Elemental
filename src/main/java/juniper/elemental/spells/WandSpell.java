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
            .create(instance -> instance.group(SpellTile.CODEC.listOf().fieldOf("tiles").forGetter(spell -> List.copyOf(spell.tiles.values()))).apply(instance, WandSpell::new));
    public static final PacketCodec<ByteBuf, WandSpell> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public Map<Vector2i, SpellTile> tiles;

    public WandSpell() {
        this.tiles = new HashMap<>();
    }

    public WandSpell(List<SpellTile> tiles) {
        this();
        for (SpellTile tile : tiles) {
            this.tiles.put(new Vector2i(tile.x, tile.y), tile);
        }
    }
}
