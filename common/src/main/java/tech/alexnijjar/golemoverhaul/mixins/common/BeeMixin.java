package tech.alexnijjar.golemoverhaul.mixins.common;


import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.alexnijjar.golemoverhaul.common.entities.goals.GoToHoneyGolemHiveGoal;

@Mixin(Bee.class)
public abstract class BeeMixin {

    @Shadow
    public abstract GoalSelector getGoalSelector();

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void golemoverhaul$registerGoals(CallbackInfo ci) {
        getGoalSelector().addGoal(6, new GoToHoneyGolemHiveGoal((Bee) (Object) this));
    }
}
