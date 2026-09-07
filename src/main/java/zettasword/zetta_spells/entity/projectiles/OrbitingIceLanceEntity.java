package zettasword.zetta_spells.entity.projectiles;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.spells.ZSSpells;

import javax.annotation.Nonnull;

public class OrbitingIceLanceEntity extends MagicArrowEntity {

    // 1. Define the Data Accessors
    private static final EntityDataAccessor<Boolean> DATA_ORBITING_ID = SynchedEntityData.defineId(OrbitingIceLanceEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_ORBIT_OFFSET_ID = SynchedEntityData.defineId(OrbitingIceLanceEntity.class, EntityDataSerializers.FLOAT);

    public OrbitingIceLanceEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public OrbitingIceLanceEntity(Level world) {
        super(ZSEntities.ORBITING_ICE_LANCE.get(), world);
    }

    // 2. Register the accessors
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ORBITING_ID, false);
        this.entityData.define(DATA_ORBIT_OFFSET_ID, 0.0f);
    }

    // --- Orbiting Logic ---

    public void setOrbiting(boolean orbiting) {
        this.entityData.set(DATA_ORBITING_ID, orbiting);
        if (orbiting) {
            this.setNoGravity(true);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    public boolean isOrbiting() {
        return this.entityData.get(DATA_ORBITING_ID);
    }

    public void setOrbitOffset(float offset) {
        this.entityData.set(DATA_ORBIT_OFFSET_ID, offset);
    }

    public float getOrbitOffset() {
        return this.entityData.get(DATA_ORBIT_OFFSET_ID);
    }

    /// Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
    /// aims it in the direction they are looking with the given speed.
    @Override
    public void aim(LivingEntity caster, float speed) {
        if (getOwner() == null) this.setOwner(caster);



        this.setYRot(caster.getYRot() % 360.0F);
        this.setXRot(Mth.clamp(caster.getXRot(), -90.0F, 90.0F) % 360.0F);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        this.xo -= Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * 0.16F;
        this.yo -= 0.10000000149011612D;
        this.zo -= Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * 0.16F;

        this.setPos(xo, yo, zo);

        double motionX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI)
                * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI);
        double motionY = -Mth.sin(this.getXRot() / 180.0F * (float) Math.PI);
        double motionZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI)
                * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI);

        this.shoot(motionX, motionY, motionZ, speed * 1.5F, 1.0F);
    }

    @Override
    public int getLifetime() {
        // Return -1 (infinite) while orbiting so they don't despawn before the player fires them
        return 300;
    }

    @Override
    public boolean isValidTarget(@Nonnull Entity entity) {
        // Prevent the orbiting lances from dealing damage to the player or allies
        if (this.isOrbiting()) return false;
        return super.isValidTarget(entity);
    }

    @Nonnull
    @Override
    public Vec3 getDeltaMovement() {
        if (this.isOrbiting()) {
            return Vec3.ZERO;
        }
        return super.getDeltaMovement();
    }

    @Override
    public void tick() {
        // Smoothly track the owner while in orbit mode
        if (this.isOrbiting() && !this.isRemoved() && this.getOwner() instanceof LivingEntity owner) {
            // Configuration for the orbit
            double radius = 1.2; // Slightly wider so it's clearly visible around the player
            double targetY = owner.getEyeY() + 1.25; // Moved UP: 0.5 blocks higher than the player's eye level

            // Calculate target position based on owner's head rotation
            float angle = (float) Math.toRadians(owner.yHeadRot) + this.getOrbitOffset();
            double targetX = owner.getX() - Math.sin(angle) * radius;
            double targetZ = owner.getZ() + Math.cos(angle) * radius;

            // 3. Smoothly interpolate position (0.2F = moves 20% of the distance per tick, creating a smooth follow)
            float lerpFactor = 0.2F;

            double smoothX = Mth.lerp(lerpFactor, (float) this.getX(), (float) targetX);
            double smoothY = Mth.lerp(lerpFactor, (float) this.getY(), (float) targetY);
            double smoothZ = Mth.lerp(lerpFactor, (float) this.getZ(), (float) targetZ);

            this.setPos(smoothX, smoothY, smoothZ);

            // 4. Smoothly interpolate rotation (Mth.rotLerp correctly handles the -180 to 180 degree wrap-around)
            float smoothYRot = Mth.rotLerp(lerpFactor, this.getYRot(), owner.yHeadRotO);
            float smoothXRot = Mth.rotLerp(lerpFactor, this.getXRot(), owner.getXRot());

            //this.absMoveTo(smoothX, smoothY, smoothZ, smoothXRot, smoothYRot);

            //this.setYRot(smoothYRot);
            //this.setYHeadRot(smoothYRot);
            //this.setYBodyRot(smoothYRot);
            //this.setXRot(smoothXRot);
            //this.setYRot(smoothYRot);
            //this.setYHeadRot(smoothYRot);
            //this.setXRot(smoothXRot);

            // 
            //double bob = Math.sin(this.tickCount * 0.15) * 0.08;
            //this.setPos(smoothX, smoothY, smoothZ);
        }

        super.tick();
    }

    // --- Data Saving (Persistence across chunk reloads) ---

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Orbiting", this.isOrbiting());
        compound.putFloat("OrbitOffset", this.getOrbitOffset());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrbiting(compound.getBoolean("Orbiting"));
        this.setOrbitOffset(compound.getFloat("OrbitOffset"));
    }

    // --- Original Logic ---

    @Override
    public void onHitTargetExtraEffects(@Nonnull EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity livingEntity && !level().isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), ZSSpells.ICE_LANCES.get().property(DefaultProperties.EFFECT_DURATION), 1));
        }
    }

    @Override
    protected void onHitBlock(@Nonnull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide()) return;
        for (int j = 0; j < 10; j++) {
            ParticleBuilder.create(EBParticles.ICE, level().getRandom(), this.getX(), this.getY(), this.getZ(), 0.5, true)
                    .time(20 + random.nextInt(10)).gravity(true).spawn(this.level());
        }
    }

    @Override
    public @Nonnull SoundEvent getSoundEvent(HitResult result) {
        return result instanceof BlockHitResult ? EBSounds.ENTITY_ICE_LANCE_SMASH.get() : EBSounds.ENTITY_ICE_LANCE_HIT.get();
    }

    @Override
    public double getDamage(@Nonnull EntityHitResult hitResult) {
        return ZSSpells.ICE_LANCES.get().property(DefaultProperties.DAMAGE);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@Nonnull EntityHitResult hitResult) {
        return EBDamageSources.FROST;
    }
}