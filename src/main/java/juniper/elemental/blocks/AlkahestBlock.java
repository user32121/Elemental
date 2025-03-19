package juniper.elemental.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;

public class AlkahestBlock extends Block {
    public static final Property<Integer> LAYERS = IntProperty.of("level", 1, 16);
    public static final int TICK_RATE = 5;

    public AlkahestBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 16));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LAYERS);
    }

    // @Override
    // protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    //     return VoxelShapes.empty();
    // }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        world.scheduleBlockTick(pos, state.getBlock(), TICK_RATE);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, state.getBlock(), TICK_RATE);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        //fall
        int layer = state.get(LAYERS);
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.isOf(state.getBlock())) {
            int belowLayer = belowState.get(LAYERS);
            if (16 - belowLayer >= layer) {
                //all fluid can flow to below block
                world.setBlockState(pos.down(), belowState.with(LAYERS, belowLayer + layer));
                world.removeBlock(pos, false);
                return;
            } else {
                //only some fluid can flow to below block
                world.setBlockState(pos.down(), belowState.with(LAYERS, 16));
                world.setBlockState(pos, state.with(LAYERS, layer = layer - (16 - belowLayer)));
            }
        } else if (belowState.isReplaceable()) {
            world.setBlockState(pos.down(), state);
            world.removeBlock(pos, false);
            return;
        }
        //spread
        for (Direction dir : Direction.shuffle(random)) {
            if (!dir.getAxis().isHorizontal()) {
                continue;
            }
            if (layer <= 1) {
                break;
            }
            BlockState sideState = world.getBlockState(pos.offset(dir));
            if (sideState.isOf(state.getBlock())) {
                int sideLayer = sideState.get(LAYERS);
                if (layer > sideLayer + 1) {
                    world.setBlockState(pos.offset(dir), sideState.with(LAYERS, sideLayer + 1));
                    world.setBlockState(pos, state.with(LAYERS, --layer));
                }
            } else if (sideState.isReplaceable()) {
                world.setBlockState(pos.offset(dir), state.with(LAYERS, 1));
                world.setBlockState(pos, state.with(LAYERS, --layer));
            }
        }
    }
}
