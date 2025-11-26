package tech.alexnijjar.golemoverhaul.common.registry.neoforge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItemsImpl {

    public static SpawnEggItem createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor
            , int highlightColor, Item.Properties properties) {
        return new DeferredSpawnEggItem(type, backgroundColor, highlightColor, properties);
    }
}
