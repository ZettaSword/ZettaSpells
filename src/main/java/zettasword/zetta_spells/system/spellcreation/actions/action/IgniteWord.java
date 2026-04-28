package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.content.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class IgniteWord extends TargetSpellWord {
    public IgniteWord() {
        super("ignite");
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
        if (target.getTargetEntity() == null){
            BlockPos pos = target.getTargetPos();
            if (pos != null){
                BlockPos relativePos = pos.relative(ctx.getHitDirection());
                Level world = ctx.getWorld();
                if (world.isEmptyBlock(relativePos)){
                    if (!world.isClientSide && BlockUtil.canPlaceBlock(ctx.getCaster(), world, relativePos) && consumeMana(ctx, 10)) {
                        world.setBlockAndUpdate(relativePos, Blocks.FIRE.defaultBlockState());
                    }
                }
                ctx.addCooldown(1);
            }
        }
        if (target.getTargetEntity() != null && !ctx.getWorld().isClientSide){
            if (target.getTargetEntity() instanceof LivingEntity living && consumeMana(ctx, 10)) {
                living.setSecondsOnFire(ctx.getMod("duration", 5).getInt());
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
        return words.get(i).contains("ignite");
    }
}
