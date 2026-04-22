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
import com.binaris.wizardry.setup.registries.Spells;
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

import javax.annotation.Nullable;
import java.util.List;

public class CustomPlayerSpell extends Spell {

    public CustomPlayerSpell(){
        requiresPacket();
    }



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
        int index = CastItemDataHelper.getCurrentSpellIndex(stack);
        String spell = getCustomSpell(stack, index);
        if (spell != null){
            String[] texts = spell.split(";");
            for (String fragment : texts) {
                if (fragment.isEmpty()) continue;
                SpellCreator.spellCast(new SpellCreateContext(ctx.world(), caster, hand), fragment);
            }
            return true;
        }
        return false;
    }

    private List<String> getCustomSpells(Player caster, InteractionHand hand) {
        List<String> spellwords = Lists.newArrayList();
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
                }

                return spellwords;
            }
        }
        return null;
    }

    @Nullable
    public static String getCustomSpell(ItemStack stack, int index){
        if (stack.hasTag()){
            CompoundTag tag = stack.getTag();
            String key = "custom_spells_" + index;
            if (tag == null) return null;
            if (tag.contains(key)){
                return tag.getString(key);
            }
        }
        return null;
    }

    public static void setCustomSpell(ItemStack stack, int index, String spell){
        CompoundTag tag = stack.getOrCreateTag();
        String key = "custom_spells_" + index;
        tag.putString(key, spell);
    }

    @Nullable
    public static String getCustomSpellName(ItemStack stack, int index){
        if (stack.hasTag()){
            CompoundTag tag = stack.getTag();
            String key = "custom_spells_name_" + index;
            if (tag == null) return null;
            if (tag.contains(key)){
                return tag.getString(key);
            }
        }
        return null;
    }

    public static void setCustomSpellName(ItemStack stack, int index, String spellName){
        CompoundTag tag = stack.getOrCreateTag();
        String key = "custom_spells_name_" + index;
        tag.putString(key, spellName);
    }

    public static int getAdjacentSpellIndex(ItemStack stack, int offset) {
        List<Spell> spells = CastItemDataHelper.getSpells(stack);
        if (spells.isEmpty()) return -999;
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return -999;
        int currentIndex = stack.getTag().getInt(CastItemDataHelper.SELECTED_SPELL_KEY);

        // Bounds check
        if (currentIndex < 0 || currentIndex >= spells.size()) {
            currentIndex = 0;
        }

        return (currentIndex + offset + spells.size()) % spells.size();
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
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.MAGIC, SpellType.UTILITY, SpellAction.POINT_DOWN, 20, 0, 5)
                .add(DefaultProperties.RANGE, 14F)
                .build();
    }
}
