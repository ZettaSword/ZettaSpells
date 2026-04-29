package zettasword.zetta_spells.system.spellcreation.actions.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.system.BlockStateUtils;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.particles.Alteria;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class ModifyBlockWord extends TargetSpellWord {
    public ModifyBlockWord() {
        super("modify_block");
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
        String next = SpellCreator.next(words, i+1);
        Level world = ctx.getWorld();
        BlockPos pos = target.getTargetPos();
        if (!world.isEmptyBlock(pos)){
            if (next.isEmpty()) next = "nothing";
            if (!world.isClientSide) {
                if (!BlockStateUtils.toggleBooleanProperty(world, pos, next)) {
                    BlockStateUtils.setIntProperty(world, pos, next, SpellCreator.getInt(SpellCreator.next(words, i+2)));
                }
            }
            if (world.isClientSide && ctx.canCreateFx()){
                Alteria.spawnBlockOutlineParticles(world, pos, ParticleTypes.HAPPY_VILLAGER, 5);
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
        return ctx.getPrevious().equals("modify") && words.get(i).equals("block");
    }
}
