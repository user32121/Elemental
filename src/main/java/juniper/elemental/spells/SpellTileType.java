package juniper.elemental.spells;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.function.TriConsumer;
import org.joml.Vector2i;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import juniper.elemental.Elemental;
import juniper.elemental.entities.SpellEntity;
import juniper.elemental.spells.SpellTile.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record SpellTileType(String name, Identifier texture, TriConsumer<SpellState, SpellEntity, SpellTile> execute, List<Pair<String, SpellProperty>> properties) implements StringIdentifiable {
    public enum SpellProperty {
        NUMBER, DIRECTION
    }

    private static List<SpellTileType> makeAll() {
        List<SpellTileType> all = new ArrayList<>();
        all.add(START);
        all.add(make("nop", NOP, List.of()));
        all.add(make("debug", (state, entity, tile) -> {
            double[] primaryValues = state.getRegisterRaw(true);
            double[] secondaryValues = state.getRegisterRaw(false);
            sendOwnerMessage(entity, Text.of(String.format("primary: [%s, %s, %s, %s]\nsecondary: [%s, %s, %s, %s]", primaryValues[0], primaryValues[1], primaryValues[2], primaryValues[3],
                    secondaryValues[0], secondaryValues[1], secondaryValues[2], secondaryValues[3])));
        }, List.of()));
        all.add(make("swap", (state, entity, tile) -> {
            state.swapRegisters();
        }, List.of()));
        all.add(make("copy", (state, entity, tile) -> {
            state.setRegisterRaw(true, state.getRegisterRaw(false));
            state.setCachedRegisterEntity(true, state.getCachedRegisterEntity(false));
        }, List.of()));
        all.add(make("constant", (state, entity, tile) -> {
            state.setRegisterDouble(true, tile.properties.getOrDefault("value", 0.0));
        }, List.of(new Pair<>("value", SpellProperty.NUMBER))));
        //TODO remove constants from add/multiply
        all.add(make("add", (state, entity, tile) -> {
            state.setRegisterDouble(true, state.getRegisterDouble(true) + state.getRegisterDouble(false) + tile.properties.getOrDefault("value", 0.0));
        }, List.of(new Pair<>("value", SpellProperty.NUMBER))));
        all.add(make("multiply_vector", (state, entity, tile) -> {
            state.setRegisterVec3d(true, state.getRegisterVec3d(true).multiply(state.getRegisterDouble(false) * tile.properties.getOrDefault("value", 0.0)));
        }, List.of(new Pair<>("value", SpellProperty.NUMBER))));
        all.add(make("get_self", (state, entity, tile) -> {
            state.setRegisterEntity(true, entity);
        }, List.of()));
        all.add(make("get_caster", (state, entity, tile) -> {
            state.setRegisterEntity(true, entity.getOwner());
        }, List.of()));
        all.add(make("get_look", (state, entity, tile) -> {
            Entity e = state.getRegisterEntity(true, entity.getWorld(), entity.getBlockPos());
            if (e != null) {
                float pitch = e.getPitch();
                float yaw = e.getYaw();
                Vec3d dir = new Vec3d(MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE) * -MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE),
                        -MathHelper.sin(pitch * MathHelper.RADIANS_PER_DEGREE), MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE));
                state.setRegisterVec3d(true, dir);
            }
        }, List.of()));
        all.add(make("add_velocity", (state, entity, tile) -> {
            Entity target = state.getRegisterEntity(true, entity.getWorld(), entity.getBlockPos());
            Vec3d velocity = state.getRegisterVec3d(false);
            if (target == null) {
                sendOwnerMessage(entity, Text.of("Primary register does not contain valid entity"));
                return;
            }
            target.addVelocity(velocity);
            target.velocityModified = true;
        }, List.of()));
        all.add(make("get_nearest_entity", (state, spell, tile) -> {
            Entity nearest = null;
            for (Entity e : spell.getWorld().getOtherEntities(spell, new Box(spell.getBlockPos()).expand(32))) {
                if (nearest == null || e.squaredDistanceTo(spell) < nearest.squaredDistanceTo(spell)) {
                    nearest = e;
                }
            }
            state.setRegisterEntity(true, nearest);
        }, List.of()));
        all.add(make("constant_vector", (state, spell, tile) -> {
            state.setRegisterVec3d(true, new Vec3d(tile.properties.getOrDefault("x", 0.0), tile.properties.getOrDefault("y", 0.0), tile.properties.getOrDefault("z", 0.0)));
        }, List.of(new Pair<>("x", SpellProperty.NUMBER), new Pair<>("y", SpellProperty.NUMBER), new Pair<>("z", SpellProperty.NUMBER))));
        all.add(make("branch_equals", (state, spell, tile) -> {
            double[] r1 = state.getRegisterRaw(true);
            double[] r2 = state.getRegisterRaw(false);
            if (r1[0] == r2[0] && r1[1] == r2[1] && r1[2] == r2[2] && r1[3] == r2[3]) {
                state.nextTile = state.curTile.add(Direction.fromId((int) (double) tile.properties.get("branch")).asVec2i(), new Vector2i());
            }
        }, List.of(new Pair<>("branch", SpellProperty.DIRECTION))));
        //TODO get_collided
        return all;
    }

    public static TriConsumer<SpellState, SpellEntity, SpellTile> NOP = (state, entity, tile) -> {
    };
    public static final SpellTileType START = make("start", NOP, List.of());
    public static final List<SpellTileType> ALL = makeAll();
    public static final Codec<SpellTileType> CODEC = StringIdentifiable.createBasicCodec(() -> ALL.toArray(SpellTileType[]::new));
    public static final PacketCodec<ByteBuf, SpellTileType> PACKET_CODEC = PacketCodecs.codec(CODEC);

    private static SpellTileType make(String name, TriConsumer<SpellState, SpellEntity, SpellTile> execute, List<Pair<String, SpellProperty>> properties) {
        return new SpellTileType(name, Identifier.of(Elemental.MOD_ID, "item/wand/" + name), execute, properties);
    }

    public static void sendOwnerMessage(SpellEntity sender, Text message) {
        if (sender.getOwner() instanceof PlayerEntity player) {
            player.sendMessage(message, false);
        }
    }

    @Override
    public String asString() {
        return name;
    }
}