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
    private static String SPELL_KEY = "Spell";
    private static String CUR_TILE_KEY = "CurrentTile";

    private WandSpell spell;
    private Vector2i curTile;
    private SpellState state;

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
        if (curTile != null) {
            nbt.putIntArray(CUR_TILE_KEY, new int[] { curTile.x, curTile.y });
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        NbtElement spellNbt = nbt.get(SPELL_KEY);
        if (spellNbt != null) {
            DataResult<Pair<WandSpell, NbtElement>> res = WandSpell.CODEC.decode(NbtOps.INSTANCE, spellNbt);
            spell = res.getOrThrow().getFirst();
        }
        int[] ar = nbt.getIntArray(CUR_TILE_KEY);
        if (ar.length >= 2) {
            curTile = new Vector2i(ar[0], ar[1]);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.spell == null) {
            return;
        }
        if (curTile == null) {
            curTile = getStart();
            return;
        }
        SpellTile tile = spell.tiles.get(curTile);
        if (tile == null) {
            if (getWorld() instanceof ServerWorld world) {
                kill(world);
            }
            return;
        }
        tile.type.execute().accept(state, this);
        curTile = curTile.add(tile.next.asVec2i());
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
