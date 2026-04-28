package zettasword.zetta_spells.system.spellcreation.actions.action;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class InformationWord extends TargetSpellWord {
    public InformationWord() {
        super("information");
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
        return words.get(i).equals("info") && !ctx.getTargets().isEmpty();
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
        // Block Pos!
        if (target.getTargetEntity() == null){
            BlockPos pos = target.getTargetPos();
            if (pos != null){
                StringBuilder result = new StringBuilder("[" + ctx.getWorld().getBlockState(pos) + "] ");
                result.append("\n [").append(ctx.getWorld().getBlockState(pos).getValues()).append("] ");
                boolean flag = consumeMana(ctx, result.length());
                if (flag && !ctx.getWorld().isClientSide) {
                    ctx.getCaster().sendSystemMessage(Component.literal(result.toString()));
                }
                return flag;
            }
        }

        // Entity!
        if (target.getTargetEntity() != null){
            Entity entity = target.getTargetEntity();
            StringBuilder result;
            if (entity instanceof LivingEntity living){
                result = new StringBuilder("["+ living + "] ");
                for (MobEffectInstance instance : living.getActiveEffects()){
                    result.append("[").append(instance.toString()).append("] ");
                }
            }else {
                result = new StringBuilder(entity.toString());
            }
            result.append("\n [entity_type: ").append(entity.getType()).append("] ");
            boolean flag = consumeMana(ctx, result.length());
            if (flag && !ctx.getWorld().isClientSide) {
                ctx.getCaster().sendSystemMessage(Component.literal(result.toString()));
            }
            return flag;
        }
        return false;
    }
}
