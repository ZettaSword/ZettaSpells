package zettasword.zetta_spells.items;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.content.entity.living.Remnant;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class LanternOfKnowledge extends Item implements IManaItem {
    public LanternOfKnowledge() {
        super(new Properties().stacksTo(1).durability(5).rarity(Rarity.EPIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getMana(stack) == getManaCapacity(stack);
    }

    @Override
    public void onCraftedBy(ItemStack p_41447_, Level p_41448_, Player p_41449_) {
        super.onCraftedBy(p_41447_, p_41448_, p_41449_);
        this.setMana(p_41447_, 0);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        p_41423_.add(Component.translatable("item.zetta_spells.lantern_of_knowledge.desc").withStyle(ChatFormatting.DARK_PURPLE));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) stack.getDamageValue());
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof Remnant) {
            if (getMana(stack) < getManaCapacity(stack)) {
                // Increment charge
                setMana(stack, getMana(stack) + 1);

                target.remove(Entity.RemovalReason.DISCARDED);

                if (!player.level().isClientSide()) {
                    if (getMana(stack) != 5) {
                        player.displayClientMessage(
                                Component.translatable("item.zetta_spells.lantern_of_knowledge.charge_up", getMana(stack)).withStyle(ChatFormatting.DARK_PURPLE), true);
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }else{
                        player.displayClientMessage(Component.translatable("item.zetta_spells.lantern_of_knowledge.charged").withStyle(ChatFormatting.DARK_PURPLE), true);
                    }
                }
                return InteractionResult.sidedSuccess(player.level().isClientSide());
            } else {
                if (!player.level().isClientSide()) {
                    player.displayClientMessage(Component.translatable("item.zetta_spells.lantern_of_knowledge.charged").withStyle(ChatFormatting.DARK_PURPLE), true);
                }
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        InteractionHand offHand = pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack offStack = pPlayer.getItemInHand(offHand);

        if (getMana(stack) == getManaCapacity(stack) && offStack.getItem() instanceof SpellWordItem) {
            if (!pLevel.isClientSide) {
                CompoundTag tag = offStack.getOrCreateTag();

                if (!tag.getBoolean(SpellWordItem.DISCOVERED_TAG)) {
                    // Set the required boolean tag
                    tag.putBoolean(SpellWordItem.DISCOVERED_TAG, true);
                    setMana(stack, 0);

                    // Visual/Audio feedback
                    pPlayer.displayClientMessage(Component.translatable("item.zetta_spells.lantern_of_knowledge.discovered").withStyle(ChatFormatting.DARK_PURPLE), true);
                    pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void rechargeMana(ItemStack stack, int mana) {
    }

    @Override
    public void consumeMana(ItemStack stack, int mana, LivingEntity wielder) {
    }

    @Override
    public int getMana(ItemStack stack) {
        return getManaCapacity(stack) - stack.getDamageValue();
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return stack.getMaxDamage();
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        stack.setDamageValue(getManaCapacity(stack) - mana);
    }
}
