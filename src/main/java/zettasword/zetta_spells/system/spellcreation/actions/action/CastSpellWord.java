package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.content.data.CastCommandData;
import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.core.registry.EBRegistries;
import com.binaris.wizardry.registry.EBRegistriesForge;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class CastSpellWord extends SpellWord {
    public CastSpellWord() {
        super("cast_spell");
    }

    /**
     * Allows us to get if key things are considered. Like if there is a word, and is a current target is a spider, and more, obviously.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean shouldCast(SpellCreateContext ctx, List<String> words, int i) {
        return ctx.getPrevious().equals("cast") && Services.REGISTRY_UTIL.getSpell(ResourceLocation.parse(words.get(i))) != null && ctx.getCaster() instanceof Player;
    }

    /**
     * Use this to cast the spell you've made and registered.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        Player player = (Player) ctx.getCaster();
        WizardData data = Services.OBJECT_DATA.getWizardData(player);
        if (data == null) return false;
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.parse(words.get(i)));
        if (spell == null || spell == Spells.NONE) return false;
        if (!Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell) && !player.isCreative()) return false;
        try {
            if (consumeMana(ctx, spell.getCost())) {
                if (spell.isInstantCast()) spell.cast(new PlayerCastContext(ctx.getWorld(), player, ctx.getHand(), 0, data.getSpellModifiers()));
                else handleContinuousSpell(spell, player, data.getSpellModifiers(), ctx.getMod("duration", 5).getIntSafe(1));
            }
            ctx.addCooldown(spell.getCooldown());
        }catch (Exception ignore){}
        return false;
    }

    private static void handleContinuousSpell(Spell spell, Player player, SpellModifiers modifiers, int duration) {
        CastCommandData data = Services.OBJECT_DATA.getCastCommandData(player);
        if (data.isCommandCasting()) {
            data.stopCastingContinuousSpell();
        } else {
            data.startCastingContinuousSpell(spell, modifiers, duration);
        }
    }

    /**
     * Resolves a Spell from a name string with flexible matching:
     * 1. Exact ResourceLocation match (e.g., "ebwizardry:fireball", "modid:custom_spell")
     * 2. Fallback to "ebwizardry:" prefix for shorthand names (e.g., "heal" → "ebwizardry:heal")
     * 3. Optional: fuzzy match by path only (use with caution for ambiguity)
     *
     * @param name The spell name from user input
     * @return The resolved Spell, or null/Spells.NONE if not found
     */
    private Spell findSpell(String name) {
        if (name == null || name.isBlank()) return null;

        // ── 1. Try exact ResourceLocation match first ──────────────────────
        try {
            ResourceLocation exactLoc = ResourceLocation.parse(name);
            Spell spell = Services.REGISTRY_UTIL.getSpell(exactLoc);
            if (spell != null && spell != Spells.NONE) return spell;
        } catch (ResourceLocationException e) {
            // Invalid format (e.g., missing colon), continue to fallback
        }

        // ── 2. Try ebwizardry fallback: assume "ebwizardry:" prefix ─────────
        if (!name.contains(":")) {
            ResourceLocation ebLoc = ResourceLocation.tryBuild("ebwizardry", name);
            if (ebLoc != null) {
                Spell spell = Services.REGISTRY_UTIL.getSpell(ebLoc);
                if (spell != null && spell != Spells.NONE) return spell;
            }
        }

        // ── 3. Optional: Fuzzy match by path only (disable for strict mode) ─
        // Only enable if you accept potential ambiguity between mods.
        // Comment out or remove if you prefer users must specify modid for non-ebwizardry spells.
        for (ResourceLocation loc : EBRegistriesForge.SPELL.get().getKeys()) {
            if (loc.getPath().equalsIgnoreCase(name)) {
                Spell spell = Services.REGISTRY_UTIL.getSpell(loc);
                if (spell != null && spell != Spells.NONE) {
                    return spell; // First match wins
                }
            }
        }

        return null; // Not found
    }
}
