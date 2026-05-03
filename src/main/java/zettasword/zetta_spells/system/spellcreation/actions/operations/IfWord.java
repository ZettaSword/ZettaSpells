package zettasword.zetta_spells.system.spellcreation.actions.operations;

import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class IfWord extends SpellWord {
    public IfWord() {
        super("if");
    }

    /**
     *
     *
     * @param ctx    Context of the spell.
     * @param words  Words used in the spell.
     * @param i      Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        if (words.get(i).equals("sneaking")){
            ctx.setIf(context->context.getCaster().isShiftKeyDown());
        }

        if (words.get(i).equals("sprinting")){
            ctx.setIf(context -> context.getCaster().isSprinting());
        }

        if (words.get(i).equals("light")){
            ctx.setIf(context ->
                    context.world().getLightEmission(context.getCaster().blockPosition()) >= context.getMod("light", 0).getInt());
        }

        // Inverse
        if (words.get(i).equals("not")){
            String next = SpellCreator.next(words, i+1);
            if (next.equals("sneaking")){
                ctx.setIf(context -> !context.getCaster().isShiftKeyDown());
            }

            if (next.equals("sprinting")){
                ctx.setIf(context -> context.getCaster().isSprinting());
            }

            if (next.equals("light")){
                ctx.setIf(context ->
                        context.world().getLightEmission(context.getCaster().blockPosition()) <= context.getMod("light", 0).getInt());
            }
        }
        return false;
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
        return ctx.getPrevious().equals("if");
    }
}
