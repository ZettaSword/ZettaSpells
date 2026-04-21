package zettasword.zetta_spells.system.spellcreation.actions.operations;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class ShiftPosWord extends SpellWord {
    public ShiftPosWord() {
        super("shift_pos");
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
        return ctx.getPrevious().equals("shift") && (words.get(i).equals("position") || words.get(i).equals("pos"));
    }

    /**
     * Use this to cast the spell you've made and registered.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        int number = ctx.getMod("shift", 0).getInt();
        String current = SpellCreator.next(words, i+1);
        if (number <= 0) return false;
        SpellTarget target = new SpellTarget();
        if (ctx.getTargets().isEmpty()) return false;
        BlockPos pos = ctx.getTarget().getTargetPos();
        LivingEntity caster = ctx.getCaster();
        if (current.equals("back")){ // Back
            target.setTargetPos(pos.relative(caster.getDirection().getOpposite(), number));
        }
        if (current.equals("forth")){ // Forth
            target.setTargetPos(pos.relative(caster.getDirection(), number));
        }
        if (current.equals("right")){ // Right
            target.setTargetPos(pos.relative(caster.getDirection().getClockWise(), number));
        }
        if (current.equals("left")){ // Left
            target.setTargetPos(pos.relative(caster.getDirection().getCounterClockWise(), number));
        }
        if (current.equals("down")){ //Down
            target.setTargetPos(pos.relative(Direction.DOWN, number));
        }
        if (current.equals("up")){ //Up
            target.setTargetPos(pos.relative(Direction.UP, number));
        }
        if (current.equals("look")){ // Following caster look
            Vec3 lookVec = caster.getLookAngle().scale(number);
            target.setTargetPos(pos.offset((int) Math.round(lookVec.x), (int) Math.round(lookVec.y), (int) Math.round(lookVec.z)));
        }
        ctx.getTargets().set(0, target);
        return true;
    }
}
