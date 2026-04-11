package zettasword.zetta_spells.capability;

import com.binaris.wizardry.api.content.spell.Spell;

public interface IRaceData {

    /**
     * Synchronises this SpellManagerData with the client.
     */
    void sync();

    int getSpellKnowledge(Spell spell);

    void addSpellKnowledge(Spell spell, int add_knowledge);

    void setRace(String race);
    String getRace();

    int getAbilityCooldown();

    int getTransformationToFish();

    void setAbilityCooldown(int ability_cooldown);
    void reduceAbilityCooldown(int amount);

    void setTransformationToFish(int transformation_to_fish);

    void reduceTransformationToFish(int amount);
}


