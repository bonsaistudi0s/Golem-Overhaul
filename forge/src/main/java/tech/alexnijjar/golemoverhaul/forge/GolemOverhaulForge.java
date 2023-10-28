package tech.alexnijjar.golemoverhaul.forge;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.forge.GolemOverhaulClientForge;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@Mod(GolemOverhaul.MOD_ID)
public class GolemOverhaulForge {

    public GolemOverhaulForge() {
        GolemOverhaul.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(GolemOverhaulForge::onAttributes);
        bus.addListener(GolemOverhaulForge::commonSetup);
        if (FMLEnvironment.dist.isClient()) {
            GolemOverhaulClientForge.init();
        }
    }

    public static void onAttributes(EntityAttributeCreationEvent event) {
        ModEntityTypes.registerAttributes((entityType, attribute) -> event.put(entityType.get(), attribute.get().build()));
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        GolemOverhaul.postInit();
    }
}
