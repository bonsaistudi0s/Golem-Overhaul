package tech.alexnijjar.golemoverhaul.common.entities.candle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MeltedCandleGolem extends CandleGolem {
    public MeltedCandleGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 25.0)
            .add(Attributes.MOVEMENT_SPEED, 0.4)
            .add(Attributes.ATTACK_DAMAGE, 7.0);
    }

    @Override
    public void setLit(boolean lit) {}
}
