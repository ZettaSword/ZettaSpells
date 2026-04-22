package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.particles.Alteria;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class PlaceBlockWord extends TargetSpellWord {
    public PlaceBlockWord() {
        super("place_block");
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
        return words.get(i).equals("place") && ctx.getCaster() instanceof Player;
    }

    @Override
    public boolean cast(SpellCreateContext ctx, SpellTarget target, List<String> words, int i) {
        InteractionHand hand = ctx.getHand();
        LivingEntity caster = ctx.getCaster();
        Level world = ctx.getWorld();
        BlockPos pos = target.getTargetPos();
        if (pos == null) return false;
        InteractionHand opposite_hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack opposite = caster.getItemInHand(opposite_hand);
        if (!opposite.isEmpty() && opposite.getItem() instanceof BlockItem blockItem) {
            if (BlockUtil.canBlockBeReplaced(world, pos)) {
                if (!world.isClientSide && BlockUtil.canPlaceBlock(caster, world, pos)) {
                    world.setBlock(pos, blockItem.getBlock().defaultBlockState(), 3);
                    opposite.shrink(1);
                }
                if (world.isClientSide){
                    Alteria.spawnBlockOutlineParticles(world, pos, EBParticles.SPARKLE, 0xFF9800, 5);
                }
            }
        }
        return isSuccess();
    }
}
