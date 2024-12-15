package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.render.entity.CraftingEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ElementalModelLayers {
    public static final EntityModelLayer CRAFTING = register("crafting", CraftingEntityModel::getTexturedModelData);

    public static void init() {
    }

    private static EntityModelLayer register(String name, TexturedModelDataProvider provider) {
        EntityModelLayer modelLayer = new EntityModelLayer(Identifier.of(Elemental.MOD_ID, name), "main");
        EntityModelLayerRegistry.registerModelLayer(modelLayer, provider);
        return modelLayer;
    }
}
