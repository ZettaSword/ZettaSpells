package zettasword.zetta_spells.spells.types;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class DomainSpell extends Spell {

    public DomainSpell(){
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

    protected abstract boolean createDomain(Level world, LivingEntity caster, SpellModifiers mods, int ticks);
}
