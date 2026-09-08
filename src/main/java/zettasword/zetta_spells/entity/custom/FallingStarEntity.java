package zettasword.zetta_spells.entity.custom;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import zettasword.zetta_spells.entity.ZSEntities;

import javax.annotation.Nonnull;

public class FallingStarEntity extends MagicArrowEntity {
    public FallingStarEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public FallingStarEntity(Level world){
        super(ZSEntities.FALLING_STAR.get(), world);
    }

    /// Returns the damage dealt by this arrow. Keep in mind that the damage multiplier is applied after this value.
    ///
    /// @return The base damage dealt by this arrow.
    @Override
    public double getDamage(@Nonnull EntityHitResult hitResult) {
        return 4;
    }

    /// Returns the maximum flight time in ticks before this projectile disappears, or -1 if it can continue indefinitely
    /// until it hits something. This should be constant.
    ///
    /// @return The maximum flight time in ticks.
    @Override
    public int getLifetime() {
        return 120;
    }

    /// This method is used to get the damage type of the magic arrow. You must override this and return a valid
    /// `ResourceKey<DamageType>` for your magic arrow's damage type.
    ///
    /// @return The damage type of the magic arrow.
    @Override
    public ResourceKey<DamageType> getDamageType(@Nonnull EntityHitResult hitResult) {
        return EBDamageSources.SORCERY;
    }

    /// Ticks when the arrow is in the air
    @Override
    public void ticksInAir() {
        super.ticksInAir();
        if (this.level().isClientSide){
            if (this.tickCount % 5 == 0){
                ParticleBuilder.create(EBParticles.SPARKLE).velocity(0, -0.2, 0).time(20).pos(this.position()).gravity(false).spawn(level());
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D, 0.0D));
    }

    /// Ticks when the arrow is in the ground
    @Override
    public void tickInGround() {
        super.tickInGround();
        this.discard();
    }

    /// **This isn't for applying damage to the entity**
    ///
    /// Here you can apply any extra effects to the entity that was hit by the projectile. (e.g. setting fire, poisoning, etc.)
    ///
    /// @param hitResult The result of the hit.
    @Override
    public void onHitTargetExtraEffects(@Nonnull EntityHitResult hitResult) {
        super.onHitTargetExtraEffects(hitResult);
        if (!level().isClientSide()){
            hitResult.getEntity().playSound(SoundEvents.FIREWORK_ROCKET_BLAST);
        }
        if (level().isClientSide()){
            ParticleBuilder.create(EBParticles.FLASH).scale(1.0F).time(10).pos(hitResult.getEntity().position()).gravity(false).spawn(level());
        }
    }
}
