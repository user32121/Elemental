package juniper.elemental.spells;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import net.minecraft.nbt.NbtCompound;

public class SpellState {
    private static final String CUR_TILE_KEY = "CurrentTile";
    private static final String TICKS_LEFT_KEY = "TicksLeft";

    public @Nullable Vector2i curTile;
    public int ticksLeft = 20 * 60;

    public int[] register = new int[4];

    public int getRegisterInt() {
        return register[0];
    }

    public void setRegisterInt(int value) {
        setRegister(value, 0, 0, 0);
    }

    public void setRegister(int v0, int v1, int v2, int v3) {
        register[0] = v0;
        register[1] = v1;
        register[2] = v2;
        register[3] = v3;
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
