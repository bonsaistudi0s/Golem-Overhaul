package tech.alexnijjar.golemoverhaul.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.datagen.provider.client.ModBlockStateProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.client.ModItemModelProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.client.ModLangProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.server.*;

@Mod.EventBusSubscriber(modid = GolemOverhaul.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GolemOverhaulDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var lookupProvider = event.getLookupProvider();
        var packOutput = generator.getPackOutput();
        var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));

        generator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModEntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModBiomeTagProvider(packOutput, lookupProvider, existingFileHelper));
    }
}
