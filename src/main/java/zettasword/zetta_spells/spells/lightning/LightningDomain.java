package zettasword.zetta_spells.spells.lightning;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.spells.types.DomainSpell;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.SigilCreator;

import java.util.Collections;
import java.util.List;

public class LightningDomain extends DomainSpell {

    public LightningDomain(){
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
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        this.playSoundLoop(world, entity, castTicks);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
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
