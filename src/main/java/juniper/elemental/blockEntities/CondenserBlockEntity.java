package juniper.elemental.blockEntities;

import juniper.elemental.Elemental;
import juniper.elemental.blocks.ElementHolder;
import juniper.elemental.elements.ElementSignal;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CondenserBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    public static final Item[][] FUEL_ITEMS = {
            { Items.BIG_DRIPLEAF, Items.BLUE_ORCHID, Items.LILY_OF_THE_VALLEY, Items.POPPY },
            { Items.COPPER_BLOCK, Items.LAPIS_BLOCK, Items.IRON_BLOCK, Items.COAL_BLOCK },
            { Items.GOLDEN_CARROT, Items.PRISMARINE_SHARD, Items.WIND_CHARGE, Items.FIRE_CHARGE } };
    public static final ElementSignal[] FUEL_TYPES = { ElementSignal.EARTH1, ElementSignal.WATER1, ElementSignal.AIR1,
            ElementSignal.FIRE1 };
    public static final int CONDENSE_TIME_MAX = 60 * 20;
    public static final int FUEL_TIME_MAX = 15 * 20;
    public static final String FUEL_TIME_KEY = "FuelTime";
    public static final String FUEL_TYPE_KEY = "FuelType";
    public static final String CONDENSE_TIME_KEY = "CondenseTime";
    public static final String CONDENSE_TYPE_KEY = "CondenseType";
    private final SimpleInventory inventory = new SimpleInventory(3);
    private int fuelTimeLeft = 0;
    private ElementSignal fuelType = ElementSignal.OFF;
    private int condenseTime = 0;
    private ElementSignal condenseType = ElementSignal.OFF;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> fuelTimeLeft;
                case 1 -> fuelType.ordinal();
                case 2 -> condenseTime;
                case 3 -> condenseType.ordinal();
                default -> throw new IndexOutOfBoundsException(index);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    fuelTimeLeft = value;
                    break;
                case 1:
                    fuelType = ElementSignal.VALUES[value];
                    break;
                case 2:
                    condenseTime = value;
                    break;
                case 3:
                    condenseType = ElementSignal.VALUES[value];
                    break;
                default:
                    throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public CondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.CONDENSER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CondenserScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    private ElementSignal valueOrDefault(String name, ElementSignal def) {
        try {
            return ElementSignal.valueOf(name);
        } catch (IllegalArgumentException e) {
            Elemental.LOGGER.warn("invalid enum", e);
            return def;
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registries);
        fuelTimeLeft = nbt.getShort(FUEL_TIME_KEY);
        fuelType = valueOrDefault(nbt.getString(FUEL_TYPE_KEY), ElementSignal.OFF);
        condenseTime = nbt.getShort(CONDENSE_TIME_KEY);
        condenseType = valueOrDefault(nbt.getString(CONDENSE_TYPE_KEY), ElementSignal.OFF);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), registries);
        nbt.putShort(FUEL_TIME_KEY, (short) fuelTimeLeft);
        nbt.putString(FUEL_TYPE_KEY, fuelType.toString());
        nbt.putShort(CONDENSE_TIME_KEY, (short) condenseTime);
        nbt.putString(CONDENSE_TYPE_KEY, condenseType.toString());
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
        for (Item item : FUEL_ITEMS[slot]) {
            if (stack.isOf(item)) {
                return true;
            }
        }
        return false;
    }

    private static void tryConsumeFuel(CondenserBlockEntity blockEntity) {
        for (int i = 0; i < FUEL_TYPES.length; ++i) {
            boolean valid = true;
            for (int j = 0; j < FUEL_ITEMS.length; ++j) {
                if (!blockEntity.inventory.getStack(j).isOf(FUEL_ITEMS[j][i])) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                for (int j = 0; j < FUEL_ITEMS.length; ++j) {
                    blockEntity.inventory.getStack(j).decrement(1);
                }
                blockEntity.fuelTimeLeft = FUEL_TIME_MAX;
                blockEntity.fuelType = FUEL_TYPES[i];
                return;
            }
        }
    }

    private static void tickCondensing(World world, BlockPos pos, BlockState state, CondenserBlockEntity blockEntity) {
        if (blockEntity.fuelTimeLeft > 0
                && (blockEntity.fuelType.equals(blockEntity.condenseType) || blockEntity.condenseTime <= 0)) {
            //has fuel and matching types: forward progress
            ++blockEntity.condenseTime;
            blockEntity.condenseType = blockEntity.fuelType;
            if (blockEntity.condenseTime >= CONDENSE_TIME_MAX) {
                blockEntity.condenseTime = 0;
                world.setBlockState(pos, state.with(ElementHolder.SIGNAL, blockEntity.condenseType));
            }
        } else if (blockEntity.condenseTime > 0) {
            //backward progress
            blockEntity.condenseTime -= 2;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, CondenserBlockEntity blockEntity) {
        if (blockEntity.fuelTimeLeft <= 0) {
            tryConsumeFuel(blockEntity);
        }
        tickCondensing(world, pos, state, blockEntity);
        if (blockEntity.fuelTimeLeft > 0) {
            --blockEntity.fuelTimeLeft;
        }
    }
}
