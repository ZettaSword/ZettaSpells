package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.ZSEntities;

public class CosmeticSigil extends ScaledConstructEntity {
    private ResourceLocation location = ZettaSpells.location("textures/sigils/old/circle_arcane.png");
    private static final EntityDataAccessor<Integer> DATA_LIFETIME =
            SynchedEntityData.defineId(CosmeticSigil.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> DATA_LOCATION =
            SynchedEntityData.defineId(CosmeticSigil.class, EntityDataSerializers.STRING);

    public CosmeticSigil(EntityType<?> type, Level world) {
        super(type, world);
    }

    public CosmeticSigil(Level world) {
        super(ZSEntities.COSMETIC_SIGIL.get(), world);
    }

    public ResourceLocation getLocation(){return ResourceLocation.parse(this.entityData.get(DATA_LOCATION));}
    public void setLocation(ResourceLocation location){
        this.location = location;
        this.entityData.set(DATA_LOCATION, location.toString());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putString("loc_namespace", this.location.getNamespace());
        tag.putString("loc_path", this.location.getPath());
        return tag;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LIFETIME, 600); // Default value
        this.entityData.define(DATA_LOCATION,  ZettaSpells.location("textures/sigils/old/circle_arcane.png").toString()); // Default value
    }

    public void setLifetime(int lifetime) {
        this.entityData.set(DATA_LIFETIME, lifetime);
        this.lifetime=lifetime;
    }

    public int getLifetime() {
        return this.entityData.get(DATA_LIFETIME);
    }


    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        this.location = ResourceLocation.fromNamespaceAndPath(nbt.getString("loc_namespace"), nbt.getString("loc_path"));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setLocation(ResourceLocation.fromNamespaceAndPath(tag.getString("loc_namespace"), tag.getString("loc_path")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("loc_namespace", this.location.getNamespace());
        tag.putString("loc_path", this.location.getPath());
    }
}
