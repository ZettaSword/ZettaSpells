package zettasword.zetta_spells.spells.lightning;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.entity.projectile.SparkBombEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.SigilCreator;

import java.util.Collections;
import java.util.List;

public class LightningDomain extends Spell {

    public LightningDomain(){
    }

    /// This cast method is meant to be used for spells that are cast by a player source. This is useful for spells that
    /// are meant to be cast by players, as it provides more information about the caster and the context of the cast.
    ///
    /// Override this method to implement the casting behavior for spells that are meant to be cast by players.
    ///
    /// @param ctx The context of the spell cast, containing information about the world, caster, hand used, modifiers, etc.
    /// @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
    /// considered as having been cast, so no cooldown will be applied.
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Level world = ctx.world();
        LivingEntity caster = ctx.caster();
        SpellModifiers mods = ctx.modifiers();
        return createDomain(world, caster, mods, ctx.castingTicks());
    }

    /// This cast method is meant to be used for spells that are cast by an entity source, like a mob. This is
    /// useful for spells that are meant to be cast by entities, as it provides more information about the caster and the
    /// context of the cast.
    ///
    /// Override this method to implement the casting behavior for spells that are meant to be cast by entities.
    ///
    /// @param ctx The context of the spell cast, containing information about the world, caster, modifiers, etc.
    /// @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
    /// considered as having been cast, so no cooldown will be applied.
    @Override
    public boolean cast(EntityCastContext ctx) {
        Level world = ctx.world();
        LivingEntity caster = ctx.caster();
        SpellModifiers mods = ctx.modifiers();
        return createDomain(world, caster, mods, ctx.castingTicks());
    }

    public boolean createDomain(Level world, LivingEntity caster, SpellModifiers mods, int ticks) {
        List<LivingEntity> livingEntityList =
                EntityUtil.getLivingEntitiesInRange(world, caster.getX(), caster.getY(), caster.getZ(), 30);
        if (!livingEntityList.isEmpty()) {
            livingEntityList.removeIf(e -> (e == caster));
            livingEntityList.removeIf(e -> AllyDesignation.isAllied(caster,e));

            if (ticks % 20 == 0 && !world.isClientSide()) {
                Collections.shuffle(livingEntityList);
                int maxCount = (int) (1 + (mods.get(SpellModifiers.BLAST, 1) / 2));
                int count = 0;
                for (LivingEntity target : livingEntityList){
                    MagicDamageSource.causeMagicDamage(caster, target, property(DefaultProperties.DAMAGE), EBDamageSources.SHOCK);
                    Alchemy.applyNotHiding(target, EBMobEffects.PARALYSIS.get(), property(DefaultProperties.EFFECT_DURATION), property(DefaultProperties.EFFECT_STRENGTH), caster);
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(world);
                    if (lightningbolt != null) {
                        lightningbolt.moveTo(Vec3.atBottomCenterOf(target.getOnPos()));
                        lightningbolt.setVisualOnly(false);
                        world.addFreshEntity(lightningbolt);
                        ParticleBuilder.create(EBParticles.LIGHTNING)
                                .pos(target.position()).target(target).time(1)
                                .allowServer(true).spawn(world);
                    }
                    ZSSigil sigil = SigilCreator.create(world, target.getPosition(1.0F), 40, "lightning");
                    world.addFreshEntity(sigil);

                    count++;
                    if (count >= maxCount) break;
                }
            }
            return true;
        }
        return false;
    }



    @Override
    public boolean isInstantCast() {
        return false;
    }

    /// This method is where you should set the default properties for your spell when creating a new spell class. This
    /// method is called in the constructor of the Spell class, and the properties returned by this method are assigned
    /// to the spell's properties field.
    ///
    /// @return A SpellProperties object with the default properties for your spell.
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.MASTER, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT_UP, 20,50,200)
                .add(DefaultProperties.DAMAGE, 10.0F).add(DefaultProperties.EFFECT_DURATION, 2).add(DefaultProperties.EFFECT_STRENGTH, 0).build();
    }
}
