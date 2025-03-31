package juniper.elemental.items;

import juniper.elemental.init.ElementalScreenHandlers;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
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

    public ExtendedScreenHandlerFactory<String> createScreenHandlerFactory(ItemStack stack) {
        return new ExtendedScreenHandlerFactory<String>() {
            @Override
            public Text getDisplayName() {
                return stack.getName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return ElementalScreenHandlers.WAND.create(syncId, playerInventory, "TODO spells (server)");
            }

            @Override
            public String getScreenOpeningData(ServerPlayerEntity player) {
                return "TODO encode spells (client)";
            }
        };
    }

    public ActionResult configure(World world, PlayerEntity user, Hand hand) {
        user.openHandledScreen(createScreenHandlerFactory(user.getStackInHand(hand)));
        return ActionResult.SUCCESS;
    }

    public ActionResult cast(World world, PlayerEntity user, Hand hand) {
        //TODO
        user.sendMessage(Text.of("TODO cast"), false);
        return ActionResult.SUCCESS;
    }
}
