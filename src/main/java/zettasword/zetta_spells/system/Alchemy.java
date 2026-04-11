package zettasword.zetta_spells.system;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Helper class to apply potions faster.
 * **/
public class Alchemy {

    /** Applies mob effect as hidden.
     * Amplification 0 = 1 Level of potion.
     **/
    public static void apply(LivingEntity living, MobEffect effect, double seconds, int amplification){
        living.addEffect(new MobEffectInstance(effect, (int) (seconds * 20), amplification, false, false));
    }

    /** Applies multiple mob effect as hidden.
     * Amplification 0 = 1 Level of potion.
     **/
    public static void apply(LivingEntity living, double seconds, int amplification, MobEffect... effects){
        for (MobEffect effect : effects){
            living.addEffect(new MobEffectInstance(effect, (int) (seconds * 20), amplification, false, false));
        }
    }

    public static void applyNotHiding(LivingEntity living, MobEffect effect, double seconds, int amplification, @Nullable LivingEntity from){
        living.addEffect(new MobEffectInstance(effect, (int) (seconds * 20), amplification), from);
    }
}
