package zettasword.zetta_spells.capability.spellcaster;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ISpellcasterData {
    void sync();
    List<Spell> getSpells();
    void setSpells(List<Spell> spells);

    Spell getCurrentSpell();
    void setCurrentSpell(Spell spell);

    int getSpellCooldown();
    void setSpellCooldown(int ticks);

    boolean isCasting();
    void setCasting(boolean casting, int duration);

    SpellModifiers getModifiers();

    void tick(LivingEntity holder);

}
