package zettasword.zetta_spells.system.spellcreation.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpellsMod;
import zettasword.zetta_spells.system.spellcreation.actions.action.ApplyEffectWord;
import zettasword.zetta_spells.system.spellcreation.actions.action.BreakBlockWord;
import zettasword.zetta_spells.system.spellcreation.actions.action.CastSpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.action.SummonWord;
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
    public static final ResourceLocation REGISTRY_KEY = ResourceLocation.fromNamespaceAndPath(ZettaSpellsMod.MODID, "spell_words");

    // DeferredRegister for SpellAction
    public static final DeferredRegister<SpellWord> SPELL_WORDS = DeferredRegister.create(REGISTRY_KEY, ZettaSpellsMod.MODID);
    public static final Supplier<IForgeRegistry<SpellWord>> SPELL_WORD = SPELL_WORDS.makeRegistry(() -> new RegistryBuilder<SpellWord>().disableSaving().disableOverrides());

    // Shapes
    public static final RegistryObject<SpellWord> RAY = SPELL_WORDS.register("ray", RayWord::new);
    public static final RegistryObject<SpellWord> SELF = SPELL_WORDS.register("self", SelfWord::new);
    public static final RegistryObject<SpellWord> BLOCK_AREA = SPELL_WORDS.register("block_area", BlockAreaWord::new);
    public static final RegistryObject<SpellWord> ENTITY_AREA = SPELL_WORDS.register("entity_area", EntityAreaWord::new);

    // Actions
    public static final RegistryObject<SpellWord> APPLY_EFFECT = SPELL_WORDS.register("apply_effect", ApplyEffectWord::new);
    public static final RegistryObject<SpellWord> CAST = SPELL_WORDS.register("cast", CastSpellWord::new);
    public static final RegistryObject<SpellWord> BREAK_BLOCK = SPELL_WORDS.register("break_block", BreakBlockWord::new);
    public static final RegistryObject<SpellWord> SUMMON = SPELL_WORDS.register("summon", SummonWord::new);

    // Operations
    public static final RegistryObject<SpellWord> FILTER = SPELL_WORDS.register("filter", FilterWord::new);
    public static final RegistryObject<SpellWord> IF = SPELL_WORDS.register("if", IfWord::new);
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
            id = ZettaSpellsMod.MODID + ":" + id;
        }
        ResourceLocation loc = ResourceLocation.tryParse(id);
        return loc != null ? getAction(loc) : Optional.empty();
    }
}