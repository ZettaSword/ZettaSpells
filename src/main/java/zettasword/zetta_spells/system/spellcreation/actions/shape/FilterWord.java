package zettasword.zetta_spells.system.spellcreation.actions.shape;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class FilterWord extends SpellWord {

    public FilterWord() {
        super("filter");
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
        return words.get(i).equals("filter") && SpellCreator.nextExist(words, i+1);
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
        String filter = SpellCreator.next(words, i+1);
        if (filter.equals("living")){
            ctx.setFilter(entity -> entity instanceof LivingEntity);
        }
        if (filter.equals("undead")){
            ctx.setFilter(entity -> entity instanceof LivingEntity living && living.getMobType().equals(MobType.UNDEAD));
        }
        if (filter.equals("none")){
            ctx.setFilter(entity -> true);
        }
        return true;
    }
}
