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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.NotImplementedException;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItems {
    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, GolemOverhaul.MOD_ID);
    public static final ResourcefulRegistry<Item> SPAWN_EGGS = ResourcefulRegistries.create(ITEMS);
    public static final Supplier<CreativeModeTab> TAB = new ResourcefulCreativeTab(new ResourceLocation(GolemOverhaul.MOD_ID, "main"))
        .setItemIcon(() -> Items.IRON_INGOT)
        .addRegistry(ITEMS)
        .build();

    public static final RegistryEntry<Item> NETHERITE_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("netherite_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.NETHERITE_GOLEM, 0x2f2829, 0x3b393b, new Item.Properties()));
    public static final RegistryEntry<Item> COAL_GOLEM_SPAWN_EGG = SPAWN_EGGS.register("coal_golem_spawn_egg", createSpawnEggItem(ModEntityTypes.COAL_GOLEM, 0xf8df66, 0xd47721, new Item.Properties()));

    @ExpectPlatform
    public static Supplier<Item> createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Item.Properties properties) {
        throw new NotImplementedException();
    }
}
