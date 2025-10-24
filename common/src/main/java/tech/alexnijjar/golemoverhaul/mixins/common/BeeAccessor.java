package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeAccessor {

    @Accessor
    int getRemainingCooldownBeforeLocatingNewHive();

    @Accessor
    void setRemainingCooldownBeforeLocatingNewHive(int remainingCooldownBeforeLocatingNewHive);

    @Accessor
    void setRemainingCooldownBeforeLocatingNewFlower(int remainingCooldownBeforeLocatingNewFlower);

    @Invoker
    boolean invokeWantsToEnterHive();
}
