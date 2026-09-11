package zettasword.zetta_spells.spells.sorcery;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.network.MayFlyPacketS2C;
import zettasword.zetta_spells.network.PacketHandler;
import zettasword.zetta_spells.network.RaceCapabilitySyncPacketS2C;
import zettasword.zetta_spells.system.SigilCreator;

import javax.annotation.Nullable;

public class DisableGravity extends Spell {

    public DisableGravity(){
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
        if (!ctx.world().isClientSide) {
            Player player = ctx.caster();
            boolean to_set = !player.getAbilities().mayfly;
            player.getAbilities().mayfly=to_set;
            player.setDeltaMovement(0,0,0);
            ZSSigil sigil = SigilCreator.create(ctx.world(), player.getPosition(1.0F).add(new Vec3(0, 0.6,0)), 40, "sorcery");
            ctx.world().addFreshEntity(sigil);

            MayFlyPacketS2C packet = new MayFlyPacketS2C(to_set);
            PacketHandler.INSTANCE.sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
        return true;
    }

    /// This method is where you should set the default properties for your spell when creating a new spell class. This
    /// method is called in the constructor of the Spell class, and the properties returned by this method are assigned
    /// to the spell's properties field.
    ///
    /// @return A SpellProperties object with the default properties for your spell.
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.ALTERATION, SpellAction.IMBUE,
                50, 60, 60).build();
    }
}
