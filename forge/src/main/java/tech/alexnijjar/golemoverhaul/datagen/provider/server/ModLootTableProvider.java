package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(EntityLootTables::new, LootContextParamSets.ENTITY)));
    }

    private static class EntityLootTables implements LootTableSubProvider {
        @Override
        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> output) {
            output.accept(getEntity(ModEntityTypes.NETHERITE_GOLEM.get()), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.NETHERITE_INGOT).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(4, 7)))))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(4, 7))))));

            output.accept(getEntity(ModEntityTypes.COAL_GOLEM.get()), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.COAL))));
        }
    }

    private static ResourceLocation getEntity(EntityType<?> entity) {
        return new ResourceLocation(GolemOverhaul.MOD_ID, "entities/%s".formatted(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entity)).getPath()));
    }
}
