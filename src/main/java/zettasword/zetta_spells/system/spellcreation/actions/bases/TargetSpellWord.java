package zettasword.zetta_spells.system.spellcreation.actions.bases;

import net.minecraft.resources.ResourceLocation;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Class that allows to do something with targets, instead of writing for loops in each spellword. If any of targets will suffice -> It will return True.
 *  Use this class cast() instead of usual one in this case.
 * **/
public abstract class TargetSpellWord extends SpellWord {

    public TargetSpellWord(ResourceLocation registryName) {
        super(registryName);
    }

    public TargetSpellWord(String spellword) {
        super(spellword);
    }

    /**
     * Do not override it, this is required for our own cast method here.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        if (ctx.getTargets().isEmpty()) return false;

        Set<SpellTarget> processed = new HashSet<>();
        int index = 0;
        int safetyLimit = 1500; // Prevent infinite loops from recursive targeting

        while (index < ctx.getTargets().size() && safetyLimit-- > 0) {
            SpellTarget target = ctx.getTargets().get(index);

            // Skip nulls or targets we've already cast on
            if (target == null || processed.contains(target)) {
                index++;
                continue;
            }

            // Mark as processed BEFORE casting (prevents re-processing if cast() re-adds it)
            processed.add(target);

            // Execute spell logic
            if (cast(ctx, target, words, i)) {
                success();
            }

            // If target still exists at current index → it wasn't removed → move forward
            // If it's gone → list shifted → stay at current index to process what shifted in
            if (index < ctx.getTargets().size() && ctx.getTargets().get(index) == target) {
                index++;
            }
        }

        return isSuccess();
    }

    /**
     * Replaces default cast method with this one instead, which now returns True if any of this method cast will success();
     *
     * @param ctx   Context of the spell.
     * @param target SpellTarget.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    public abstract boolean cast(SpellCreateContext ctx, SpellTarget target, List<String> words, int i);
}
