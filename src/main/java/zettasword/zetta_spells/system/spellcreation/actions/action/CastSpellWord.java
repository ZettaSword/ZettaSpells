package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
                spell.cast(new PlayerCastContext(ctx.getWorld(), player, ctx.getHand(), 0, data.getSpellModifiers()));
            }
        }catch (Exception ignore){}
        return false;
    }
}
