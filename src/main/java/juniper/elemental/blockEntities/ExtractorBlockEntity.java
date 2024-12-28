package juniper.elemental.blockEntities;

import java.util.ArrayList;
import java.util.List;

import juniper.elemental.blocks.ElementHolder;
import juniper.elemental.elements.ElementSignal;
import juniper.elemental.init.ElementalBlockEntities;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalItems;
import juniper.elemental.screens.ExtractorScreenHandler;
import net.minecraft.block.Block;
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
import net.minecraft.world.World.ExplosionSourceType;

public class ExtractorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    public static final List<Pair<Vec3i, Block>> BLOCK_LIST;
    public static final List<Pair<Vec3i, TagKey<Block>>> TAG_LIST;
    public static final Item[] SHARD_ITEMS = { ElementalItems.EARTH_SHARD, ElementalItems.WATER_SHARD, ElementalItems.AIR_SHARD, ElementalItems.FIRE_SHARD, Items.AIR };
    public static final int MAX_PROGRESS = 1024;
    public static final String FLAGS_KEY = "Flags";
    public static final String PROGRESS_KEY = "Progress";
    private final SimpleInventory inventory = new SimpleInventory(5);
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> {
                    int value = 0;
                    value |= isMultiblockValid ? (1 << 0) : 0;
                    for (int i = 0; i < hasElement.length; ++i) {
                        value |= hasElement[i] ? (1 << (i + 1)) : 0;
                    }
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
                    for (int i = 0; i < hasElement.length; ++i) {
                        hasElement[i] = (value & (1 << (i + 1))) != 0;
                    }
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
    private boolean[] hasElement = new boolean[4];
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
                //doesn't actually check block states but it doesn't really matter
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
        return new ExtractorScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExtractorBlockEntity blockEntity) {
        //multiblock check
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
        //absorb elements
        for (int x = -2; x <= 2; x += 4) {
            for (int y = -2; y <= 2; y += 4) {
                for (int z = -2; z <= 2; z += 4) {
                    BlockPos targetPos = pos.add(x, y, z);
                    BlockState targetState = world.getBlockState(targetPos);
                    ElementSignal signal;
                    if (targetState.getBlock() instanceof ElementHolder && (signal = targetState.get(ElementHolder.SIGNAL)).is_active) {
                        if (blockEntity.hasElement[signal.elementOrdinal]) {
                            world.createExplosion(null, null, null, pos.toCenterPos(), 10, true, ExplosionSourceType.BLOCK);
                            return;
                        }
                        blockEntity.hasElement[signal.elementOrdinal] = true;
                        world.setBlockState(targetPos, targetState.with(ElementHolder.SIGNAL, ElementSignal.OFF));
                    }
                }
            }
        }
        //inventory/element check
        for (boolean b : blockEntity.hasElement) {
            if (!b) {
                return;
            }
        }
        for (int i = 0; i < 4; ++i) {
            if (blockEntity.getStack(i).isEmpty()) {
                return;
            }
        }
        //consume elements/items and increase progress
        for (int i = 0; i < blockEntity.hasElement.length; ++i) {
            blockEntity.hasElement[i] = false;
        }
        for (int i = 0; i < 4; ++i) {
            blockEntity.removeStack(i, 1);
        }
        ++blockEntity.progress;
        if (blockEntity.progress >= MAX_PROGRESS) {
            blockEntity.clear();
            world.setBlockState(pos, ElementalBlocks.DARK_PORTAL.getDefaultState());
        }
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

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registries);
        propertyDelegate.set(0, nbt.getInt(FLAGS_KEY));
        progress = nbt.getInt(PROGRESS_KEY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), registries);
        nbt.putInt(FLAGS_KEY, propertyDelegate.get(0));
        nbt.putInt(PROGRESS_KEY, progress);
    }
}
