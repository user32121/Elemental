package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.render.block.LightBallBlockEntityModel;
import juniper.elemental.render.block.LightCrystalBlockEntityModel;
import juniper.elemental.render.block.PackedLightCrystalBlockEntityModel;
import juniper.elemental.render.entity.DarkGhostEntityModel;
import juniper.elemental.render.entity.ReactionCraftingEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ElementalModelLayers {
    private static final Dilation HAT_DILATION = new Dilation(0.5f);
    private static final Dilation ARMOR_DILATION = new Dilation(1.0f);
    public static final EntityModelLayer REACTION_CRAFTING = register("reaction_crafting", ReactionCraftingEntityModel::getTexturedModelData);
    public static final EntityModelLayer LIGHT_CRYSTAL = register("light_crystal", LightCrystalBlockEntityModel::getTexturedModelData);
    public static final EntityModelLayer DARK_GHOST = register("dark_ghost", DarkGhostEntityModel::getTexturedModelData);
    public static final EntityModelLayer DARK_GHOST_INNER_ARMOR = registerInnerArmor("dark_ghost");
    public static final EntityModelLayer DARK_GHOST_OUTER_ARMOR = registerOuterArmor("dark_ghost");
    public static final EntityModelLayer PACKED_LIGHT_CRYSTAL = register("packed_light_crystal", PackedLightCrystalBlockEntityModel::getTexturedModelData);
    public static final EntityModelLayer LIGHT_BALL = register("light_ball", LightBallBlockEntityModel::getTexturedModelData);

    public static void init() {
    }

    private static EntityModelLayer register(String name, TexturedModelDataProvider provider) {
        return register(name, provider, "main");
    }

    private static EntityModelLayer registerInnerArmor(String name) {
        return register(name, () -> TexturedModelData.of(ArmorEntityModel.getModelData(HAT_DILATION), 64, 32), "inner_armor");
    }

    private static EntityModelLayer registerOuterArmor(String name) {
        return register(name, () -> TexturedModelData.of(ArmorEntityModel.getModelData(ARMOR_DILATION), 64, 32), "outer_armor");
    }

    private static EntityModelLayer register(String name, TexturedModelDataProvider provider, String layer) {
        EntityModelLayer modelLayer = new EntityModelLayer(Identifier.of(Elemental.MOD_ID, name), layer);
        EntityModelLayerRegistry.registerModelLayer(modelLayer, provider);
        return modelLayer;
    }
}
