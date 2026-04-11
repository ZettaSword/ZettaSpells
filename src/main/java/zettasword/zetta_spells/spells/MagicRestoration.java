package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.abstr.BuffSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.mob_effects.MagicRestorationMobEffect;
import zettasword.zetta_spells.mob_effects.ZSEffects;

public class MagicRestoration extends BuffSpell {

    public MagicRestoration() {
        super(255/255F, 152/255F, 0/255F, MagicRestorationMobEffect::new);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        int bonusAmplifier = getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY));

        if (ctx.world() != null){
            if(!ctx.world().isClientSide && caster != null){
                caster.addEffect(new MobEffectInstance(ZSEffects.MAGIC_RESTORATION.get(), ZSEffects.MAGIC_RESTORATION.get().isInstantenous() ? 1 :
                        (int) (this.property(BuffSpell.getEffectDurationProperty(ZSEffects.MAGIC_RESTORATION.get())) * ctx.modifiers().get(SpellModifiers.DURATION)),
                        this.property(BuffSpell.getEffectStrengthProperty(ZSEffects.MAGIC_RESTORATION.get())) + bonusAmplifier,
                        false, true));
                return true;
            }
        }
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.ADVANCED, Elements.MAGIC, SpellType.BUFF, SpellAction.POINT_UP, 200, 60, 600)
                .add(BuffSpell.getEffectDurationProperty(ZSEffects.MAGIC_RESTORATION.get()), 1200)
                .add(BuffSpell.getEffectStrengthProperty(ZSEffects.MAGIC_RESTORATION.get()), 0)
                .build();
    }
}
