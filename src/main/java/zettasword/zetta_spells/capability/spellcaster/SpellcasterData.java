package zettasword.zetta_spells.capability.spellcaster;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zettasword.zetta_spells.ZettaSpells;

import java.util.ArrayList;
import java.util.List;

public class SpellcasterData implements INBTSerializable<CompoundTag>, ISpellcasterData {
    public static final ResourceLocation LOCATION = ZettaSpells.location("spellcaster_data");
    public static final Capability<SpellcasterData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });
    private List<Spell> spells = new ArrayList<>(List.of(Spells.MAGIC_MISSILE));
    private Spell currentSpell = Spells.NONE;
    private final LivingEntity caster;
    private int spellCooldown = 0;
    private int castingDuration = 0;
    private final SpellModifiers modifiers = new SpellModifiers(); // Adjust constructor as needed

    public SpellcasterData(LivingEntity caster) {
        this.caster = caster;
    }

    @Override
    public void sync() {

    }

    @Override
    public List<Spell> getSpells() { return spells; }

    @Override
    public void setSpells(List<Spell> spells) { this.spells = spells; }

    @Override
    public Spell getCurrentSpell() { return currentSpell; }

    @Override
    public void setCurrentSpell(Spell spell) {
        this.currentSpell = spell != null ? spell : Spells.NONE;
    }

    @Override
    public int getSpellCooldown() { return spellCooldown; }

    @Override
    public void setSpellCooldown(int ticks) { this.spellCooldown = Math.max(0, ticks); }

    @Override
    public boolean isCasting() { return castingDuration > 0; }

    @Override
    public void setCasting(boolean casting, int duration) {
        if (casting) {
            this.castingDuration = duration;
        } else {
            this.castingDuration = 0;
        }
    }

    @Override
    public SpellModifiers getModifiers() { return modifiers; }

    @Override
    public void tick(LivingEntity holder) {
        if (spellCooldown > 0) spellCooldown--;
        if (castingDuration > 0) {
            castingDuration--;
            if (castingDuration == 0 && currentSpell != Spells.NONE) {
                // Finish casting
                LivingEntity target = holder.getLastHurtMob();
                if (holder instanceof Mob mob) target = mob.getTarget();
                var ctx = new com.binaris.wizardry.api.content.spell.internal.EntityCastContext(holder.level(), holder, net.minecraft.world.InteractionHand.MAIN_HAND,
                        20, target, modifiers);
                currentSpell.cast(ctx);
                setCurrentSpell(Spells.NONE);
                setSpellCooldown(40); // Brief cooldown after cast
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag spellList = new ListTag();
        for (Spell spell : spells) {
            if (spell != null) spellList.add(StringTag.valueOf(spell.getLocation().toString()));
        }
        tag.put("Spells", spellList);
        if (currentSpell != null) tag.putString("CurrentSpell", currentSpell.getLocation().toString());
        tag.putInt("Cooldown", spellCooldown);
        tag.putInt("CastingTime", castingDuration);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("Spells", Tag.TAG_LIST)) {
            ListTag spellList = tag.getList("Spells", Tag.TAG_STRING);
            spells.clear();
            for (int i = 0; i < spellList.size(); i++) {
                Spell spell = Services.REGISTRY_UTIL.getSpell(
                        ResourceLocation.tryParse(spellList.getString(i)));
                if (spell != null) spells.add(spell);
            }
        }
        if (tag.contains("CurrentSpell")) {
            Spell spell = Services.REGISTRY_UTIL.getSpell(
                    ResourceLocation.tryParse(tag.getString("CurrentSpell")));
            if (spell != null) currentSpell = spell;
        }
        spellCooldown = tag.getInt("Cooldown");
        castingDuration = tag.getInt("CastingTime");
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<SpellcasterData> dataHolder;

        public Provider(LivingEntity caster) {
            this.dataHolder = LazyOptional.of(() -> new SpellcasterData(caster));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return SpellcasterData.INSTANCE.orEmpty(capability, dataHolder.cast());
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
