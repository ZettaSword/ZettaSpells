package zettasword.zetta_spells.system.mixins;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.client.SpellGUIDisplay;
import com.binaris.wizardry.core.config.EBClientConfig;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import zettasword.zetta_spells.spells.CustomPlayerSpell;
import zettasword.zetta_spells.spells.ZSSpells;

@Mixin(value = SpellGUIDisplay.class, remap = false)
public abstract class SpellGUIDisplayMixin {

    @Shadow
    private static int switchTimer = 0;

    @Shadow
    private static final int SPELL_SWITCH_TIME = 4;

    @Invoker("getFormattedSpellName")
    private static Component getFormattedSpellName(Spell spell, Player player, int cooldown) {
        throw new AssertionError("Unimplemented");
    }

    @Invoker("drawBackground")
    private static void drawBackground(GuiGraphics guiGraphics, int x, int y, boolean flipX, boolean flipY, ResourceLocation icon, float cooldownBarProgress, boolean creativeMode, boolean jammed) {
        throw new AssertionError("Unimplemented");
    }

    @Invoker("drawText")
    private static void drawText(GuiGraphics guiGraphics, int x, int y, boolean flipX, boolean flipY, Component prevSpellName, Component spellName, Component nextSpellName, float animationProgress) {
        throw new AssertionError("Unimplemented");
    }

        // NOT the best way to do it, I'm kinda scared doing THIS
    /**
     * @author ZettaSword
     * @reason Trying to allow rendering custom spell's names.
     */
    @Overwrite
    public static void renderSpellHUD(GuiGraphics guiGraphics, PoseStack stack, Player player, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer){
        if (!EBClientConfig.SHOW_SPELL_HUD.get()) return;

        if (!(wand.getItem() instanceof ICastItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        boolean flipX = EBClientConfig.SPELL_HUD_FLIP_X.get();
        boolean flipY = EBClientConfig.SPELL_HUD_FLIP_Y.get();

        if (EBClientConfig.SPELL_HUD_DYNAMIC_POSITIONING.get()) {
            flipX = flipX == ((mainHand ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.LEFT);
        }

        stack.pushPose();

        int x = flipX ? width : 0;
        int y = flipY ? 0 : height;

        Spell spell = ((ICastItem) wand.getItem()).getCurrentSpell(wand);
        int cooldown = ((ICastItem) wand.getItem()).getCurrentCooldown(wand, player.level());
        int maxCooldown = ((ICastItem) wand.getItem()).getCurrentMaxCooldown(wand);

        if (textLayer) {
            float animationProgress = Math.signum(switchTimer) * ((SPELL_SWITCH_TIME - Math.abs(switchTimer) + partialTicks) / SPELL_SWITCH_TIME);
            // My changes
            Spell previous = ((ICastItem) wand.getItem()).getPreviousSpell(wand);
            Spell current = ((ICastItem) wand.getItem()).getCurrentSpell(wand);
            Spell next = ((ICastItem) wand.getItem()).getNextSpell(wand);
            // My changes


            Component prevSpellName = getFormattedSpellName(((ICastItem) wand.getItem()).getPreviousSpell(wand), player, 0);
            Component spellName = getFormattedSpellName(((ICastItem) wand.getItem()).getCurrentSpell(wand), player, cooldown);
            Component nextSpellName = getFormattedSpellName(((ICastItem) wand.getItem()).getNextSpell(wand), player, 0);



            // My changes
            int currentIndex = CastItemDataHelper.getCurrentSpellIndex(wand);
            int previousIndex = CustomPlayerSpell.getAdjacentSpellIndex(wand, -1);
            int nextIndex = CustomPlayerSpell.getAdjacentSpellIndex(wand, 1);
            if (previousIndex != -999) {
                if (previous == ZSSpells.CUSTOM_PLAYER_SPELL.get()) {
                    String name = CustomPlayerSpell.getCustomSpellName(wand, previousIndex);
                    if (name != null) prevSpellName = Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE);
                }
            }

            if (current == ZSSpells.CUSTOM_PLAYER_SPELL.get()){
                String name = CustomPlayerSpell.getCustomSpellName(wand, currentIndex);
                if (name != null) spellName = Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE);
            }

            if (nextIndex != -999){
                if (next == ZSSpells.CUSTOM_PLAYER_SPELL.get()){
                    String name = CustomPlayerSpell.getCustomSpellName(wand, nextIndex);
                    if (name != null) nextSpellName = Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE);
                }
            }
            drawText(guiGraphics, x, y, flipX, flipY, prevSpellName, spellName, nextSpellName, animationProgress);
        } else {
            boolean discovered = true;

            if (!player.isCreative()) {
                discovered = Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell);
            }

            ResourceLocation location = spell.getLocation();
            ResourceLocation icon = discovered ?
                    WizardryMainMod.location(location.getNamespace(), "textures/spells/%s.png".formatted(location.getPath()))
                    : WizardryMainMod.location("textures/spells/none.png");

            float progress = 1;
            if (!player.isCreative()) {
                progress = maxCooldown == 0 ? 1 : (maxCooldown - (float) cooldown + partialTicks) / maxCooldown;
            }

            drawBackground(guiGraphics, x, y, flipX, flipY, icon, progress, player.isCreative(), player.hasEffect(EBMobEffects.ARCANE_JAMMER.get()));
        }

        stack.popPose();
    }
}
