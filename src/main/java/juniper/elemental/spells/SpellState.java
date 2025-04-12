package juniper.elemental.spells;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpellState {
    private static final String CUR_TILE_KEY = "CurrentTile";
    private static final String TICKS_LEFT_KEY = "TicksLeft";

    public @Nullable Vector2i curTile;
    public int ticksLeft = 20 * 60;

    //this is slightly scuffed, but should be ok since all int32 fit in a double
    private double[] register = new double[4];
    private @Nullable Entity registerEntity;

    public int getRegisterInt() {
        return (int) register[0];
    }

    public void setRegisterInt(int value) {
        setRegisterRaw(value, 0, 0, 0);
    }

    public void setRegisterRaw(double v0, double v1, double v2, double v3) {
        register[0] = v0;
        register[1] = v1;
        register[2] = v2;
        register[3] = v3;
    }

    public double[] getRegisterRaw() {
        return register;
    }

    public Vec3d getRegisterVec3d() {
        return new Vec3d(register[0], register[1], register[2]);
    }

    public void setRegisterVec3d(Vec3d value) {
        setRegisterRaw(value.x, value.y, value.z, 0);
    }

    public Entity getRegisterEntity(World world, BlockPos pos) {
        int[] uuidAr = new int[] { (int) register[0], (int) register[1], (int) register[2], (int) register[3] };
        UUID uuid = Uuids.toUuid(uuidAr);
        if (registerEntity != null && registerEntity.getUuid().equals(uuid)) {
            return registerEntity;
        } else {
            //not sure if there is a more efficient method
            for (Entity e : world.getEntitiesByClass(Entity.class, new Box(pos).expand(32), entity -> entity.getUuid().equals(uuid))) {
                return e;
            }
        }
        return null;
    }

    public void setRegisterEntity(Entity value) {
        if (value == null) {
            setRegisterRaw(0, 0, 0, 0);
        } else {
            int[] uuid = Uuids.toIntArray(value.getUuid());
            setRegisterRaw(uuid[0], uuid[1], uuid[2], uuid[3]);
        }
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(TICKS_LEFT_KEY, ticksLeft);
        if (curTile != null) {
            nbt.putIntArray(CUR_TILE_KEY, new int[] { curTile.x, curTile.y });
        }
        return nbt;
    }

    public static SpellState fromNbt(NbtCompound nbt) {
        SpellState state = new SpellState();
        state.ticksLeft = nbt.getInt(TICKS_LEFT_KEY);
        int[] ar = nbt.getIntArray(CUR_TILE_KEY);
        if (ar.length >= 2) {
            state.curTile = new Vector2i(ar[0], ar[1]);
        }
        return state;
    }
}
