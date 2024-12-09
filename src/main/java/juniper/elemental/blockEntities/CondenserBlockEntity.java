package juniper.elemental.blockEntities;

import juniper.elemental.init.ElementalBlockEntities;
import juniper.elemental.screens.CondenserScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class CondenserBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    public static final Item[][] ALLOWED_ITEMS = {
            { Items.BIG_DRIPLEAF, Items.BLUE_ORCHID, Items.LILY_OF_THE_VALLEY, Items.POPPY },
            { Items.COPPER_BLOCK, Items.LAPIS_BLOCK, Items.IRON_BLOCK, Items.COAL_BLOCK },
            { Items.GOLDEN_CARROT, Items.PRISMARINE_SHARD, Items.WIND_CHARGE, Items.FIRE_CHARGE } };
    private final SimpleInventory inventory = new SimpleInventory(3);

    public CondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.CONDENSER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CondenserScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registries);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registries);
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return inventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        for (Item item : ALLOWED_ITEMS[slot]) {
            if (stack.isOf(item)) {
                return true;
            }
        }
        return false;
    }
}
