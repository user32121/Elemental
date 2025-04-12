package juniper.elemental.entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2i;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;

import juniper.elemental.spells.SpellState;
import juniper.elemental.spells.SpellTile;
import juniper.elemental.spells.SpellTileType;
import juniper.elemental.spells.WandSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SpellEntity extends ProjectileEntity {
    private static final String SPELL_KEY = "Spell";
    private static final String STATE_KEY = "State";

    private WandSpell spell;
    private SpellState state = new SpellState();

    public SpellEntity(EntityType<? extends SpellEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        //NO OP
    }

    public void setSpell(WandSpell spell) {
        this.spell = spell;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.spell != null) {
            DataResult<NbtElement> res = WandSpell.CODEC.encode(spell, NbtOps.INSTANCE, NbtOps.INSTANCE.empty());
            nbt.put(SPELL_KEY, res.result().get());
        }
        nbt.put(STATE_KEY, state.toNbt());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        NbtElement nbt2 = nbt.get(SPELL_KEY);
        if (nbt2 != null) {
            DataResult<Pair<WandSpell, NbtElement>> res = WandSpell.CODEC.decode(NbtOps.INSTANCE, nbt2);
            spell = res.getOrThrow().getFirst();
        }
        nbt2 = nbt.get(STATE_KEY);
        if (nbt2 != null && nbt2 instanceof NbtCompound nbt3) {
            state = SpellState.fromNbt(nbt3);
        }
    }

    @Override
    public void tick() {
        super.tick();
        //no spell, do nothing
        if (this.spell == null) {
            return;
        }
        //no more ticks, terminate
        if (state.ticksLeft <= 0) {
            if (getWorld() instanceof ServerWorld world) {
                kill(world);
            }
            return;
        }
        //first tick spent finding start tile
        if (state.curTile == null) {
            state.curTile = getStart();
            return;
        }
        //traveled to blank tile, terminate
        SpellTile tile = spell.tiles.get(state.curTile);
        if (tile == null) {
            if (getWorld() instanceof ServerWorld world) {
                kill(world);
            }
            return;
        }
        //normal execution
        tile.type.execute().accept(state, this, tile);
        --state.ticksLeft;
        state.curTile = state.curTile.add(tile.next.asVec2i());
    }

    private Vector2i getStart() {
        List<Vector2i> starts = new ArrayList<>();
        for (SpellTile tile : spell.tiles.values()) {
            if (tile.type == SpellTileType.START) {
                starts.add(new Vector2i(tile.x, tile.y));
            }
        }
        if (starts.isEmpty()) {
            if (this.getOwner() instanceof PlayerEntity player) {
                player.sendMessage(Text.of("Spell contains no start tiles"), false);
                if (getWorld() instanceof ServerWorld world) {
                    kill(world);
                }
            }
            return null;
        }
        return starts.get(getRandom().nextInt(starts.size()));
    }
}
