package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.common.entities.golems.BarrelGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntityLootTables::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(BarrelGolemBarterLootProvider::new, LootContextParamSets.PIGLIN_BARTER)
        ));
    }

    private static class BlockLootTables extends BlockLootSubProvider {

        protected BlockLootTables() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            dropSelf(ModBlocks.CANDLE_GOLEM_BLOCK.get());
            dropSelf(ModBlocks.CLAY_GOLEM_STATUE.get());
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return List.of(
                    ModBlocks.CANDLE_GOLEM_BLOCK.get(),
                    ModBlocks.CLAY_GOLEM_STATUE.get()
            );
        }
    }

    private static class EntityLootTables extends EntityLootSubProvider {

        protected EntityLootTables() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            add(ModEntityTypes.BARREL_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.COD).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                    .add(LootItem.lootTableItem(Items.WHEAT).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.FLINT).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.CARROT).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))

                    .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(Items.EMERALD).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(Items.IRON_HELMET).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15f, 0.8f))))
                ));

            add(ModEntityTypes.CANDLE_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.CANDLE))));

            add(ModEntityTypes.COAL_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.COAL))));

            add(ModEntityTypes.HAY_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.WHEAT).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(0, 4))))));

            add(ModEntityTypes.HONEY_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.HONEYCOMB).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(0, 3))))));

            add(ModEntityTypes.KELP_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.KELP).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(1, 3)))))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(1, 3))))));

            add(ModEntityTypes.NETHERITE_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(1, 3))))));

            add(ModEntityTypes.SLIME_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.SLIME_BALL).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(1, 3))))));

            add(ModEntityTypes.TERRACOTTA_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.TERRACOTTA).apply(SetItemCountFunction
                        .setCount(UniformGenerator.between(0, 2)))))); // TODO: drop cactus or deadbush
        }

        @Override
        protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
            return ModEntityTypes.GOLEMS.stream().map(RegistryEntry::get);
        }

        @Override
        protected boolean canHaveLootTable(@NotNull EntityType<?> type) {
            return true;
        }
    }

    private static class BarrelGolemBarterLootProvider implements LootTableSubProvider {

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
            output.accept(
                    BarrelGolem.BARTERING_LOOT,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1))
                                            .add(TagEntry.expandTag(ItemTags.VILLAGER_PLANTABLE_SEEDS).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                            .add(TagEntry.expandTag(ItemTags.SAPLINGS).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                            .add(LootItem.lootTableItem(Items.POTION).setWeight(40).apply(SetPotionFunction.setPotion(Potions.WATER)))
                                            .add(LootItem.lootTableItem(Items.EGG).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))

                                            .add(LootItem.lootTableItem(Items.LEATHER).setWeight(30).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(30).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                                            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(30).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))

                                            .add(LootItem.lootTableItem(Items.AXOLOTL_BUCKET).setWeight(10))
                                            .add(LootItem.lootTableItem(Items.NAME_TAG).setWeight(10))
                                            .add(LootItem.lootTableItem(Items.LEAD).setWeight(10))
                                            .add(LootItem.lootTableItem(Items.SADDLE).setWeight(10))
                                            .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))

                                            .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.BOOK).setWeight(3).apply(EnchantRandomlyFunction.randomApplicableEnchantment()))
                                            .add(LootItem.lootTableItem(Items.TOTEM_OF_UNDYING).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(3))
                                            .add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS).setWeight(3))
                            )
            );
        }
    }
}
