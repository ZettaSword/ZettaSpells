package zettasword.zetta_spells.system.spellcreation.actions.action;

import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class TurnFxWord extends SpellWord {
    public TurnFxWord() {
        super("turn_fx");
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
        return words.get(i).equals("turn") && SpellCreator.next(words, i+1).equals("fx");
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
        String next = SpellCreator.next(words, i+2);
        if (next.equals("yes") || next.equals("true") || next.equals("on")){
            ctx.setCreateFx(true);
            return true;
        }
        if (next.equals("no") || next.equals("false") || next.equals("off")){
            ctx.setCreateFx(false);
            return true;
        }
        // If nothing from above, just switch it
        ctx.setCreateFx(!ctx.canCreateFx());
        return true;
    }
}
