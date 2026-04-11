package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.BuffSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.mob_effects.HeatMobEffect;
import zettasword.zetta_spells.mob_effects.ZSEffects;

public class HeatHealing extends BuffSpell {
    public HeatHealing() {
        super(1.0F, 0.3F, 0.3F, HeatMobEffect::new);
    }

    public static void heal(LivingEntity entity, float health) {
        float excessHealth = entity.getHealth() + health - entity.getMaxHealth();
        entity.heal(health);
        if (excessHealth > 0.0F && entity instanceof Player) {
            burn(entity);
        }
    }

    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        int bonusAmplifier = getStandardBonusAmplifier(ctx.modifiers().get("potency"));
        if (caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0.0F) {
            float excessHealth = caster.getHealth() + (Float)this.property(DefaultProperties.HEALTH) * ctx.modifiers().get("potency") - caster.getMaxHealth();
            if (excessHealth > 0.0F && caster instanceof Player) {
                if (!ctx.world().isClientSide())
                    caster.addEffect(new MobEffectInstance(ZSEffects.HEAT.get(), ZSEffects.HEAT.get().isInstantenous() ? 1 : (int)((float) this.property(getEffectDurationProperty(ZSEffects.HEAT.get())) * ctx.modifiers().get(SpellModifiers.DURATION)), (Integer)this.property(getEffectStrengthProperty(ZSEffects.HEAT.get())) + bonusAmplifier, false, true));
                burn(caster);
            }
            return true;
        } else {
            if (caster.getHealth() <= 0.0F) return false;
            if (!ctx.world().isClientSide())
                caster.addEffect(new MobEffectInstance(ZSEffects.HEAT.get(), ZSEffects.HEAT.get().isInstantenous() ? 1 : (int)((float) this.property(getEffectDurationProperty(ZSEffects.HEAT.get())) * ctx.modifiers().get(SpellModifiers.DURATION)), (Integer)this.property(getEffectStrengthProperty(ZSEffects.HEAT.get())) + bonusAmplifier, false, true));
            burn(caster);
            return false;
        }
    }

    public static void burn(LivingEntity entity){
        entity.setSecondsOnFire(15);
    }

    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.ALTERATION, SpellAction.IMBUE, 20, 50, 50).add(DefaultProperties.HEALTH, 6.0F).add(BuffSpell.getEffectDurationProperty(ZSEffects.HEAT.get()), 15*20).add(BuffSpell.getEffectStrengthProperty(ZSEffects.HEAT.get()), 0).build();
    }
}
