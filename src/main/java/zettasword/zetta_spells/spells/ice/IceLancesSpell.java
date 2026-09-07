package zettasword.zetta_spells.spells.ice;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.projectiles.OrbitingIceLanceEntity;

import java.util.List;

public class IceLancesSpell extends Spell {

    public IceLancesSpell() {
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        LivingEntity caster = ctx.caster();
        if (caster == null) return false;

        if (!ctx.world().isClientSide) {
            // Look for existing orbiting lances owned by the caster
            List<OrbitingIceLanceEntity> lances = ctx.world().getEntitiesOfClass(
                    OrbitingIceLanceEntity.class, 
                    caster.getBoundingBox().inflate(10), 
                    e -> e.getOwner() == caster && e.isOrbiting()
            );

            if (lances.isEmpty()) {
                // FIRST CAST: Spawn 5 lances in a half-circle
                float speed = property(DefaultProperties.SPEED);
                if (speed <= 0) speed = 3.0f;

                for (int i = 0; i < 5; i++) {
                    OrbitingIceLanceEntity lance = new OrbitingIceLanceEntity(ctx.world());
                    lance.setOwner(caster);
                    
                    // Spread from -90 degrees (left) to +90 degrees (right)
                    float offset = (float) Math.toRadians(-90.0 + (180.0 / 4.0) * i);
                    lance.setOrbitOffset(offset);
                    lance.setOrbiting(true);
                    
                    // Calculate initial spawn position
                    double radius = 1.0;
                    double yLevel = caster.getEyeY() + 1.25;
                    float angle = (float) (caster.yHeadRot * Math.PI / 180.0) + offset;
                    
                    double x = caster.getX() - Math.sin(angle) * radius;
                    double z = caster.getZ() + Math.cos(angle) * radius;
                    
                    lance.setPos(x, yLevel, z);
                    lance.setYRot(-caster.getYHeadRot());
                    lance.setYBodyRot(-caster.getYHeadRot());
                    lance.setYHeadRot(-caster.getYHeadRot());
                    lance.setXRot(-caster.getXRot());
                    
                    lance.damageMultiplier = ctx.modifiers().getFactor(SpellModifiers.POTENCY);
                    ctx.world().addFreshEntity(lance);
                }
            } else {
                // SECOND CAST: Fire the existing lances
                Vec3 look = caster.getLookAngle();
                float speed = property(DefaultProperties.SPEED);
                if (speed <= 0) speed = 3.0f;

                for (OrbitingIceLanceEntity lance : lances) {
                    lance.setOrbiting(false);
                    lance.setNoGravity(false);
                    lance.aim(caster, speed);
                }
            }
        }
        
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(
                        SpellTiers.ADVANCED, 
                        Elements.ICE, 
                        SpellTypes.PROJECTILE, 
                        SpellAction.POINT, 
                        120,
                        0,
                        20
                )
                .add(DefaultProperties.RANGE, 20F)
                .add(DefaultProperties.DAMAGE, 5F)
                .add(DefaultProperties.SPEED, 3.0F)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .build();
    }
}