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
    private double[] primaryRegister = new double[4];
    private double[] secondaryRegister = new double[4];
    private @Nullable Entity primaryRegisterEntity;
    private @Nullable Entity secondaryRegisterEntity;

    public int getRegisterInt(boolean primary) {
        return (int) getRegisterRaw(primary)[0];
    }

    public void setRegisterInt(boolean primary, int value) {
        setRegisterRaw(primary, value, 0, 0, 0);
    }

    public void setRegisterRaw(boolean primary, double v0, double v1, double v2, double v3) {
        if (primary) {
            primaryRegister[0] = v0;
            primaryRegister[1] = v1;
            primaryRegister[2] = v2;
            primaryRegister[3] = v3;
        } else {
            secondaryRegister[0] = v0;
            secondaryRegister[1] = v1;
            secondaryRegister[2] = v2;
            secondaryRegister[3] = v3;
        }
    }

    public void setRegisterRaw(boolean primary, double[] value) {
        setRegisterRaw(primary, value[0], value[1], value[2], value[3]);
    }

    public double[] getRegisterRaw(boolean primary) {
        if (primary) {
            return primaryRegister;
        } else {
            return secondaryRegister;
        }
    }

    public void setCachedRegisterEntity(boolean primary, Entity value) {
        if (primary) {
            primaryRegisterEntity = value;
        } else {
            secondaryRegisterEntity = value;
        }
    }

    public Entity getCachedRegisterEntity(boolean primary) {
        if (primary) {
            return primaryRegisterEntity;
        } else {
            return secondaryRegisterEntity;
        }
    }

    public Vec3d getRegisterVec3d(boolean primary) {
        double[] register = getRegisterRaw(primary);
        return new Vec3d(register[0], register[1], register[2]);
    }

    public void setRegisterVec3d(boolean primary, Vec3d value) {
        setRegisterRaw(primary, value.x, value.y, value.z, 0);
    }

    public Entity getRegisterEntity(boolean primary, World world, BlockPos pos) {
        double[] register = getRegisterRaw(primary);
        int[] uuidAr = new int[] { (int) register[0], (int) register[1], (int) register[2], (int) register[3] };
        UUID uuid = Uuids.toUuid(uuidAr);
        Entity registerEntity = getCachedRegisterEntity(primary);
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

    public void setRegisterEntity(boolean primary, Entity value) {
        if (value == null) {
            setRegisterRaw(primary, 0, 0, 0, 0);
        } else {
            int[] uuid = Uuids.toIntArray(value.getUuid());
            setRegisterRaw(primary, uuid[0], uuid[1], uuid[2], uuid[3]);
        }
        setCachedRegisterEntity(primary, value);
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

    public void swapRegisters() {
        double[] ar = primaryRegister;
        primaryRegister = secondaryRegister;
        secondaryRegister = ar;
        Entity e = primaryRegisterEntity;
        primaryRegisterEntity = secondaryRegisterEntity;
        secondaryRegisterEntity = e;
    }
}
