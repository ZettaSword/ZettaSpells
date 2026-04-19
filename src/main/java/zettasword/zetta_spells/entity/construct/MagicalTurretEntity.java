package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.content.entity.projectile.MagicMissileEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.spells.ZettaSpells;

import java.util.List;

public class MagicalTurretEntity extends MagicConstructEntity {

    public MagicalTurretEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.lifetime = ZettaSpells.MAGIC_TURRET.get().property(DefaultProperties.DURATION);
    }

    public MagicalTurretEntity(Level level){
        super(ZSEntities.MAGICAL_TURRET.get(), level);
        this.lifetime = ZettaSpells.MAGIC_TURRET.get().property(DefaultProperties.DURATION);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount % 40 == 0) {
            AABB aabb = getBoundingBox().inflate(8.0F);
            List<Entity> entityList = level().getEntities(this, aabb, entity -> entity != this && entity != getCaster() && !AllyDesignation.isAllied(getCaster(), entity) && entity.isAlive());
            if (entityList.isEmpty()) return;
            Entity target  = entityList.get(0);
            if (target instanceof Mob mob) {
                //mob.hurt(mob.damageSources().magic(), 4.0F);
                MagicMissileEntity arrow = new MagicMissileEntity(level());
                float g = 0.05f;
                aim(getCaster(), this, arrow, mob, 14 / Mth.sqrt(2 * this.getEyeHeight() / g), 0.1F);
                level().addFreshEntity(arrow);
            }
        }
    }

    public void aim(LivingEntity caster, Entity turret, MagicMissileEntity entity, Entity target, float speed, float aimingError) {
        if (entity.getOwner() == null) entity.setOwner(caster);

        this.yo = turret.yo + (double) turret.getDimensions(turret.getPose()).height * 0.85F ;
        double dx = target.xo - turret.xo;
        double dy = !entity.isNoGravity() ?
                target.yo + (double) (target.getDimensions(turret.getPose()).height / 3.0f) - this.yo
                : target.yo + (double) (target.getDimensions(turret.getPose()).height / 2.0f) - this.yo;
        double dz = target.zo - turret.zo;
        double horizontalDistance = Mth.sqrt((float) (dx * dx + dz * dz));

        if (horizontalDistance >= 1.0E-7D) {
            float yaw = (float) (Math.atan2(dz, dx) * 180.0d / Math.PI) - 90.0f;
            float pitch = (float) (-(Math.atan2(dy, horizontalDistance) * 180.0d / Math.PI));
            double dxNormalised = dx / horizontalDistance;
            double dzNormalised = dz / horizontalDistance;
            this.absMoveTo(turret.xo + dxNormalised, this.yo, turret.zo + dzNormalised, yaw, pitch);

            float bulletDropCompensation = !this.isNoGravity() ? (float) horizontalDistance * 0.2f : 0;
            entity.shoot(dx, dy + (double) bulletDropCompensation, dz, speed, aimingError);
        }
    }
}