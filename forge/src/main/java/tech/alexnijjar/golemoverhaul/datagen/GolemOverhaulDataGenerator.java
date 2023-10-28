package tech.alexnijjar.golemoverhaul.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.datagen.provider.client.ModItemModelProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.client.ModLangProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.server.ModBlockTagProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.server.ModEntityTypeTagProvider;
import tech.alexnijjar.golemoverhaul.datagen.provider.server.ModLootTableProvider;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = GolemOverhaul.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GolemOverhaulDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput));

        generator.addProvider(event.includeServer(), new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModEntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    }
}
