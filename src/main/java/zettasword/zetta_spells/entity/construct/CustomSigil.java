package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;

import java.util.List;

public class CustomSigil extends ScaledConstructEntity {
    private ResourceLocation location = ZettaSpells.location("textures/sigils/old/circle_arcane.png");
    private static final EntityDataAccessor<Integer> DATA_LIFETIME =
            SynchedEntityData.defineId(CustomSigil.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> DATA_LOCATION =
            SynchedEntityData.defineId(CustomSigil.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> DATA_SPELL =
            SynchedEntityData.defineId(CustomSigil.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> DATA_ONE_TIME =
            SynchedEntityData.defineId(CustomSigil.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_COOLDOWN =
            SynchedEntityData.defineId(CustomSigil.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> LAST_MANA =
            SynchedEntityData.defineId(CustomSigil.class, EntityDataSerializers.INT);

    public CustomSigil(EntityType<?> type, Level world) {
        super(type, world);
    }

    public CustomSigil(Level world) {
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
        this.entityData.define(DATA_SPELL, "");
        this.entityData.define(DATA_ONE_TIME, true);
        this.entityData.define(DATA_COOLDOWN, 0);
        this.entityData.define(LAST_MANA, 0);
    }

    public void setSpell(String spell){
        this.entityData.set(DATA_SPELL, spell);
    }

    public String getSpell(){
        return this.entityData.get(DATA_SPELL);
    }

    public boolean isOneTime(){
        return this.entityData.get(DATA_ONE_TIME);
    }

    public void setOneTime(boolean once){
        this.entityData.set(DATA_ONE_TIME, once);
    }

    public int getCooldown(){
        return this.entityData.get(DATA_COOLDOWN);
    }

    public void setCooldown(int cooldown){
        this.entityData.set(DATA_COOLDOWN, cooldown);
    }

    public void setLastMana(int value){
        this.entityData.set(LAST_MANA, value);
    }

    public int getLastMana(){
        return this.entityData.get(LAST_MANA);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 != 0 || this.level().isClientSide) return;
        int cooldown = getCooldown();
        if (cooldown > 0) this.setCooldown(cooldown - 1);

        if (cooldown == 0) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, this.getX(), this.getY(), this.getZ(), this.level());
            for (LivingEntity target : targets) {
                SpellCreateContext context = new SpellCreateContext(level(), this.getCaster(), target);
                // Important thing here
                context.externalCast();
                context.setLastExternalMana(this.getLastMana());
                SpellCreator.spellCast(context, this.getSpell());
            }
            this.setCooldown(40);
            if (isOneTime()){
                this.setLifetime(10);
            }
        }
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
