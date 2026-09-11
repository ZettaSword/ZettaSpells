package zettasword.zetta_spells.spells.necromancy;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.system.SigilCreator;

public class TurnOffMind extends RaySpell {
    public TurnOffMind(){
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof Mob mob){
            if (mob.getHealth() < ctx.caster().getHealth()) {
                if (!ctx.world().isClientSide) {
                    mob.setNoAi(!mob.isNoAi());
                    mob.getBrain().clearMemories();
                    //mob.removeFreeWill();

                    ZSSigil sigil = SigilCreator.create(ctx.world(), mob.position(), 40, "necromancy");
                    ctx.world().addFreshEntity(sigil);
                    sigil = SigilCreator.create(ctx.world(), mob.position(), 30, "necromancy");
                    sigil.setDeltaMovement(0, 0.5, 0);
                    ctx.world().addFreshEntity(sigil);
                }
            }else{
                if (ctx.caster() instanceof Player player && !ctx.world().isClientSide) {
                    player.displayClientMessage(Component.translatable("spell.zetta_spells.turn_off_mind.refuse"), true);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellTypes.ALTERATION, SpellAction.POINT, 100, 20, 60)
                .add(DefaultProperties.RANGE, 14F)
                .build();
    }
}
