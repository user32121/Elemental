package juniper.elemental.blockEntities;

import java.util.ArrayList;
import java.util.List;

import juniper.elemental.init.ElementalBlockEntities;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalItems;
import juniper.elemental.screens.ExtractorScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class ExtractorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    public static final List<Pair<Vec3i, Block>> BLOCK_LIST;
    public static final List<Pair<Vec3i, TagKey<Block>>> TAG_LIST;
    public static final Item[] SHARD_ITEMS = { ElementalItems.EARTH_SHARD, ElementalItems.WATER_SHARD, ElementalItems.AIR_SHARD, ElementalItems.FIRE_SHARD };
    public static final int MAX_PROGRESS = 1024;
    private final SimpleInventory inventory = new SimpleInventory(4);
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> {
                    int value = 0;
                    value |= isMultiblockValid ? (1 << 0) : 0;
                    yield value;
                }
                case 1 -> progress;
                default -> throw new IndexOutOfBoundsException(index);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    isMultiblockValid = (value & (1 << 0)) != 0;
                    break;
                case 1:
                    progress = value;
                default:
                    throw new IndexOutOfBoundsException(index);
            }
            return;
        }

        @Override
        public int size() {
            return 2;
        }
    };
    private boolean isMultiblockValid;
    private int progress;
    static {
        BLOCK_LIST = new ArrayList<>();
        TAG_LIST = new ArrayList<>();
        for (BlockPos pos : BlockPos.iterate(new BlockPos(-2, -2, -2), new BlockPos(2, 2, 2))) {
            pos = pos.toImmutable();
            int faceCount = (Math.abs(pos.getX()) == 2 ? 1 : 0) + (Math.abs(pos.getY()) == 2 ? 1 : 0) + (Math.abs(pos.getZ()) == 2 ? 1 : 0);
            if (pos.equals(Vec3i.ZERO)) {
                continue;
            } else if (faceCount == 3) {
                BLOCK_LIST.add(new Pair<>(pos, ElementalBlocks.CATALYST));
            } else if (Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) == -pos.getY()) {
                TAG_LIST.add(new Pair<>(pos, BlockTags.STAIRS));
            } else if (faceCount == 2) {
                BLOCK_LIST.add(new Pair<>(pos, ElementalBlocks.CONDUIT));
            }
        }
    }

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.EXTRACTOR, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExtractorScreenHandler(syncId, playerInventory, inventory, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExtractorBlockEntity blockEntity) {
        blockEntity.isMultiblockValid = true;
        for (Pair<Vec3i, Block> pair : BLOCK_LIST) {
            if (!world.getBlockState(pos.add(pair.getLeft())).isOf(pair.getRight())) {
                blockEntity.isMultiblockValid = false;
                return;
            }
        }
        for (Pair<Vec3i, TagKey<Block>> pair : TAG_LIST) {
            if (!world.getBlockState(pos.add(pair.getLeft())).isIn(pair.getRight())) {
                blockEntity.isMultiblockValid = false;
                return;
            }
        }
        //TODO rest of behavior (consuming signals)
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
        return stack.isOf(SHARD_ITEMS[slot]);
    }
}
