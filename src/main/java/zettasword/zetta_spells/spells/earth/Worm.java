package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.AllyDesignation;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.system.SigilCreator;

import java.util.List;

// AI generated with Qwen, modified by me.
public class Worm extends RaySpell {

    public Worm() {
        super();
        ignoreLivingEntities(true);
    }

    /// Whether this spell is instant or not. An instant spell is a spell that is cast in a single tick, (it could have
    /// cooldown and/or charge-up time) and does not have a duration. By default, this returns true, as most spells are
    /// instant, but you can override this to return false if your spell is meant to have a duration and be cast over
    /// multiple ticks.
    ///
    /// @return true if this spell is instant, false otherwise.
    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellTypes.UTILITY, SpellAction.POINT, 25, 20, 20)
                .add(DefaultProperties.RANGE, 4.0F)
                .build();
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        executeWorming(ctx.caster(), blockHit.getBlockPos(), 1, 4F);
        return true;
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

    /**
     * Pushes the entity in its look direction and breaks a 3x3 tunnel starting from the given BlockPos.
     *
     * @param entity       The entity performing the dash (Player or Mob).
     * @param startPos     The center of the first 3x3 cross-section to break.
     * @param distance     How many blocks forward to tunnel (including startPos).
     * @param pushStrength The velocity multiplier for the push.
     */
    public static void executeWorming(Entity entity, BlockPos startPos, int distance, float pushStrength) {
        Level level = entity.level();

        if (level.isClientSide()) {
            return;
        }

        Vec3 lookVector = entity.getLookAngle();

        // Calculate orthogonal vectors for the 3x3 grid
        Vec3 upVector = entity.getUpVector(1.0F);
        Vec3 rightVector = lookVector.cross(upVector);

        if (rightVector.lengthSqr() < 1e-6) {
            rightVector = new Vec3(1, 0, 0);
        }
        rightVector = rightVector.normalize();
        upVector = rightVector.cross(lookVector).normalize();

        boolean isPlayer = entity instanceof Player;

        // Convert the provided BlockPos to a Vec3 center for offset calculations
        Vec3 center = startPos.getCenter();

        // i = 0 means the first slice is exactly at your provided startPos
        for (int i = 0; i < distance; i++) {
            Vec3 sliceCenter = center.add(lookVector.scale(i));
            boolean blocked = false;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Vec3 offset = rightVector.scale(dx).add(upVector.scale(dy));
                    BlockPos pos = BlockPos.containing(sliceCenter.add(offset));
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir()) {
                        continue;
                    }

                    if (isPlayer) {
                        Player player = (Player) entity;

                        if (player.isSpectator()) {
                            blocked = true;
                            continue;
                        }

                        if (!level.mayInteract(player, pos)) {
                            blocked = true;
                            continue;
                        }
                    }

                    if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.STONE)) {
                        level.destroyBlock(pos, false, entity);
                    } else {
                        blocked = true;
                    }
                }
            }

            if (blocked) {
                break;
            }
        }

        // Push the entity in its look direction

        if (!isPlayer) {
            Vec3 pushVec = lookVector.scale(pushStrength);
            entity.setDeltaMovement(entity.getDeltaMovement().add(pushVec));
            entity.hurtMarked = true;
        }
        entity.fallDistance=0;
    }
}