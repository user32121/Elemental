package juniper.elemental.blockEntities;

import juniper.elemental.init.ElementalBlockEntities;
import juniper.elemental.screens.ExtractorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ExtractorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public static final int MAX_PROGRESS = 1024;
    private final SimpleInventory inventory = new SimpleInventory(3);
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int var1) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void set(int var1, int var2) {
            // TODO Auto-generated method stub
            return;
        }

        @Override
        public int size() {
            return 2;
        }
    };

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
}
