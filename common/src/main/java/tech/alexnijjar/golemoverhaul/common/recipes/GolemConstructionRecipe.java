package tech.alexnijjar.golemoverhaul.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GolemConstructionRecipe implements Recipe<SingleEntityInput> {
    private final ResourceLocation id;
    private final List<String> pattern;
    private final Map<String, ResourceKey<Block>> key;
    private final ResourceKey<EntityType<?>> entity;
    private final ResourceKey<Item> item;

    /// If true, this recipe is just for display in JEI and will not trigger pattern matching and spawning the golem
    private final boolean visualOnly;
    private final float blockScale;
    private final float entityScale;

    public GolemConstructionRecipe(ResourceLocation id, List<String> pattern, Map<String, ResourceKey<Block>> key, ResourceKey<EntityType<?>> entity, ResourceKey<Item> item, boolean visualOnly, float blockScale, float entityScale) {
        this.id = id;
        this.pattern = pattern;
        this.key = key;
        this.entity = entity;
        this.item = item;
        this.visualOnly = visualOnly;
        this.blockScale = blockScale;
        this.entityScale = entityScale;
    }

    @Override
    public boolean matches(SingleEntityInput input, @NotNull Level level) {
        var key = BuiltInRegistries.ENTITY_TYPE.getResourceKey(input.entity());
        return key.isPresent() && key.get().equals(entity);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleEntityInput container, @NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public BlockPattern createPattern() {
        var builder = BlockPatternBuilder.start();
        builder.aisle(this.pattern.toArray(new String[0]));
        this.key.forEach((key, block_key) ->
                builder.where(key.charAt(0), BlockInWorld.hasState(BlockStatePredicate.forBlock(Objects.requireNonNull(BuiltInRegistries.BLOCK.get(block_key))))));
        return builder.build();
    }

    public Map<String, ResourceKey<Block>> getKey() {
        return key;
    }

    public ResourceKey<Item> getItem() {
        return item;
    }

    public float getBlockScale() {
        return blockScale;
    }

    public float getEntityScale() {
        return entityScale;
    }

    public List<String> getPattern() {
        return pattern;
    }

    public ResourceKey<EntityType<?>> getEntity() {
        return entity;
    }

    public static class Type implements RecipeType<GolemConstructionRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<GolemConstructionRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull GolemConstructionRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject serializedRecipe) {
            var patternJson = GsonHelper.getAsJsonArray(serializedRecipe, "pattern");
            var pattern = StreamSupport.stream(patternJson.spliterator(), false)
                    .map(element -> GsonHelper.convertToString(element, "pattern entry"))
                    .collect(Collectors.toList());

            var keyJson = GsonHelper.getAsJsonObject(serializedRecipe, "key");
            var key = keyJson.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                var blockId = new ResourceLocation(GsonHelper.convertToString(entry.getValue(), "key value"));
                                return ResourceKey.create(Registries.BLOCK, blockId);
                            }
                    ));

            var entityId = new ResourceLocation(GsonHelper.getAsString(serializedRecipe, "entity"));
            var entity = ResourceKey.create(Registries.ENTITY_TYPE, entityId);

            var itemId = new ResourceLocation(GsonHelper.getAsString(serializedRecipe, "item"));
            var item = ResourceKey.create(Registries.ITEM, itemId);

            var visualOnly = GsonHelper.getAsBoolean(serializedRecipe, "visualOnly", false);
            var blockScale = GsonHelper.getAsFloat(serializedRecipe, "blockScale", 1.0f);
            var entityScale = GsonHelper.getAsFloat(serializedRecipe, "entityScale", 1.0f);

            return new GolemConstructionRecipe(recipeId, pattern, key, entity, item, visualOnly, blockScale, entityScale);
        }

        @Override
        public @Nullable GolemConstructionRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            var pattern = buffer.readList(FriendlyByteBuf::readUtf);

            var key = buffer.readMap(
                    FriendlyByteBuf::readUtf,
                    (buf) -> ResourceKey.create(Registries.BLOCK, buf.readResourceLocation())
            );

            var entity = ResourceKey.create(Registries.ENTITY_TYPE, buffer.readResourceLocation());
            var item = ResourceKey.create(Registries.ITEM, buffer.readResourceLocation());

            var visualOnly = buffer.readBoolean();
            var blockScale = buffer.readFloat();
            var entityScale = buffer.readFloat();

            return new GolemConstructionRecipe(recipeId, pattern, key, entity, item, visualOnly, blockScale, entityScale);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull GolemConstructionRecipe recipe) {
            buffer.writeCollection(recipe.pattern, FriendlyByteBuf::writeUtf);

            buffer.writeMap(recipe.key,
                    FriendlyByteBuf::writeUtf,
                    (buf, resourceKey) -> buf.writeResourceLocation(resourceKey.location())
            );

            buffer.writeResourceLocation(recipe.entity.location());
            buffer.writeResourceLocation(recipe.item.location());
            buffer.writeBoolean(recipe.visualOnly);
            buffer.writeFloat(recipe.blockScale);
            buffer.writeFloat(recipe.entityScale);
        }
    }
}
