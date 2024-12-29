package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.render.block.LightCrystalBlockEntityModel;
import juniper.elemental.render.entity.ReactionCraftingEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ElementalModelLayers {
    public static final EntityModelLayer REACTION_CRAFTING = register("reaction_crafting", ReactionCraftingEntityModel::getTexturedModelData);
    public static final EntityModelLayer LIGHT_CRYSTAL = register("light_crystal", LightCrystalBlockEntityModel::getTexturedModelData);

    public static void init() {
    }

    private static EntityModelLayer register(String name, TexturedModelDataProvider provider) {
        EntityModelLayer modelLayer = new EntityModelLayer(Identifier.of(Elemental.MOD_ID, name), "main");
        EntityModelLayerRegistry.registerModelLayer(modelLayer, provider);
        return modelLayer;
    }
}
