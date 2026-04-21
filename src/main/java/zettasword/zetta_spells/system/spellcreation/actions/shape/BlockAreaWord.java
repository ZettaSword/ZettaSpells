package zettasword.zetta_spells.system.spellcreation.actions.shape;

import com.binaris.wizardry.api.content.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import zettasword.zetta_spells.ZettaSpellsMod;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BlockAreaWord extends SpellWord {

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
     * @param words  Words used in the spell.
     * @param i      Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        if (ctx.getTargets().isEmpty()) return false;
        int area = ctx.getMod("area", 5).getIntSafe();
        BlockPos pos = ctx.getTarget().getTargetPos();
        List<BlockPos> blockPosList = getBlockCube(pos, area);
        ctx.clearTargets();
        for (BlockPos position : blockPosList){
            ctx.addTarget(position);
        }
        ZettaSpellsMod.LOGGER.warn("AFFECTING: {}", ctx.getTargets().size());
        success();
        return isSuccess();
    }

    public List<BlockPos> getBlockCube(BlockPos center, int area){
        List<BlockPos> result = new ArrayList<>();

        for (int x = -area; x <= area; x++) {
            for (int y = -area; y <= area; y++) {
                for (int z = -area; z <= area; z++) {
                    result.add(center.offset(x, y, z));
                }
            }
        }
        return result;
    }
}
