package zettasword.zetta_spells.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWords;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SpellWordItem extends Item {
    
    public static final String SPELLWORD_TAG = "SpellWord";
    
    public SpellWordItem() {
        super(new Properties().stacksTo(1));
    }
    
    /**
     * Sets the SpellWord for this ItemStack using its registry key
     */
    public static void setSpellWord(ItemStack stack, ResourceLocation spellWordId) {
        stack.getOrCreateTag().putString(SPELLWORD_TAG, spellWordId.toString());
    }
    
    /**
     * Gets the SpellWord ResourceLocation from the ItemStack's NBT
     */
    public static Optional<ResourceLocation> getSpellWordId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (!stack.hasTag() || !(tag != null && tag.contains(SPELLWORD_TAG))) {
            return Optional.empty();
        }
        String id = tag.getString(SPELLWORD_TAG);
        ResourceLocation loc = ResourceLocation.tryParse(id);
        return Optional.ofNullable(loc);
    }
    
    /**
     * Gets the actual SpellWord instance from registry
     */
    public static Optional<SpellWord> getSpellWord(ItemStack stack) {
        return getSpellWordId(stack).flatMap(SpellWords::getAction);
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return getSpellWord(stack)
            .map(spellWord -> spellWord.getDisplayName(stack)) // Let SpellWord provide its name
            .orElseGet(() -> Component.translatable(getDescriptionId(stack))
                .append(Component.literal(" (Unknown)").withStyle(ChatFormatting.GRAY)));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        getSpellWord(stack).ifPresentOrElse(
            spellWord -> spellWord.addTooltip(stack, level, tooltip, flag),
            () -> tooltip.add(Component.literal("Invalid or missing spellword")
                .withStyle(ChatFormatting.RED))
        );
        tooltip.add(Component.translatable("spellword.zetta_spells.general_item_desc").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltip, flag);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Optional: make item glow if it has a valid spellword
        return getSpellWord(stack).isPresent();
    }
}