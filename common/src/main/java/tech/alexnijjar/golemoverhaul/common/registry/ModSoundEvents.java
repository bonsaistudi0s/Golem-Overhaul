package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

public class ModSoundEvents {

    public static final ResourcefulRegistry<SoundEvent> SOUND_EVENTS = ResourcefulRegistries.create(BuiltInRegistries.SOUND_EVENT, GolemOverhaul.MOD_ID);

    public static final RegistryEntry<SoundEvent> COAL_GOLEM_AMBIENT = SOUND_EVENTS.register("coal_golem_ambient", () ->
        SoundEvent.createVariableRangeEvent(new ResourceLocation(GolemOverhaul.MOD_ID, "coal_golem_ambient")));

    public static final RegistryEntry<SoundEvent> COAL_GOLEM_HURT = SOUND_EVENTS.register("coal_golem_hurt", () ->
        SoundEvent.createVariableRangeEvent(new ResourceLocation(GolemOverhaul.MOD_ID, "coal_golem_hurt")));

    public static final RegistryEntry<SoundEvent> COAL_GOLEM_DEATH = SOUND_EVENTS.register("coal_golem_death", () ->
        SoundEvent.createVariableRangeEvent(new ResourceLocation(GolemOverhaul.MOD_ID, "coal_golem_death")));

    public static final RegistryEntry<SoundEvent> COAL_GOLEM_EXPLODE = SOUND_EVENTS.register("coal_golem_explode", () ->
        SoundEvent.createVariableRangeEvent(new ResourceLocation(GolemOverhaul.MOD_ID, "coal_golem_explode")));

    public static final RegistryEntry<SoundEvent> BARREL_GOLEM_BARTER = SOUND_EVENTS.register("barrel_golem_barter", () ->
        SoundEvent.createVariableRangeEvent(new ResourceLocation(GolemOverhaul.MOD_ID, "barrel_golem_barter")));
}
