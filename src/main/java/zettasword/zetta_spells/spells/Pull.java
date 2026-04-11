package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Pull extends RaySpell {
    public Pull(){
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return pullTowards(entityHit.getEntity(), ctx.caster(), 0.5D, 4, 20, false);
    }

    public static boolean pullTowards(Entity target, Entity caster, double baseForce,
                                   double minDistance, double maxDistance, boolean horizontalOnly) {
        // 1. Server-side enforcement
        if (target.level().isClientSide()) return false;
        if (target == caster || !target.isAlive() || !caster.isAlive()) return false;

        Vec3 targetPos = target.position();
        Vec3 casterPos = caster.position();
        double distance = targetPos.distanceTo(casterPos);

        // 2. Distance bounds check
        if (distance <= minDistance || distance >= maxDistance) return false;

        // 3. Calculate direction
        Vec3 direction = casterPos.subtract(targetPos);
        if (horizontalOnly) {
            direction = new Vec3(direction.x, 0, direction.z);
        }
        double dirLen = direction.length();
        if (dirLen < 0.001) return false; // Prevent NaN on normalize
        direction = direction.scale(1.0 / dirLen); // Normalize

        // 4. Distance-based force calculation (Linear falloff)
        // Force = baseForce * (1.0 at minDistance → 0.0 at maxDistance)
        double forceRatio = (maxDistance - distance) / (maxDistance - minDistance);
        double force = baseForce * forceRatio;

        // 5. Apply to existing delta movement
        Vec3 currentMotion = target.getDeltaMovement();
        Vec3 newMotion = currentMotion.add(direction.scale(force));

        // 6. Clamp velocity to prevent physics breakage
        double maxSpeed = 2.5; // Minecraft's typical soft limit for entity motion
        if (newMotion.lengthSqr() > maxSpeed * maxSpeed) {
            newMotion = newMotion.normalize().scale(maxSpeed);
        }

        target.setDeltaMovement(newMotion);
        return true;
    }


    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT, 10, 0, 20)
                .add(DefaultProperties.RANGE, 14F)
                .build();
    }
}
