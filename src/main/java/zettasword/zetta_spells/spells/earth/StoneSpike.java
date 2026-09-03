package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

// AI generated with Qwen, modified by me.
public class StoneSpike extends RaySpell {

    public StoneSpike() {
        super();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellTypes.ATTACK, SpellAction.POINT, 25, 60, 30)
                .add(DefaultProperties.RANGE, 20.0F)
                .add(DefaultProperties.DAMAGE, 8.0F)
                .build();
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity living) {
            
            if (!ctx.world().isClientSide) {
                float potency = ctx.modifiers().get(SpellModifiers.POTENCY);
                float durationMod = ctx.modifiers().get(SpellModifiers.DURATION);

                float damage = this.property(DefaultProperties.DAMAGE) * potency;
                living.hurt(MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.MAGIC), damage);

                double knockup = 0.5 + (potency * 0.1);
                living.push(0, knockup, 0);
                living.hasImpulse = true;

                int duration = (int) (100 * durationMod);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 1));

                BlockPos groundPos = living.blockPosition().below();
                ServerLevel serverLevel = (ServerLevel) ctx.world();

                serverLevel.playSound(null, living.getX(), living.getY(), living.getZ(),
                        SoundEvents.STONE_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);

                BlockState state = ctx.world().getBlockState(groundPos);
                serverLevel.levelEvent(2001, groundPos, Block.getId(state));
            }
            
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!ctx.world().isClientSide) {
            BlockPos pos = blockHit.getBlockPos();
            ServerLevel serverLevel = (ServerLevel) ctx.world();
            serverLevel.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.STONE_HIT, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(15).color(0.4f, 0.3f, 0.2f).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.3f, 0.2f, 0.1f).spawn(ctx.world());
    }
}