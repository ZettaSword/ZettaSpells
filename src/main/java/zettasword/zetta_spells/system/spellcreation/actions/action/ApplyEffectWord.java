package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class ApplyEffectWord extends SpellWord {
    public ApplyEffectWord() {
        super("apply_effect");
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
        return ctx.getPrevious().equals("apote") && ForgeRegistries.MOB_EFFECTS.containsKey(ResourceLocation.parse(words.get(i)));
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
        LivingEntity living = living(ctx.getTarget().getTargetEntity());
        if (living == null) return false;
        try {
            MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(words.get(i)));
            int duration = ctx.getMods().getOrDefault("duration", SVar.init(10)).getInt();
            int amplification = ctx.getMods().getOrDefault("amplification", SVar.init(1)).getInt();
            if (mobEffect != null && consumeMana(ctx, amplification*(duration * 2))) {
                if (!ctx.getWorld().isClientSide) {
                    living.addEffect(new MobEffectInstance(mobEffect, duration * 20,
                             amplification - 1, false, false));
                }
                // Client side
                if (ctx.getWorld().isClientSide) {
                    ParticleBuilder.create(EBParticles.BUFF).entity(living).color(mobEffect.getColor()).spawn(ctx.getWorld());
                }

                return true;
            }
        }catch (Exception ignore){}
        return false;
    }
}
