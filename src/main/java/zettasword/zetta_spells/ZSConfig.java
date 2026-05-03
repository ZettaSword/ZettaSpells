package zettasword.zetta_spells;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = ZettaSpells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ZSConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LEARNING_SYSTEM = BUILDER
            .comment("Is learning system is active? (It allows you cast spell, and slowly getting better at this spell, increasing potency and decreasing cost)")
            .define("learningSystem", true);

    private static final ForgeConfigSpec.BooleanValue SPELLCREATION_ENABLED = BUILDER
            .comment("SpellCreation enabled? (Players can create own spells with Unfinished Spellbook and put it on the wand.)")
            .define("spellCreationEnabled", true);

    private static final ForgeConfigSpec.BooleanValue SPELLCREATION_CAST_FROM_SPELLBOOKS = BUILDER
            .comment("SpellCreation: You can cast your custom spell from your Spellbook you've made.")
            .define("spellCreationCastFromSpellbooks", false);

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BANNED_MOB_EFFECTS = BUILDER
            .comment("Banned Mob Effects (bans effects from applying using Apply spellword in custom spells.)")
            .defineListAllowEmpty("banned_mob_effects", List.of("minecraft:saturation", "minecraft:harm", "minecraft:wither"), ZSConfig::validateMobEffectName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BANNED_SUMMONS = BUILDER
            .comment("Banned Summons (bans entities from being summoned using Summon spellword in custom spells.)")
            .defineListAllowEmpty("banned_summons", List.of("minecraft:wither"), ZSConfig::validateMobName);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean learningSystem;
    public static boolean spellCreationEnabled;
    public static boolean spellCreationCastFromSpellbooks;
    public static List<? extends String> banned_mob_effects;
    public static List<? extends String> banned_summons;

    private static boolean validateMobEffectName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.MOB_EFFECTS.containsKey(ResourceLocation.tryParse(itemName));
    }

    private static boolean validateMobName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ENTITY_TYPES.containsKey(ResourceLocation.tryParse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        learningSystem = LEARNING_SYSTEM.get();
        spellCreationEnabled = SPELLCREATION_ENABLED.get();
        spellCreationCastFromSpellbooks = SPELLCREATION_CAST_FROM_SPELLBOOKS.get();
        banned_mob_effects = BANNED_MOB_EFFECTS.get();
        banned_summons = BANNED_SUMMONS.get();

        // convert the list of strings into a set of items
        //items = ITEM_STRINGS.get().stream()
         //       .map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse((itemName)))
          //      .collect(Collectors.toSet());
    }
}
