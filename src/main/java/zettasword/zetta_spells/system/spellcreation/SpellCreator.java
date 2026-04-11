package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.TextProcessingUtil;

import java.util.List;

public class SpellCreator {

    public static void spellCast(SpellCreateContext context, String spell) {
        Level world = context.getWorld();
        LivingEntity caster = context.getCaster();
        SpellTarget target = new SpellTarget(caster);

        // Starting the spell-creation.
        List<String> words = TextProcessingUtil.extractWords(spell);

    }

    public static void checkParameters(){

    }
}
