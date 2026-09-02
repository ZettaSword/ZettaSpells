package zettasword.zetta_spells.system.mixins;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.client.SpellGUIDisplay;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zettasword.zetta_spells.spells.magic.CustomPlayerSpell;
import zettasword.zetta_spells.spells.ZSSpells;

@Mixin(value = SpellGUIDisplay.class, remap = false)
public abstract class SpellGUIDisplayMixin {

    // Invoker to call the original private method
    @Invoker("getFormattedSpellName")
    private static Component invokeGetFormattedSpellName(Spell spell, Player player, int cooldown) {
        throw new AssertionError("Unimplemented");
    }

    // Redirects the 1st call to getFormattedSpellName (Previous Spell)
    @Redirect(
            method = "renderSpellHUD",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/binaris/wizardry/client/SpellGUIDisplay;getFormattedSpellName(Lcom/binaris/wizardry/api/content/spell/Spell;Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/network/chat/Component;",
                    ordinal = 0
            )
    )
    private static Component zettaSpells_redirectPrevSpellName(
            Spell spell, Player player, int cooldown,
            // Appended target method arguments to capture 'wand':
            GuiGraphics guiGraphics, PoseStack stack, Player playerArg, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer
    ) {
        Component original = invokeGetFormattedSpellName(spell, player, cooldown);
        if (spell == ZSSpells.CUSTOM_PLAYER_SPELL.get()) {
            int index = CustomPlayerSpell.getAdjacentSpellIndex(wand, -1);
            if (index != -999) {
                String name = CustomPlayerSpell.getCustomSpellName(wand, index);
                if (name != null) return Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE);
            }
        }
        return original;
    }

    // Redirects the 2nd call to getFormattedSpellName (Current Spell)
    @Redirect(
            method = "renderSpellHUD",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/binaris/wizardry/client/SpellGUIDisplay;getFormattedSpellName(Lcom/binaris/wizardry/api/content/spell/Spell;Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/network/chat/Component;",
                    ordinal = 1
            )
    )
    private static Component zettaSpells_redirectCurrentSpellName(
            Spell spell, Player player, int cooldown,
            GuiGraphics guiGraphics, PoseStack stack, Player playerArg, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer
    ) {
        Component original = invokeGetFormattedSpellName(spell, player, cooldown);
        if (spell == ZSSpells.CUSTOM_PLAYER_SPELL.get()) {
            int index = CastItemDataHelper.getCurrentSpellIndex(wand);
            String name = CustomPlayerSpell.getCustomSpellName(wand, index);
            if (name != null) return Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return original;
    }

    // Redirects the 3rd call to getFormattedSpellName (Next Spell)
    @Redirect(
            method = "renderSpellHUD",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/binaris/wizardry/client/SpellGUIDisplay;getFormattedSpellName(Lcom/binaris/wizardry/api/content/spell/Spell;Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/network/chat/Component;",
                    ordinal = 2
            )
    )
    private static Component zettaSpells_redirectNextSpellName(
            Spell spell, Player player, int cooldown,
            GuiGraphics guiGraphics, PoseStack stack, Player playerArg, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer
    ) {
        Component original = invokeGetFormattedSpellName(spell, player, cooldown);
        if (spell == ZSSpells.CUSTOM_PLAYER_SPELL.get()) {
            int index = CustomPlayerSpell.getAdjacentSpellIndex(wand, 1);
            if (index != -999) {
                String name = CustomPlayerSpell.getCustomSpellName(wand, index);
                if (name != null) return Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE);
            }
        }
        return original;
    }
}