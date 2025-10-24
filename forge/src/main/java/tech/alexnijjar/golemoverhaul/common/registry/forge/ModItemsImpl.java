package tech.alexnijjar.golemoverhaul.common.registry.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItemsImpl {

    public static SpawnEggItem createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new ForgeSpawnEggItem(type, backgroundColor, highlightColor, properties);
    }
}
