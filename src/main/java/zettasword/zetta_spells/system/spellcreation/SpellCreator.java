package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.TextProcessingUtil;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWords;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class SpellCreator {

    public static SpellCreateContext spellCast(SpellCreateContext context, String spell) {
        if (SpellWord.getCurrentMana(context) <= 0 && !context.isCreative()) return context;
        Level world = context.getWorld();
        LivingEntity caster = context.getCaster();
        if (caster == null) return context;
        List<SpellTarget> targets = context.getTargets();
        boolean creative = context.isCreative();

        // Starting the spell-creation.
        List<String> words = TextProcessingUtil.extractWords(spell);
        String previous = "";
        HashMap<String, SVar> mods = context.getMods();

        for (int i = 0; i<words.size();i++){
            String current = words.get(i);
            String next = next(words, i+1);
            if (!next.isEmpty() && nextExist(words, i+2)){
                // Something like "Num duration 30" or "Truth destroy false" or "word element magic"
                // to set Duration to 30, Destroy to False, and Element to Magic.
                // Caster can define own parameters, it's spellwords job to check if those exist.
                processVariable(mods, current, next, next(words, i+2));

                // Example; Previous is 'Vec', Key is current ('target' as example), Value is three after. getCoordinates() gets three int after.
                // Vec target 0 10 0
                if (previous.equals("vec") || previous.equals("vector") || previous.equals("coordinates")){
                    Vec3 vec = getCoordinates(words, i);
                    if (vec != null) {
                        SVar mod = SVar.init(vec);
                        mods.put(current, mod);
                    }
                }

                context.setMods(mods);
            }

            for (SpellWord action : SpellWords.getWords()){
                if(action.shouldCast(context, words, i) && !action.cast(context, words, i) && !creative){
                    if (SpellWord.getCurrentMana(context) <= 0) break;
                }

                if (!context.getIf().test(context)){
                    context.stopSpell();
                    break;
                }
            }
            if (context.shouldStopSpell()) break;

            previous = current;
            context.setPrevious(current);
        }
        context.addCooldown(words.size());
        context.spellFinished();
        return context;
    }

    public static void processVariable(HashMap<String, SVar> mods, String type, String key, String value){
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
        if (key == null) return;
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
    public static Vec3 getCoordinates(List<String> words, int i){
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

    public static int getInt(String next){
        try {
            return Integer.parseInt(next);
        }catch (Exception ignore){
            return 0;
        }
    }

    public static int getInt(String next, int fallback){
        try {
            return Integer.parseInt(next);
        }catch (Exception ignore){
            return fallback;
        }
    }
}
