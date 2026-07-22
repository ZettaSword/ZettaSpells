package zettasword.zetta_spells.entity.custom;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;

import javax.annotation.Nonnull;

public class CustomArrow extends MagicArrowEntity {
    private static final EntityDataAccessor<Integer> DATA_LIFETIME =
            SynchedEntityData.defineId(CustomArrow.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_MAX_MANA =
            SynchedEntityData.defineId(CustomArrow.class, EntityDataSerializers.INT);

    public CustomArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @Nonnull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    /**
     * Subclasses must override this to set their own base damage.
     */
    @Override
    public double getDamage() {
        return 0;
    }

    /**
     * Returns the maximum flight time in ticks before this projectile disappears, or -1 if it can continue indefinitely
     * until it hits something. This should be constant.
     */
    @Override
    public int getLifetime() {
        return this.entityData.get(DATA_LIFETIME);
    }

    public void setLifetime(int lifetime) {
        this.entityData.set(DATA_LIFETIME, lifetime);
    }

    public int getMaxMana() {
        return this.entityData.get(DATA_MAX_MANA);
    }

    public void setMaxMana(int maxMana) {
        this.entityData.set(DATA_MAX_MANA, maxMana);
    }

    /**
     * This method is used to get the texture for the magic arrow.
     * The texture is represented by a ResourceLocation object. You must return a valid ResourceLocation or implement
     * a different renderer of your own accordingly.
     */
    @Override
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath(WizardryMainMod.MOD_ID, "textures/entity/magic_missile.png");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LIFETIME, 12);
        this.entityData.define(DATA_MAX_MANA, 0);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;
        SpellCreateContext context = new SpellCreateContext(level(), target);
        context.setMaxMana(this.getMaxMana());
        super.onHitEntity(hitResult);
    }
}
