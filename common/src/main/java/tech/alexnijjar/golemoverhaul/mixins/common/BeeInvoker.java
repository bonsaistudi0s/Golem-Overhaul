package tech.alexnijjar.golemoverhaul.mixins.common;

import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeInvoker {

    @Invoker
    boolean invokeWantsToEnterHive();
}
