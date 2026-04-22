package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.ZSEntities;

public class CosmeticSigil extends ScaledConstructEntity {
    public ResourceLocation location = ZettaSpells.location("textures/sigils/old/circle_arcane.png");

    public CosmeticSigil(EntityType<?> type, Level world) {
        super(type, world);
    }

    public CosmeticSigil(Level world) {
        super(ZSEntities.COSMETIC_SIGIL.get(), world);
    }

    public ResourceLocation getLocation(){return this.location;}
    public void setLocation(ResourceLocation location){ this.location = location;}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putString("loc_namespace", this.location.getNamespace());
        tag.putString("loc_path", this.location.getPath());
        return tag;
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
