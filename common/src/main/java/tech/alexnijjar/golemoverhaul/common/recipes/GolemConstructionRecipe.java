package tech.alexnijjar.golemoverhaul.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeSerializers;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeTypes;

import java.util.List;
import java.util.Map;

// TODO actually use the recipes in world crafting not just REI
public record GolemConstructionRecipe(
    List<String> pattern,
    Map<String, ResourceKey<Block>> key,
    ResourceKey<EntityType<?>> entity,
    ResourceKey<Item> item
) implements CodecRecipe<Container> {

    public static final MapCodec<GolemConstructionRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("pattern").forGetter(GolemConstructionRecipe::pattern),
            Codec.unboundedMap(Codec.STRING, ResourceKey.codec(Registries.BLOCK)).fieldOf("key").forGetter(GolemConstructionRecipe::key),
            ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(GolemConstructionRecipe::entity),
            ResourceKey.codec(Registries.ITEM).fieldOf("item").forGetter(GolemConstructionRecipe::item)
        ).apply(instance, GolemConstructionRecipe::new));

    public static final ByteCodec<GolemConstructionRecipe> NETWORK_CODEC = ObjectByteCodec.create(
        ByteCodec.STRING.listOf().fieldOf(GolemConstructionRecipe::pattern),
        new com.teamresourceful.bytecodecs.defaults.MapCodec<>(ByteCodec.STRING, ExtraByteCodecs.resourceKey(Registries.BLOCK)).fieldOf(GolemConstructionRecipe::key),
        ExtraByteCodecs.resourceKey(Registries.ENTITY_TYPE).fieldOf(GolemConstructionRecipe::entity),
        ExtraByteCodecs.resourceKey(Registries.ITEM).fieldOf(GolemConstructionRecipe::item),
        GolemConstructionRecipe::new
    );

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public CodecRecipeSerializer<? extends CodecRecipe<Container>> serializer() {
        return ModRecipeSerializers.GOLEM_CONSTRUCTION.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.GOLEM_CONSTRUCTION.get();
    }
}
