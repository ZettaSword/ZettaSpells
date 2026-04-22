package zettasword.zetta_spells.system.spellcreation.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.system.spellcreation.actions.action.*;
import zettasword.zetta_spells.system.spellcreation.actions.operations.FilterWord;
import zettasword.zetta_spells.system.spellcreation.actions.operations.IfWord;
import zettasword.zetta_spells.system.spellcreation.actions.operations.ShiftPosWord;
import zettasword.zetta_spells.system.spellcreation.actions.shape.BlockAreaWord;
import zettasword.zetta_spells.system.spellcreation.actions.shape.EntityAreaWord;
import zettasword.zetta_spells.system.spellcreation.actions.shape.RayWord;
import zettasword.zetta_spells.system.spellcreation.actions.shape.SelfWord;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class SpellWords {
    // Unique registry key
    public static final ResourceLocation REGISTRY_KEY = ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "spell_words");

    // DeferredRegister for SpellAction
    public static final DeferredRegister<SpellWord> SPELL_WORDS = DeferredRegister.create(REGISTRY_KEY, ZettaSpells.MODID);
    public static final Supplier<IForgeRegistry<SpellWord>> SPELL_WORD = SPELL_WORDS.makeRegistry(() -> new RegistryBuilder<SpellWord>().disableSaving().disableOverrides());

    // Shapes
    public static final RegistryObject<SpellWord> RAY = regSpellWord(RayWord::new);
    public static final RegistryObject<SpellWord> SELF = regSpellWord(SelfWord::new);
    public static final RegistryObject<SpellWord> BLOCK_AREA = regSpellWord(BlockAreaWord::new);
    public static final RegistryObject<SpellWord> ENTITY_AREA = regSpellWord(EntityAreaWord::new);

    // Actions
    public static final RegistryObject<SpellWord> APPLY_EFFECT = regSpellWord(ApplyEffectWord::new);
    public static final RegistryObject<SpellWord> CAST = regSpellWord(CastSpellWord::new);
    public static final RegistryObject<SpellWord> BREAK_BLOCK = regSpellWord(BreakBlockWord::new);
    public static final RegistryObject<SpellWord> PLACE_BLOCK = regSpellWord(PlaceBlockWord::new);
    public static final RegistryObject<SpellWord> SUMMON = regSpellWord(SummonWord::new);

    // Operations
    public static final RegistryObject<SpellWord> FILTER = regSpellWord(FilterWord::new);
    public static final RegistryObject<SpellWord> IF = regSpellWord(IfWord::new);
    public static final RegistryObject<SpellWord> SHIFT_POSITION = regSpellWord(ShiftPosWord::new);


    /** Yay
     */
    public static void register(IEventBus modEventBus) {
        SPELL_WORDS.register(modEventBus);
    }

    public static RegistryObject<SpellWord> regSpellWord(final Supplier<SpellWord> sup){
        return SPELL_WORDS.register(sup.get().getRegistryName().getPath(), sup);
    }

    public static Optional<SpellWord> getAction(ResourceLocation id) {
        return Optional.ofNullable(SPELL_WORD.get().getValue(id));
    }

    public static Collection<SpellWord> getWords() {
        return SPELL_WORD.get().getValues().stream().toList();
    }

    public static Optional<SpellWord> getAction(String id) {
        if (!id.contains(":")) {
            id = ZettaSpells.MODID + ":" + id;
        }
        ResourceLocation loc = ResourceLocation.tryParse(id);
        return loc != null ? getAction(loc) : Optional.empty();
    }
}