package zettasword.zetta_spells.system.spellcreation.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpellsMod;
import zettasword.zetta_spells.system.spellcreation.actions.action.ApplyEffectWord;
import zettasword.zetta_spells.system.spellcreation.actions.action.CastSpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.shape.RayWord;

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

    // Actions
    public static final RegistryObject<SpellWord> APPLY_EFFECT = SPELL_WORDS.register("apply_effect", ApplyEffectWord::new);
    public static final RegistryObject<SpellWord> CAST = SPELL_WORDS.register("cast", CastSpellWord::new);


    /** Yay
     */
    public static void register(IEventBus modEventBus) {
        SPELL_WORDS.register(modEventBus);
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