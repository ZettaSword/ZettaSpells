package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.TextProcessingUtil;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWords;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class SpellCreator {

    public static void spellCast(SpellCreateContext context, String spell) {
        if (SpellWord.getCurrentMana(context) <= 0 && !context.isCreative()) return;
        Level world = context.getWorld();
        LivingEntity caster = context.getCaster();
        List<SpellTarget> targets = context.getTargets();
        boolean creative = context.isCreative();

        // Starting the spell-creation.
        List<String> words = TextProcessingUtil.extractWords(spell);
        String previous = "";
        Map<String, SVar> mods = context.getMods();

        for (int i = 0; i<words.size();i++){
            String current = words.get(i);
            String next = next(words, i+1);
            if (!next.isEmpty() && nextExist(words, i+2)){
                // Something like "Num duration 30" or "Truth destroy false" or "word element magic"
                // to set Duration to 30, Destroy to False, and Element to Magic.
                // Caster can define own parameters, it's spellwords job to check if those exist.
                processVariable(mods, current, next, next(words, i+2));
            }

            for (SpellWord action : SpellWords.getWords()){
                if(action.shouldCast(context, words, i) && !action.cast(context, words, i) && !creative){
                    if (SpellWord.getCurrentMana(context) <= 0) break;
                }
            }

            previous = current;
            context.setPrevious(current);
        }
    }

    public static void processVariable(Map<String, SVar> mods, String type, String key, String value){
        SVar mod = null;
        if (type.equals("num") || type.equals("number")) {
            try {
                mod = SVar.init(Integer.parseInt(value));
            } catch (Exception ignore) {
            }
        }
        if (type.equals("truth") || type.equals("boolean")) mod = SVar.init(Boolean.getBoolean(value));
        if (type.equals("word") || type.equals("string")) mod = SVar.init(!value.isEmpty() ? value : "empty");
        // Add nothing if mod is false.
        if (mod == null) return;
        mods.put(key, mod);
    }

    // Helping methods
    /** We don't know if everything is in bounds after all! :D
     * **/
    public static boolean nextExist(List<String> words, int i){
        return i < words.size();
    }

    /** I'm just lazy to type it every time, really.
     * **/
    public static String next(List<String> words, int i){
        return nextExist(words, i) ? words.get(i) : "";
    }

    /** Gets coordinates (x,y,z) to do something with them, if it is not possible - returns Null.
     * **/
    @Nullable
    public static Vec3 getCords(List<String> words, int i){
        if (nextExist(words, i+1) && nextExist(words, i+2) && nextExist(words, i+3)) {
            int x,y,z;
            try {
                x = Integer.parseInt(words.get(i+1));
                y = Integer.parseInt(words.get(i+2));
                z = Integer.parseInt(words.get(i+3));
            }catch (NumberFormatException exception){
                return null;
            }
            return new Vec3(x,y,z);
        }
        return null;
    }
}
