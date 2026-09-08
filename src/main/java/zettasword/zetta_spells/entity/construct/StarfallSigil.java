package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.entity.custom.FallingStarEntity;

import java.util.List;

public class StarfallSigil extends ZSSigil {
    public StarfallSigil(EntityType<?> type, Level world) {
        super(type, world);
    }

    public StarfallSigil(Level world) {
        super(ZSEntities.STARFALL_SIGIL.get(), world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && this.tickCount % 2 == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            FallingStarEntity star = new FallingStarEntity(level());
            star.setPos(this.getX() + radius * Mth.cos(angle), this.getY() - 0.2, this.getZ() + radius * Mth.sin(angle));
            star.setOwner(this.getCaster());
            level().addFreshEntity(star);
        }

        if (!level().isClientSide() && this.tickCount % 10 == 0) {
            AABB box = this.getBoundingBox().inflate(0, -15, 0);
            List<Entity> entities = level().getEntities(this, box, entity -> entity != getCaster() && entity.isAlive() && entity instanceof LivingEntity);
            entities.forEach(target -> {
                Vec3 originalVec = target.getDeltaMovement();
                target.hurt(MagicDamageSource.causeIndirectMagicDamage(this, this.getCaster(), EBDamageSources.SORCERY), 4);
                target.setDeltaMovement(originalVec);
                level().explode(this, target.position().x,target.position().y,target.position().z, 0.0F, Level.ExplosionInteraction.NONE);
            });
        }
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
