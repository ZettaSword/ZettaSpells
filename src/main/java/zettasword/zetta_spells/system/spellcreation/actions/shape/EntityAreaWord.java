package zettasword.zetta_spells.system.spellcreation.actions.shape;

import com.binaris.wizardry.api.content.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class EntityAreaWord extends SpellWord {

    public EntityAreaWord() {
        super("entity_area");
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
        return ctx.getPrevious().equals("affect") && words.get(i).equals("entity") && SpellCreator.next(words, i+1).equals("area");
    }

    /**
     * Replaces default cast method with this one instead, which now returns True if any of this method cast will success();
     *
     * @param ctx    Context of the spell.
     * @param words  Words used in the spell.
     * @param i      Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        if (ctx.getTargets().isEmpty()) return false;
        int range = ctx.getMod("area", 5).getInt();
        BlockPos pos = ctx.getTarget().getTargetPos();
        if (pos != null){
            List<Entity> entities = EntityUtil.getEntitiesInRange(ctx.getWorld(), pos.getX(), pos.getY(), pos.getZ(), range, Entity.class);
            entities.removeIf(t -> !ctx.filter().test(t));
            ctx.clearTargets();
            entities.forEach(ctx::addTarget);
            success();
        }
        return isSuccess();
    }
}
