package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.content.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.particles.Alteria;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class BreakBlockWord extends TargetSpellWord {
    public BreakBlockWord() {
        super("break_block");
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
        return words.get(i).equals("break") && ctx.getCaster() instanceof Player;
    }

    @Override
    public boolean cast(SpellCreateContext ctx, SpellTarget target, List<String> words, int i) {
        Level world = ctx.getWorld();
        BlockPos pos = target.getTargetPos();
        LivingEntity caster = ctx.getCaster();
        if (!world.isEmptyBlock(pos) && consumeMana(ctx, 10)) {
            if (!world.isClientSide && BlockUtil.canBreak((Player) caster, world, pos, false)) {
                world.destroyBlock(pos, true, caster);
            }
            if (world.isClientSide) {
                Alteria.spawnBlockOutlineParticles(world, pos, ParticleTypes.HAPPY_VILLAGER, 5);
            }
            ctx.addCooldown(1);
            success();
        }
        return isSuccess();
    }
}
