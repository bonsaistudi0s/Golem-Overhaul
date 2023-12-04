package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeTab;
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
import org.apache.commons.lang3.NotImplementedException;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItems {
    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, GolemOverhaul.MOD_ID);
    public static final ResourcefulRegistry<Item> SPAWN_EGGS = ResourcefulRegistries.create(ITEMS);
    public static final Supplier<CreativeModeTab> TAB = new ResourcefulCreativeTab(new ResourceLocation(GolemOverhaul.MOD_ID, "main"))
        .setItemIcon(() -> ModItems.CLAY_GOLEM_STATUE.get())
        .addRegistry(ITEMS)
        .build();

    public static final RegistryEntry<Item> NETHERITE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("netherite_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.NETHERITE_GOLEM, 0x2f2829, 0x3b393b, new Item.Properties()));
    public static final RegistryEntry<Item> COAL_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("coal_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.COAL_GOLEM, 0xf8df66, 0xd47721, new Item.Properties()));
    public static final RegistryEntry<Item> CANDLE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("candle_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.CANDLE_GOLEM, 0xe4ca84, 0x956a4a, new Item.Properties()));
    public static final RegistryEntry<Item> MEDIUM_CANDLE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("medium_candle_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.MEDIUM_CANDLE_GOLEM, 0xe4ca84, 0xbe955c, new Item.Properties()));
    public static final RegistryEntry<Item> MELTED_CANDLE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("melted_candle_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.MELTED_CANDLE_GOLEM, 0xffebac, 0xa77c51, new Item.Properties()));
    public static final RegistryEntry<Item> TERRACOTTA_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("terracotta_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.TERRACOTTA_GOLEM, 0xae8737, 0x87593c, new Item.Properties()));
    public static final RegistryEntry<Item> CACTUS_TERRACOTTA_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("cactus_terracotta_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.CACTUS_TERRACOTTA_GOLEM, 0x8e6026, 0x949831, new Item.Properties()));
    public static final RegistryEntry<Item> DEAD_BUSH_TERRACOTTA_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("dead_bush_terracotta_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.DEAD_BUSH_TERRACOTTA_GOLEM, 0xa3732b, 0x39251d, new Item.Properties()));

    public static final RegistryEntry<Item> CANDLE_GOLEM_BLOCK = ITEMS.register("candle_golem_block", () -> new BlockItem(ModBlocks.CANDLE_GOLEM_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CLAY_GOLEM_STATUE = ITEMS.register("clay_golem_statue", () -> new BlockItem(ModBlocks.CLAY_GOLEM_STATUE.get(), new Item.Properties()));

    @ExpectPlatform
    public static Supplier<Item> createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Item.Properties properties) {
        throw new NotImplementedException();
    }
}
