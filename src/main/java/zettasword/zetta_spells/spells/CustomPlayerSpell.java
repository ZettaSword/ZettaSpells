package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomPlayerSpell extends Spell {

    public CustomPlayerSpell(){}

    /**
     * This cast method is meant to be used for spells that are cast by a player source. This is useful for spells that
     * are meant to be cast by players, as it provides more information about the caster and the context of the cast.
     * <p>
     * Override this method to implement the casting behavior for spells that are meant to be cast by players.
     *
     * @param ctx The context of the spell cast, containing information about the world, caster, hand used, modifiers, etc.
     * @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
     * considered as having been cast, so no cooldown will be applied.
     */
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        InteractionHand hand = ctx.hand();
        ItemStack stack = caster.getItemInHand(hand);
        Map<String, String> spells = getCustomSpells(caster, hand);
        int index = getIndex(stack);
        if (spells != null && index <= spells.size()){
            int count = 0;
            for (Map.Entry<String, String> entry : spells.entrySet()) {
                String name = entry.getKey();
                String word = entry.getValue();
            }
        }
        return false;
    }

    private static int getIndex(ItemStack wand) {
        List<Spell> spells = CastItemDataHelper.getSpells(wand);
        if (spells.isEmpty()) return 0;

        int currentIndex = wand.getOrCreateTag().getInt(CastItemDataHelper.SELECTED_SPELL_KEY);

        // Bounds check
        if (currentIndex < 0 || currentIndex >= spells.size()) {
            currentIndex = 0;
        }

        return currentIndex;
    }

    private Map<String, String> getCustomSpells(Player caster, InteractionHand hand) {
        List<String> spellwords = Lists.newArrayList();
        List<String> spellwords_names = Lists.newArrayList();
        ItemStack stack = caster.getItemInHand(hand);
        if (stack.hasTag()){
            if (CastItemDataHelper.getCurrentSpell(stack) == this){
                CompoundTag tag = stack.getTag();
                if (tag == null) return null;
                if (tag.contains("custom_spells")){
                    ListTag list = tag.getList("custom_spells", Tag.TAG_STRING);
                    for (Tag element : list) {
                        if (element instanceof StringTag stringTag) {
                            spellwords.add(stringTag.getAsString());
                        }
                    }
                    tag.getCompound("custom_spells");
                }
                if (tag.contains("custom_spells_name")){
                    ListTag list = tag.getList("custom_spells_name", Tag.TAG_STRING);
                    for (Tag element : list) {
                        if (element instanceof StringTag stringTag) {
                            spellwords_names.add(stringTag.getAsString());
                        }
                    }
                    tag.getCompound("custom_spells_name");
                }
                Map<String, String> customSpells= new java.util.HashMap<>(Map.of());

                for (int i = 0; i < spellwords_names.size() && i < spellwords.size(); i++){
                    customSpells.put(spellwords_names.get(i), spellwords.get(i));
                }

                return customSpells;
            }
        }
        return null;
    }

    /**
     * This method is where you should set the default properties for your spell when creating a new spell class. This
     * method is called in the constructor of the Spell class, and the properties returned by this method are assigned
     * to the spell's properties field.
     *
     * @return A SpellProperties object with the default properties for your spell.
     */
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.MAGIC, SpellType.UTILITY, SpellAction.POINT_DOWN, 20, 0, 40)
                .add(DefaultProperties.RANGE, 14F)
                .build();
    }
}
