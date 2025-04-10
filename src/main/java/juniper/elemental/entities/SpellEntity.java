package juniper.elemental.entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2i;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;

import juniper.elemental.spells.SpellState;
import juniper.elemental.spells.SpellStep;
import juniper.elemental.spells.SpellStepType;
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
    private static String CUR_STEP_KEY = "CurrentStep";

    private WandSpell spell;
    private Vector2i curStep;
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
        if (curStep != null) {
            nbt.putIntArray(CUR_STEP_KEY, new int[] { curStep.x, curStep.y });
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
        int[] curStepNbt = nbt.getIntArray(CUR_STEP_KEY);
        if (curStepNbt.length >= 2) {
            curStep = new Vector2i(curStepNbt[0], curStepNbt[1]);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.spell == null) {
            return;
        }
        if (curStep == null) {
            curStep = getStart();
            return;
        }
        SpellStep curSpellStep = spell.steps.get(curStep);
        if (curSpellStep == null) {
            if (getWorld() instanceof ServerWorld world) {
                kill(world);
            }
            return;
        }
        curSpellStep.type.execute().accept(state, this);
        curStep = curStep.add(curSpellStep.next.asVec2i());
    }

    private Vector2i getStart() {
        List<Vector2i> starts = new ArrayList<>();
        for (SpellStep step : spell.steps.values()) {
            if (step.type == SpellStepType.START) {
                starts.add(new Vector2i(step.x, step.y));
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
