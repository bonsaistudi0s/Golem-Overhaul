package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeModeTab;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.apache.commons.lang3.NotImplementedException;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.items.HoneyBlobItem;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItems {

    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, GolemOverhaul.MOD_ID);
    public static final ResourcefulRegistry<Item> SPAWN_EGGS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<CreativeModeTab> TABS = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, GolemOverhaul.MOD_ID);
    public static final RegistryEntry<CreativeModeTab> TAB = TABS.register("main", () -> new ResourcefulCreativeModeTab(ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "main"))
        .setItemIcon(() -> ModItems.CLAY_GOLEM_STATUE.get())
        .addRegistry(ITEMS)
        .build());

    public static final RegistryEntry<Item> BARREL_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("barrel_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.BARREL_GOLEM, 0xb47f44, 0x603c2d, new Item.Properties()));
    public static final RegistryEntry<Item> CANDLE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("candle_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.CANDLE_GOLEM, 0xe4ca84, 0x956a4a, new Item.Properties()));
    public static final RegistryEntry<Item> COAL_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("coal_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.COAL_GOLEM, 0xf8df66, 0xd47721, new Item.Properties()));
    public static final RegistryEntry<Item> HAY_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("hay_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.HAY_GOLEM, 0xcd8d37, 0xd9c373, new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("honey_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.HONEY_GOLEM, 0xedb043, 0xdb914c, new Item.Properties()));
    public static final RegistryEntry<Item> KELP_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("kelp_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.KELP_GOLEM, 0x56d0b6, 0x548324, new Item.Properties()));
    public static final RegistryEntry<Item> NETHERITE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("netherite_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.NETHERITE_GOLEM, 0x2f2829, 0x3b393b, new Item.Properties()));
    public static final RegistryEntry<Item> SLIME_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("slime_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.SLIME_GOLEM, 0xddf162, 0x80bc47, new Item.Properties()));
    public static final RegistryEntry<Item> TERRACOTTA_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("terracotta_golem_spawn_egg", () -> createSpawnEgg(ModEntityTypes.TERRACOTTA_GOLEM, 0xae8737, 0x87593c, new Item.Properties()));

    public static final RegistryEntry<Item> CANDLE_GOLEM_BLOCK = ITEMS.register("candle_golem_block", () -> new BlockItem(ModBlocks.CANDLE_GOLEM_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CLAY_GOLEM_STATUE = ITEMS.register("clay_golem_statue", () -> new BlockItem(ModBlocks.CLAY_GOLEM_STATUE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_BLOB = ITEMS.register("honey_blob", () -> new HoneyBlobItem(new Item.Properties()));

    @ExpectPlatform
    public static SpawnEggItem createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
        throw new NotImplementedException();
    }
}
