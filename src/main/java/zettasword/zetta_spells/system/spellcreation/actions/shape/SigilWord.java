package zettasword.zetta_spells.system.spellcreation.actions.shape;

import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.construct.CustomSigil;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class SigilWord extends TargetSpellWord {

    public SigilWord() {
        super("sigil");
    }

    /**
     * Allows us to get if key things are considered. Like if there is a word, and is a current target is a spider, and more, obviously.
     *
     * @param ctx Context of the spell.
     * @param words   Words used in the spell.
     * @param i       Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean shouldCast(SpellCreateContext ctx, List<String> words, int i) {
        return words.get(i).equals("sigil");
    }

    /**
     * Replaces default cast method with this one instead, which now returns True if any of this method cast will success();
     *
     * @param ctx    Context of the spell.
     * @param target SpellTarget.
     * @param words  Words used in the spell.
     * @param i      Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, SpellTarget target, List<String> words, int i) {
        List<String> toCast = words.subList(i + 1, words.size());
        toCast.removeIf(p -> p.equals("sigil"));
        StringBuilder fin = new StringBuilder();
        for (String part : toCast){
            fin.append(part).append(" ");
        }
        if (!fin.isEmpty()){
            CustomSigil sigil = new CustomSigil(ctx.world());
            sigil.setCaster(ctx.getCaster());
            sigil.setLifetime(ctx.getMod("duration", 10).getInt() * 20);
            if (ctx.getMod("infinite", false).getBoolean()) {
                sigil.setLifetime(-1);
            }
            sigil.setOneTime(ctx.getMod("once", false).getBoolean());
            sigil.setLastMana(ctx.lastExternalMana());
            sigil.setPos(target.getTargetPos().getCenter());
            sigil.setSpell(fin.toString());
            ctx.world().addFreshEntity(sigil);
        }
        return true;
    }
}
