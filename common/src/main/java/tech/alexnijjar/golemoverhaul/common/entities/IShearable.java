package tech.alexnijjar.golemoverhaul.common.entities;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface IShearable {

    default boolean isShearable()
    {
        return true;
    }

    @NotNull
    default List<ItemStack> onSheared()
    {
        return Collections.emptyList();
    }
}
