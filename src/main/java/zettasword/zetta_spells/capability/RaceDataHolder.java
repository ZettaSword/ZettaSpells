package zettasword.zetta_spells.capability;

import com.binaris.wizardry.api.content.spell.NoneSpell;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zettasword.zetta_spells.ZettaSpellsMod;
import zettasword.zetta_spells.network.PacketHandler;
import zettasword.zetta_spells.network.RaceCapabilitySyncPacketS2C;

import java.util.HashMap;
import java.util.Map;


public class RaceDataHolder implements INBTSerializable<CompoundTag>, IRaceData {
    public static final ResourceLocation LOCATION = ZettaSpellsMod.location("race_data");
    public static final Capability<RaceDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });
    private String race = "human";
    private int ability_cooldown = 0;
    private final Player provider;
    private Map<Spell, Integer> spellsKnowledge = new HashMap<>();
    private int transformation_to_fish = 3600;

    public RaceDataHolder(Player player) {
        this.provider = player;
    }

    @Override
    public void sync() {
        if (!this.provider.level().isClientSide()) {
            CompoundTag tag = this.serializeNBT();

            RaceCapabilitySyncPacketS2C packet = new RaceCapabilitySyncPacketS2C(tag);
            PacketHandler.INSTANCE.sendTo(packet, ((ServerPlayer)this.provider).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            //Services.NETWORK_HELPER.sendTo((ServerPlayer) this.provider, packet);
        }
    }

    @Override
    public int getSpellKnowledge(Spell spell){
        return this.spellsKnowledge.getOrDefault(spell, 0);
    }

    @Override
    public void addSpellKnowledge(Spell spell, int add_knowledge) {
        if (spell instanceof NoneSpell) return;
        if (spellsKnowledge.containsKey(spell)){
            int knowledge = spellsKnowledge.getOrDefault(spell, 0);
            spellsKnowledge.put(spell, Math.max(knowledge + add_knowledge, 0));
            sync();
        }else{
            spellsKnowledge.put(spell,  Math.max(add_knowledge, 0));
            sync();
        }

    }

    @Override
    public void setRace(String race) {
        this.race=race;
        sync();
    }

    @Override
    public String getRace() {
        return race;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("race", this.race);
        tag.putInt("ability_cooldown", this.ability_cooldown);
        tag.putInt("transformation_to_fish", this.transformation_to_fish);

        // Serialize spellsKnowledge map
        CompoundTag spellsTag = new CompoundTag();
        for (Map.Entry<Spell, Integer> entry : spellsKnowledge.entrySet()) {
            // Store as "modid:path" -> integer
            spellsTag.putInt(entry.getKey().getLocation().toString(), entry.getValue());
        }
        tag.put("spells_knowledge", spellsTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("race")){
            this.race = tag.getString("race");
        }else{
            this.race = "human";
        }
        if (tag.contains("ability_cooldown")) this.ability_cooldown = tag.getInt("ability_cooldown");
        if (tag.contains("transformation_to_fish")) this.transformation_to_fish = tag.getInt("transformation_to_fish");

        // Deserialize spellsKnowledge map
        if (tag.contains("spells_knowledge", Tag.TAG_COMPOUND)) {
            spellsKnowledge.clear();
            CompoundTag spellsTag = tag.getCompound("spells_knowledge");
            for (String key : spellsTag.getAllKeys()) {
                ResourceLocation spellId = ResourceLocation.tryParse(key);
                if (spellId != null && spellsTag.contains(key, Tag.TAG_INT)) {
                    spellsKnowledge.put(Services.REGISTRY_UTIL.getSpell(spellId), spellsTag.getInt(key));
                }
            }
        }
    }

    public void copyFrom(@NotNull RaceDataHolder old) {
        this.race = old.race;
        this.ability_cooldown = old.ability_cooldown;
        this.transformation_to_fish = old.transformation_to_fish;
        this.spellsKnowledge = old.spellsKnowledge;
        //this.spellsDiscovered.clear();
        //this.spellsDiscovered.addAll(old.spellsDiscovered);

        //this.spellData.clear();
        //this.spellData.putAll(old.spellData);
    }

    @Override
    public int getAbilityCooldown() {
        return ability_cooldown;
    }

    @Override
    public int getTransformationToFish() {
        return transformation_to_fish;
    }

    @Override
    public void setAbilityCooldown(int ability_cooldown) {
        this.ability_cooldown = ability_cooldown;
        sync();
    }

    @Override
    public void reduceAbilityCooldown(int amount){
        this.ability_cooldown=Math.max(0, this.ability_cooldown - amount);
        sync();
    }

    @Override
    public void setTransformationToFish(int transformation_to_fish) {
        this.transformation_to_fish = transformation_to_fish;
        sync();
    }

    @Override
    public void reduceTransformationToFish(int amount){
        this.transformation_to_fish=Math.max(0, this.transformation_to_fish - amount);
        sync();
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<RaceDataHolder> dataHolder;

        public Provider(Player player) {
            this.dataHolder = LazyOptional.of(() -> new RaceDataHolder(player));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return RaceDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
