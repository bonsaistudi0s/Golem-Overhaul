package tech.alexnijjar.golemoverhaul.datagen.provider.client;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;

public class ModLangProvider extends LanguageProvider {

    public ModLangProvider(PackOutput output) {
        super(output, GolemOverhaul.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ModBlocks.BLOCKS.stream()
            .forEach(entry -> addBlock(entry,
                StringUtils.capitaliseAllWords(entry
                    .getId()
                    .getPath()
                    .replace("_", " "))));

        ModItems.ITEMS.stream()
            .filter(i -> !(i.get() instanceof BlockItem))
            .forEach(entry -> addItem(entry,
                StringUtils.capitaliseAllWords(entry
                    .getId()
                    .getPath()
                    .replace("_", " "))));

        ModEntityTypes.ENTITY_TYPES.stream()
            .forEach(entry -> addEntityType(entry,
                StringUtils.capitaliseAllWords(entry
                    .getId()
                    .getPath()
                    .replace("_", " "))));

        add("itemGroup.golemoverhaul.main", "Golem Overhaul");

        add(ConstantComponents.NETHERITE_GOLEM_SUMMON_KEY.getString(), "Summon Netherite Golem");
        add(ConstantComponents.GOLEM_OVERHAUL_CATEGORY.getString(), "Golem Overhaul");
        add(ConstantComponents.GOLEM_CONSTRUCTION_CATEGORY.getString(), "Golem Construction");
        add(ConstantComponents.CANDLE_GOLEM_TOOLTIP.getString(), "Right-click to transform into a Candle Golem");
        add(ConstantComponents.CLAY_GOLEM_STATUE_TOOLTIP.getString(), "Can be melted like ice into a Terracotta Golem");

        add("subtitles.golemoverhaul.entity.coal_golem.ambient", "Coal Golem burns");
        add("subtitles.golemoverhaul.entity.coal_golem.hurt", "Coal Golem hurts");
        add("subtitles.golemoverhaul.entity.coal_golem.death", "Coal Golem dies");
        add("subtitles.golemoverhaul.entity.coal_golem.explode", "Coal Golem explodes");
        add("subtitles.golemoverhaul.entity.barrel_golem.barter", "Barrel Golem barters");
        add("subtitles.golemoverhaul.entity.netherite_golem.hit", "Netherite Golem is hit");
        add("subtitles.golemoverhaul.entity.netherite_golem.death", "Netherite Golem dies");
        add("subtitles.golemoverhaul.entity.netherite_golem.step", "Netherite Golem steps");
        add("subtitles.golemoverhaul.entity.netherite_golem.summon", "Netherite Golem is summoned");
        add("subtitles.golemoverhaul.entity.hay_golem.hurt", "Hay Golem hurts");
        add("subtitles.golemoverhaul.entity.hay_golem.death", "Hay Golem dies");
        add("subtitles.golemoverhaul.entity.kelp_golem.death", "Kelp Golem dies");
        add("subtitles.golemoverhaul.entity.kelp_golem.step", "Kelp Golem steps");

        add("config.golemoverhaul.allowSpawning", "Allow Spawning");
        add("config.golemoverhaul.spawnBarrelGolems", "Spawn Barrel Golems");
        add("config.golemoverhaul.spawnCoalGolems", "Spawn Coal Golems");
        add("config.golemoverhaul.spawnHayGolems", "Spawn Hay Golems");
        add("config.golemoverhaul.spawnHoneyGolems", "Spawn Honey Golems");
        add("config.golemoverhaul.spawnSlimeGolems", "Spawn Slime Golems");
        add("config.golemoverhaul.spawnTerracottaGolems", "Spawn Terracotta Golems");
    }
}
