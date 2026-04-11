package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.spells.TurnMinion;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This entity will hold all information about the
 * **/
public class DeathVesselEntity extends MagicChains {
    // NBT Key
    private static final String TAG_STORED_DATA = "StoredEntityData";

    public DeathVesselEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public DeathVesselEntity(Level world) {
        super(ZSEntities.MAGIC_CHAINS.get(), world);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains(TAG_STORED_DATA, 10)) {
            this.getPersistentData().put(TAG_STORED_DATA, pCompound.getCompound(TAG_STORED_DATA));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getPersistentData().contains(TAG_STORED_DATA, 10)) {
            pCompound.put(TAG_STORED_DATA, this.getPersistentData().getCompound(TAG_STORED_DATA));
        }
    }

    @Nullable
    public Entity resurrectEntity(LivingEntity caster) {
        CompoundTag data = this.getPersistentData().getCompound(TAG_STORED_DATA);
        String typeId = data.getString("id");
        Optional<EntityType<?>> typeOpt = EntityType.byString(typeId);

        if (typeOpt.isPresent()) {
            EntityType<?> type = typeOpt.get();
            Entity newEntity = type.create(this.level());

            if (newEntity instanceof Mob livingNew) {
                newEntity.load(data);
                newEntity.setPos(this.position());
                livingNew.setHealth(livingNew.getMaxHealth());
                livingNew.hurtTime = 0;
                livingNew.deathTime = 0;
                SpellModifiers mods = new SpellModifiers();
                if (caster instanceof Player player) {
                    WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);
                    if (wizardData != null && wizardData.getSpellModifiers() != null) mods = wizardData.getSpellModifiers();
                }
                livingNew.setCustomName(null);
                TurnMinion.turnMinion(caster, mods, livingNew);
                return newEntity;
            }
            if (newEntity instanceof LivingEntity living){
                newEntity.load(data);
                newEntity.setPos(this.position());
                living.setHealth(living.getMaxHealth());
                living.hurtTime = 0;
                living.deathTime = 0;
                return living;
            }
            if (newEntity != null)
                newEntity.load(data);
            return newEntity;
        }
        return null;
    }

    public void storeEntityData(LivingEntity entity) {
        CompoundTag entityData = new CompoundTag();
        entity.save(entityData);
        entityData.remove("UUID");
        this.getPersistentData().put(TAG_STORED_DATA, entityData);
    }

}
