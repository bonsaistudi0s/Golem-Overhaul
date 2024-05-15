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
        add(ConstantComponents.GOLEM_CONSTUCTION_CATEGORY.getString(), "Golem Construction");

        add("subtitles.golemoverhaul.entity.coal_golem.ambient", "Coal Golem burns");
        add("subtitles.golemoverhaul.entity.coal_golem.hurt", "Coal Golem hurts");
        add("subtitles.golemoverhaul.entity.coal_golem.death", "Coal Golem dies");
        add("subtitles.golemoverhaul.entity.coal_golem.explode", "Coal Golem explodes");
        add("subtitles.golemoverhaul.entity.barrel_golem.barter", "Barrel Golem barters");
    }
}
