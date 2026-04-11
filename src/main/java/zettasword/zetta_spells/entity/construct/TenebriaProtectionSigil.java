package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.item.WizardArmorItem;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Elements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.system.Alchemy;

import java.util.List;

public class TenebriaProtectionSigil extends ScaledConstructEntity {
    public TenebriaProtectionSigil(EntityType<?> type, Level world) {
        super(type, world);
    }

    public TenebriaProtectionSigil(Level world) {
        super(ZSEntities.TENEBRIA_PROTECTION_SIGIL.get(), world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            level().addParticle(ParticleTypes.ASH, this.getX() + radius
                    * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle), 0, 0, 0);
        }

        if (this.level().isClientSide) return;
        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, getX(), getY(), getZ(), level());

        for (LivingEntity target : targets) {
            if (!this.isValidTarget(target)) continue;
            // If target, no matter it is a monster or not - wears Necromancy armour in any way - Tenebria doesn't like to hurt them.
            boolean isTenebriaFavourite = false;
            for (ItemStack slot : target.getArmorSlots()){
                if (slot.getItem() instanceof WizardArmorItem wizardArmorItem && wizardArmorItem.getElement() == Elements.NECROMANCY)
                    isTenebriaFavourite = true;
            }
            if (isTenebriaFavourite) return;

            Vec3 originalVec = target.getDeltaMovement();
            Alchemy.apply(target, 15, 0, MobEffects.DARKNESS, EBMobEffects.DECAY.get());

            target.setDeltaMovement(originalVec);
        }

    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
