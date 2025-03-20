package juniper.elemental.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;

public class AlkahestBlock extends Block {
    public static final Property<Integer> LAYERS = IntProperty.of("level", 1, 16);
    public static final int TICK_RATE = 5;
    public static final VoxelShape[] SHAPES;

    static {
        List<VoxelShape> shapes = new ArrayList<>();
        for (int i = 0; i <= 16; ++i) {
            shapes.add(VoxelShapes.cuboid(0, 0, 0, 1, i / 16.0, 1));
        }
        SHAPES = shapes.toArray(VoxelShape[]::new);
    }

    public AlkahestBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 16));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LAYERS);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(LAYERS)];
    }

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
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (world instanceof ServerWorld serverWorld) {
            entity.damage(serverWorld, world.getDamageSources().magic(), 1.0f);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        spread(state, world, pos, random);
    }

    protected void spread(BlockState state, ServerWorld world, BlockPos pos, Random random) {
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
            world.breakBlock(pos.down(), true);
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
                world.breakBlock(pos.offset(dir), true);
                world.setBlockState(pos.offset(dir), state.with(LAYERS, 1));
                world.setBlockState(pos, state.with(LAYERS, --layer));
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        dissolve(state, world, pos, random);
    }

    private void dissolve(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //random chance to dissolve adjacent block, depending on fluid level and block hardness
        Direction dir = Direction.random(random);
        if (dir.equals(Direction.UP)) {
            return;
        }
        double surfaceArea = dir.equals(Direction.DOWN) ? 1 : state.get(LAYERS) / 16.0;
        BlockState targetState = world.getBlockState(pos.offset(dir));
        float hardness = targetState.getHardness(world, pos.offset(dir));
        hardness = hardness < 0 ? Float.POSITIVE_INFINITY : hardness;
        double dissolveChance = surfaceArea * Math.exp(-hardness);
        if (random.nextDouble() < dissolveChance) {
            world.breakBlock(pos.offset(dir), true);
            if (random.nextDouble() < 0.1) {
                world.removeBlock(pos, false);
            }
        }
    }
}
