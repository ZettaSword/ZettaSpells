package zettasword.zetta_spells.spells.magic;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.ArcaneColor;
import zettasword.zetta_spells.system.SigilCreator;

public class MahouImpact extends RaySpell {

    public MahouImpact(){
        ignoreUncollidables(true);
    }

    /// Called when the ray hits an entity. Override this method to perform an action when the ray hits an entity, such as
    /// dealing damage or applying a status effect. Return true if the spell should be considered successfully cast
    /// when it hits an entity, or false if the spell should not be cast when it hits an entity (e.g. if you want to prevent
    /// casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
    ///
    /// @param ctx       The cast context of the spell.
    /// @param entityHit The result of the entity hit, containing information about the hit entity and hit position.
    /// @param origin    The starting point of the ray.
    /// @return true if the spell should be considered successfully cast when it hits an entity, false if the spell should
    /// not be cast when it hits an entity.
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target){
            if (!ctx.world().isClientSide()){
                Alchemy.apply(target, MobEffects.LEVITATION, 5, 0);
                Alchemy.apply(target, MobEffects.GLOWING, 15, 0);
                Alchemy.apply(target, MobEffects.MOVEMENT_SLOWDOWN, 30, 1);
                MagicDamageSource.causeMagicDamage(ctx.caster(), target, property(DefaultProperties.DAMAGE), EBDamageSources.MAGIC);

                ZSSigil sigil = SigilCreator.create(ctx.world(), target.getPosition(1.0F), 40, "mahou");
                ctx.world().addFreshEntity(sigil);
            }
        }
        return true;
    }

    /// Called when the ray hits a block. Override this method to perform an action when the ray hits a block, such as
    /// creating an explosion or spawning particles. Return true if the spell should be considered successfully cast
    /// when it hits a block, or false if the spell should not be cast when it hits a block (e.g. if you want to prevent
    /// casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
    ///
    /// @param ctx      The cast context of the spell.
    /// @param blockHit The result of the block hit, containing information about the hit position and block.
    /// @param origin   The starting point of the ray.
    /// @return true if the spell should be considered successfully cast when it hits a block, false if the spell should
    /// not be cast when it hits a block.
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    /// Called when the ray does not hit any entities or blocks. Override this method to perform an action when the ray
    /// misses, such as spawning particles at the endpoint. Return true if the spell should be considered successfully
    /// cast even when it misses, or false if the spell should not be cast when it misses (e.g. if you want to prevent
    /// casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
    ///
    /// @param ctx       The cast context of the spell.
    /// @param origin    The starting point of the ray.
    /// @param direction The normalized direction vector of the ray.
    /// @return true if the spell should be considered successfully cast even when it misses, false if the spell should
    /// not be cast when it misses.
    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void spawnParticleRay(CastContext ctx, Vec3 origin, Vec3 direction, double distance) {
        if (ctx.caster() != null) {
            ParticleBuilder.create(EBParticles.BEAM).entity(ctx.caster()).pos(origin.subtract(ctx.caster().position()))
                    .length(distance).color(ArcaneColor.ARCANE)
                    .scale((float) (Math.sin(ctx.caster().tickCount * 0.2f) * 0.1f + 1.4f)).spawn(ctx.world());
        } else {
            ParticleBuilder.create(EBParticles.BEAM).pos(origin).target(origin.add(direction.scale(distance)))
                    .color(ArcaneColor.ARCANE)
                    .scale((float) (Math.sin(ClientUtils.getPlayer().tickCount * 0.2f) * 0.1f + 1.4f)).spawn(ctx.world());
        }
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        this.playSoundLoop(world, entity, castTicks);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
    }

    /// This method is where you should set the default properties for your spell when creating a new spell class. This
    /// method is called in the constructor of the Spell class, and the properties returned by this method are assigned
    /// to the spell's properties field.
    ///
    /// @return A SpellProperties object with the default properties for your spell.
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.ADVANCED, Elements.MAGIC, SpellTypes.ATTACK, SpellAction.POINT,
                20,0,40).add(DefaultProperties.DAMAGE, 5.0F).add(DefaultProperties.RANGE, 20F).build();
    }
}
