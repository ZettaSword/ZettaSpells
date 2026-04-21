package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class ApplyEffectWord extends TargetSpellWord {
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
        return ctx.getPrevious().equals("apply");
    }

    @Override
    public boolean cast(SpellCreateContext ctx, SpellTarget target, List<String> words, int i) {
        LivingEntity living = living(target.getTargetEntity());
        if (living == null) return false;
        try {
            // Get effect name from command words
            if (i >= words.size()) return false;
            String effectName = words.get(i);

            MobEffect mobEffect = findMobEffect(effectName);
            if (mobEffect == null) return false;

            int duration = ctx.getMods().getOrDefault("duration", SVar.init(10)).getInt();
            int amplification = ctx.getMods().getOrDefault("amplification", SVar.init(1)).getInt();

            if (consumeMana(ctx, amplification * (duration * 2))) {
                // Server side: apply effect
                if (!ctx.getWorld().isClientSide) {
                    living.addEffect(new MobEffectInstance(
                            mobEffect,
                            duration * 20,           // ticks
                            amplification - 1,       // amplifier is 0-based
                            false, false             // no particles, no icon
                    ));
                }
                // Client side: spawn visual feedback
                if (ctx.getWorld().isClientSide) {
                    ParticleBuilder.create(EBParticles.BUFF)
                            .entity(living)
                            .color(mobEffect.getColor())
                            .spawn(ctx.getWorld());
                }
                success();
            }
        } catch (Exception ignore) {
        }
        return isSuccess();
    }

    /**
     * Resolves a MobEffect from a name string with flexible matching:
     * 1. Exact ResourceLocation match (e.g., "minecraft:speed", "modid:effect")
     * 2. Fallback to "minecraft:" prefix for vanilla effects (e.g., "speed" → "minecraft:speed")
     * 3. Optional: linear search by path only if ambiguous matches are acceptable
     *
     * @param name The effect name from user input
     * @return The resolved MobEffect, or null if not found
     */
    private MobEffect findMobEffect(String name) {
        if (name == null || name.isBlank()) return null;

        // ── 1. Try exact ResourceLocation match first ──────────────────────
        try {
            ResourceLocation exactLoc = ResourceLocation.parse(name);
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(exactLoc);
            if (effect != null) return effect;
        } catch (ResourceLocationException e) {
            // Invalid format (e.g., missing colon in modid:path), continue to fallback
        }

        // ── 2. Try vanilla fallback: assume "minecraft:" prefix ─────────────
        if (!name.contains(":")) {
            ResourceLocation vanillaLoc = ResourceLocation.tryBuild("minecraft", name);
            if (vanillaLoc != null) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(vanillaLoc);
                if (effect != null) return effect;
            }
        }

        // ── 3. Optionality ───────
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            ResourceLocation loc = ForgeRegistries.MOB_EFFECTS.getKey(effect);
            if (loc != null && loc.getPath().equalsIgnoreCase(name)) {
                return effect; // First match wins
            }
        }

        return null; // Not found
    }
}
