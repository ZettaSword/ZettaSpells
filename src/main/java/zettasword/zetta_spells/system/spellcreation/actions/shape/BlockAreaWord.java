package zettasword.zetta_spells.system.spellcreation.actions.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;
import java.util.stream.Stream;

public class BlockAreaWord extends TargetSpellWord {

    public BlockAreaWord() {
        super("block_area");
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
        return ctx.getPrevious().equals("affect") && words.get(i).equals("block") && SpellCreator.next(words, i+1).equals("area");
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
        int range = ctx.getMod("range", 5).getIntSafe();
        BlockPos pos = target.getTargetPos();
        if (pos != null){
            Stream<BlockPos> blockPosStream = BlockPos.betweenClosedStream(AABB.ofSize(pos.getCenter(), range, range, range));
            blockPosStream.forEach(ctx::addTarget);
            success();
        }
        return isSuccess();
    }
}
