package tech.alexnijjar.golemoverhaul.datagen.provider.client;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, GolemOverhaul.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
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

        add(ConstantComponents.NETHERITE_GOLEM_SUMMON_KEY.getString(), "Summon Netherite Golem");
        add(ConstantComponents.GOLEM_OVERHAUL_CATEGORY.getString(), "Golem Overhaul");

        add("subtitles.golemoverhaul.entity.coal_golem.ambient", "Coal Golem burns");
        add("subtitles.golemoverhaul.entity.coal_golem.hurt", "Coal Golem hurts");
        add("subtitles.golemoverhaul.entity.coal_golem.death", "Coal Golem dies");
    }
}
