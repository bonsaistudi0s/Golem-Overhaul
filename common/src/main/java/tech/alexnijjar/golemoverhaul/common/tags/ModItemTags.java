package tech.alexnijjar.golemoverhaul.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

public class ModItemTags {

    public static final TagKey<Item> WAX = tag("wax");
    public static final TagKey<Item> CACTUS = tag("cactus");

    private static TagKey<Item> tag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, name));
    }
}
