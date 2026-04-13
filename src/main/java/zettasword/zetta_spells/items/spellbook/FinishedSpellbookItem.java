package zettasword.zetta_spells.items.spellbook;

import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.api.content.util.WorkbenchUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.system.Spellcasting;
import zettasword.zetta_spells.system.TextProcessingUtil;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SpellCreator;

import java.util.List;

public class FinishedSpellbookItem extends Item implements IManaStoringItem, IWorkbenchItem {
    public FinishedSpellbookItem(Properties properties) {
        super(properties.durability(1000));
    }

    // Display stored text in tooltip
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains("written_text")) {
            if (Screen.hasShiftDown()) {
                String text = stack.getTag().getString("written_text");
                tooltip.add(Component.literal("\"" + text + "\"").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GOLD));
            }else{
                tooltip.add(Component.translatable("spellbook.shift_for_details").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
            }

            if (stack.getTag().contains("author")){
                tooltip.add(Component.translatable("spellbook.byAuthor", stack.getTag().getString("author")).withStyle(ChatFormatting.GRAY));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (stack.hasTag() && stack.getTag().contains("written_text")) {
            stack.getTag().putString("author", player.getName().getString());
            String text = stack.getTag().getString("written_text");
            int cost = TextProcessingUtil.extractWords(text).size();
            if (cost == 0) cost = 10;
            cost = Math.max(10, (cost * cost)/5);
            if (this.getMana(stack) >= cost || player.isCreative()){
                //Spellcasting.spellCast(level, player, hand, text);
                SpellCreator.spellCast(new SpellCreateContext(level, player, hand), text);
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal(stack.getHoverName().getString()), true);
                }
            }else{
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("item.zetta_spells.finished_spellbook_fail"), true);
                }
                return InteractionResultHolder.fail(stack);
            }

            //this.consumeMana(stack, cost, player);
            return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
        }
        
        return super.use(level, player, hand);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return DrawingUtils.mix(16747518, 9318116, (float)stack.getDamageValue());
    }

    public int getMana(ItemStack stack) {
        return this.getManaCapacity(stack) - stack.getDamageValue();
    }

    public int getManaCapacity(ItemStack stack) {
        return stack.getMaxDamage();
    }

    public void setMana(ItemStack stack, int mana) {
        stack.setDamageValue(this.getManaCapacity(stack) - mana);
    }

    @Override
    public int getSpellSlotCount(ItemStack itemStack) {
        return 0;
    }

    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;
        if (upgrade.hasItem()) {
            changed = this.applyUpgradeSlot(player, centre, upgrade);
        }

        changed |= WorkbenchUtils.rechargeManaFromCrystals(centre, crystals);
        return changed;
    }

    protected boolean applyUpgradeSlot(Player player, Slot centre, Slot upgrade) {
        ItemStack original = centre.getItem().copy();
        centre.set(this.applyUpgrade(player, centre.getItem(), upgrade.getItem()));
        return !ItemStack.isSameItem(centre.getItem(), original);
    }

    @Override
    public boolean showTooltip(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}