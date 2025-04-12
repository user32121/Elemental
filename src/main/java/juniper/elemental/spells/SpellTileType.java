package juniper.elemental.spells;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.function.TriConsumer;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record SpellTileType(String name, Identifier texture, TriConsumer<SpellState, SpellEntity, SpellTile> execute, List<Pair<String, SpellProperty>> properties) implements StringIdentifiable {
    public enum SpellProperty {
        INTEGER;
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
            state.setRegisterInt(true, tile.properties.getOrDefault("value", 0));
        }, List.of(new Pair<>("value", SpellProperty.INTEGER))));
        all.add(make("add", (state, entity, tile) -> {
            state.setRegisterInt(true, state.getRegisterInt(true) + state.getRegisterInt(false) + tile.properties.getOrDefault("value", 0));
        }, List.of(new Pair<>("value", SpellProperty.INTEGER))));
        all.add(make("multiply_vector", (state, entity, tile) -> {
            state.setRegisterVec3d(true, state.getRegisterVec3d(true).multiply(state.getRegisterInt(false) * tile.properties.getOrDefault("value", 0)));
        }, List.of(new Pair<>("value", SpellProperty.INTEGER))));
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
            Elemental.LOGGER.info("{}", target.getVelocity());
            target.addVelocity(velocity);
            target.velocityModified = true;
            Elemental.LOGGER.info("{}", target.getVelocity());
        }, List.of()));
        return all;
    }

    public static TriConsumer<SpellState, SpellEntity, SpellTile> NOP = (state, entity, tile) -> {
    };
    public static TriConsumer<SpellState, SpellEntity, SpellTile> TODO = (state, entity, tile) -> {
        sendOwnerMessage(entity, Text.of("TODO"));
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