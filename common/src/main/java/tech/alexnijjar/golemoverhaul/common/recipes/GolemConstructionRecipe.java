package tech.alexnijjar.golemoverhaul.common.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeSerializers;
import tech.alexnijjar.golemoverhaul.common.registry.ModRecipeTypes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs.RESOURCE_LOCATION;

public record GolemConstructionRecipe(
    List<String> pattern,
    Map<String, Either<ResourceKey<Block>, TagKey<Block>>> key,
    ResourceKey<EntityType<?>> entity,
    ResourceKey<Item> item,
    boolean visualOnly,
    float blockScale,
    float entityScale
) implements CodecRecipe<SingleEntityInput> {

    public static final MapCodec<GolemConstructionRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("pattern").forGetter(GolemConstructionRecipe::pattern),
            Codec.unboundedMap(Codec.STRING, Codec.either(ResourceKey.codec(Registries.BLOCK), TagKey.hashedCodec(Registries.BLOCK))).fieldOf("key").forGetter(GolemConstructionRecipe::key),
            ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(GolemConstructionRecipe::entity),
            ResourceKey.codec(Registries.ITEM).fieldOf("item").forGetter(GolemConstructionRecipe::item),
            Codec.BOOL.optionalFieldOf("visualOnly", false).forGetter(GolemConstructionRecipe::visualOnly),
            Codec.FLOAT.optionalFieldOf("blockScale", 1f).forGetter(GolemConstructionRecipe::blockScale),
            Codec.FLOAT.optionalFieldOf("entityScale", 1f).forGetter(GolemConstructionRecipe::entityScale)
        ).apply(instance, GolemConstructionRecipe::new));

    @SuppressWarnings("SameParameterValue")
    private static <T, R extends Registry<T>> ByteCodec<TagKey<T>> tagKey(ResourceKey<R> registry) {
        return RESOURCE_LOCATION.map(id -> TagKey.create(registry, id), TagKey::location);
    }

    public static final ByteCodec<GolemConstructionRecipe> NETWORK_CODEC = ObjectByteCodec.create(
        ByteCodec.STRING.listOf().fieldOf(GolemConstructionRecipe::pattern),
        new com.teamresourceful.bytecodecs.defaults.MapCodec<>(ByteCodec.STRING, ExtraByteCodecs.either(ExtraByteCodecs.resourceKey(Registries.BLOCK), tagKey(Registries.BLOCK))).fieldOf(GolemConstructionRecipe::key),
        ExtraByteCodecs.resourceKey(Registries.ENTITY_TYPE).fieldOf(GolemConstructionRecipe::entity),
        ExtraByteCodecs.resourceKey(Registries.ITEM).fieldOf(GolemConstructionRecipe::item),
        ByteCodec.BOOLEAN.fieldOf(GolemConstructionRecipe::visualOnly),
        ByteCodec.FLOAT.fieldOf(GolemConstructionRecipe::blockScale),
        ByteCodec.FLOAT.fieldOf(GolemConstructionRecipe::entityScale),
        GolemConstructionRecipe::new
    );

    @Override
    public boolean matches(SingleEntityInput input, Level level) {
        Optional<ResourceKey<EntityType<?>>> key = BuiltInRegistries.ENTITY_TYPE.getResourceKey(input.entity());
        return key.isPresent() && key.get().equals(this.entity());
    }

    @Override
    public CodecRecipeSerializer<? extends CodecRecipe<SingleEntityInput>> serializer() {
        return ModRecipeSerializers.GOLEM_CONSTRUCTION.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.GOLEM_CONSTRUCTION.get();
    }

    public BlockPattern createPattern() {
        BlockPatternBuilder builder = BlockPatternBuilder.start();
        builder.aisle(this.pattern.toArray(new String[0]));
        this.key.forEach((k, v) -> {
            Predicate<BlockState> predicate = v.map(
                key -> BlockStatePredicate.forBlock(Objects.requireNonNull(BuiltInRegistries.BLOCK.get(key))),
                tagKey -> (Predicate<BlockState>) state -> state.is(tagKey));
            builder.where(k.charAt(0), BlockInWorld.hasState(predicate));
        });
        return builder.build();
    }
}
