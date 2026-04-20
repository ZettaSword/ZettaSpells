package zettasword.zetta_spells.system.spellcreation.actions.shape;

import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class SigilWord extends SpellWord {

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
     * Use this to cast the spell you've made and registered.
     *
     * @param ctx Context of the spell.
     * @param words   Words used in the spell.
     * @param i       Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        if (i >= 0 && i < words.size()) {
            List<String> toCast = words.subList(i + 1, words.size());
            return true;
        }
        return false;
    }
}
