package juniper.elemental.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class ReactionRecipeSerializer implements RecipeSerializer<ReactionRecipe> {
    private static final MapCodec<ReactionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients), Codec.FLOAT.fieldOf("cost").forGetter(recipe -> recipe.cost),
                            ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result), Codec.BOOL.fieldOf("isFireWater").forGetter(recipe -> recipe.isFireWater))
            .apply(instance, ReactionRecipe::new));
    private static final PacketCodec<RegistryByteBuf, ReactionRecipe> PACKET_CODEC = PacketCodec.tuple(Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()), recipe -> recipe.ingredients,
            PacketCodecs.FLOAT, recipe -> recipe.cost, ItemStack.PACKET_CODEC, recipe -> recipe.result, PacketCodecs.BOOL, recipe -> recipe.isFireWater, ReactionRecipe::new);

    @Override
    public MapCodec<ReactionRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ReactionRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
