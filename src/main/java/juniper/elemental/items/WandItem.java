package juniper.elemental.items;

import juniper.elemental.entities.SpellEntity;
import juniper.elemental.init.ElementalComponents;
import juniper.elemental.init.ElementalEntities;
import juniper.elemental.init.ElementalItems;
import juniper.elemental.init.ElementalScreenHandlers;
import juniper.elemental.spells.WandSpell;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return super.use(world, user, hand);
        }
        if (user.isSneaking()) {
            return configure(world, user, hand);
        } else {
            return cast(world, user, hand);
        }
    }

    public ActionResult configure(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.openHandledScreen(new ExtendedScreenHandlerFactory<WandSpell>() {
            @Override
            public Text getDisplayName() {
                return stack.getName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return ElementalScreenHandlers.WAND.create(syncId, playerInventory, ElementalItems.WAND.getSpell(stack));
            }

            @Override
            public WandSpell getScreenOpeningData(ServerPlayerEntity player) {
                return ElementalItems.WAND.getSpell(stack);
            }
        });
        return ActionResult.SUCCESS;
    }

    public ActionResult cast(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SpellEntity spell = ElementalEntities.SPELL.create(world, SpawnReason.TRIGGERED);
            spell.setPosition(user.getEyePos());
            spell.setAngles(user.getYaw(), user.getPitch());
            spell.setOwner(user);
            spell.setSpell(getSpell(user.getStackInHand(hand)));
            world.spawnEntity(spell);
        }
        return ActionResult.SUCCESS;
    }

    public WandSpell getSpell(ItemStack stack) {
        if (!stack.contains(ElementalComponents.SPELL)) {
            stack.set(ElementalComponents.SPELL, new WandSpell());
        }
        return stack.get(ElementalComponents.SPELL);
    }

    public void setSpell(ItemStack stack, WandSpell value) {
        stack.set(ElementalComponents.SPELL, value);
    }
}
