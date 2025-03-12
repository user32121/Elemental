package juniper.elemental.data;

import java.util.Optional;

import juniper.elemental.Elemental;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

public class ElementalModels {
    public static final Model TriAxisBlockCore = block("template_triaxis_core", TextureKey.TEXTURE);
    public static final Model TriAxisBlockAxis = block("template_triaxis_axis", TextureKey.TEXTURE);

    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of(Elemental.MOD_ID, "block/" + parent)), Optional.empty(),
                requiredTextureKeys);
    }
}
