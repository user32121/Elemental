package juniper.elemental.blocks;

import com.mojang.serialization.MapCodec;

import juniper.elemental.blockEntities.LightCrystalBlockEntity;
import juniper.elemental.init.ElementalDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.TeleportTarget.PostDimensionTransition;
import net.minecraft.world.World;

//TODO natural spawning
public class LightCrystalBlock extends BlockWithEntity implements Portal {
    public static final VoxelShape SHAPE = VoxelShapes.cuboid(1 / 5.0, 1 / 5.0, 1 / 5.0, 4 / 5.0, 4 / 5.0, 4 / 5.0);

    public LightCrystalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LightCrystalBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(LightCrystalBlock::new);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
        if (!world.getRegistryKey().getValue().equals(ElementalDimensions.DARK_ID)) {
            return;
        }
        world.breakBlock(pos, false, player);
        player.giveItemStack(new ItemStack(state.getBlock().asItem()));
        player.tryUsePortal(this, pos);
    }

    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        if (entity instanceof ServerPlayerEntity spe) {
            return spe.getRespawnTarget(false, TeleportTarget.NO_OP);
        }
        ServerWorld targetWorld = world.getServer().getOverworld();
        pos = entity.getWorldSpawnPos(targetWorld, pos);
        PostDimensionTransition pdt = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
        return new TeleportTarget(targetWorld, pos.toBottomCenterPos(), Vec3d.ZERO, 0, 0, pdt);
    }
}
