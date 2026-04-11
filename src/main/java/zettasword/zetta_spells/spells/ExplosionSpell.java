package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.system.Alchemy;

public class ExplosionSpell extends RaySpell {

    public ExplosionSpell(){
    }

    /**
     * Called when the ray hits an entity. Override this method to perform an action when the ray hits an entity, such as
     * dealing damage or applying a status effect. Return true if the spell should be considered successfully cast
     * when it hits an entity, or false if the spell should not be cast when it hits an entity (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param entityHit The result of the entity hit, containing information about the hit entity and hit position.
     * @param origin    The starting point of the ray.
     * @return true if the spell should be considered successfully cast when it hits an entity, false if the spell should
     * not be cast when it hits an entity.
     */
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!ctx.world().isClientSide){
            Vec3 pos = entityHit.getEntity().getPosition(1.0F);
            ctx.world().explode(ctx.caster(), pos.x, pos.y, pos.z,
                Math.min(4.0F * (ctx.modifiers().get(SpellModifiers.POTENCY) * 2 * ctx.modifiers().get(SpellModifiers.BLAST)), 50F), Level.ExplosionInteraction.TNT);

            Alchemy.apply(ctx.caster(), Math.min(10 * ctx.modifiers().get(SpellModifiers.POTENCY), 240),
                    2, MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS);
            Alchemy.apply(ctx.caster(), Math.min(10 * ctx.modifiers().get(SpellModifiers.POTENCY), 240),
                    4, MobEffects.MOVEMENT_SLOWDOWN);
        }
        return true;
    }

    /**
     * Called when the ray hits a block. Override this method to perform an action when the ray hits a block, such as
     * creating an explosion or spawning particles. Return true if the spell should be considered successfully cast
     * when it hits a block, or false if the spell should not be cast when it hits a block (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx      The cast context of the spell.
     * @param blockHit The result of the block hit, containing information about the hit position and block.
     * @param origin   The starting point of the ray.
     * @return true if the spell should be considered successfully cast when it hits a block, false if the spell should
     * not be cast when it hits a block.
     */
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!ctx.world().isClientSide){
            BlockPos pos = blockHit.getBlockPos();
            ctx.world().explode(ctx.caster(), pos.getX(), pos.getY(), pos.getZ(),
                    Math.min(4.0F * (ctx.modifiers().get(SpellModifiers.POTENCY) * 2 * ctx.modifiers().get(SpellModifiers.BLAST)), 50F), Level.ExplosionInteraction.TNT);

            Alchemy.apply(ctx.caster(), Math.min(10 * ctx.modifiers().get(SpellModifiers.POTENCY), 240),
                    2, MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS);
            Alchemy.apply(ctx.caster(), Math.min(10 * ctx.modifiers().get(SpellModifiers.POTENCY), 240),
                    4, MobEffects.MOVEMENT_SLOWDOWN);
        }
        return true;
    }

    /**
     * Called when the ray does not hit any entities or blocks. Override this method to perform an action when the ray
     * misses, such as spawning particles at the endpoint. Return true if the spell should be considered successfully
     * cast even when it misses, or false if the spell should not be cast when it misses (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param origin    The starting point of the ray.
     * @param direction The normalized direction vector of the ray.
     * @return true if the spell should be considered successfully cast even when it misses, false if the spell should
     * not be cast when it misses.
     */
    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    /**
     * This method is where you should set the default properties for your spell when creating a new spell class. This
     * method is called in the constructor of the Spell class, and the properties returned by this method are assigned
     * to the spell's properties field.
     *
     * @return A SpellProperties object with the default properties for your spell.
     */
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT, 500, 60, 1200)
                .add(DefaultProperties.RANGE, 300F)
                .build();
    }
}
