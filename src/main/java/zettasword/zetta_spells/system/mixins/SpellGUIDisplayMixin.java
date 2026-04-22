package zettasword.zetta_spells.system.mixins;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.client.SpellGUIDisplay;
import com.binaris.wizardry.client.SpellHUDSkin;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.mojang.blaze3d.vertex.PoseStack;
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

    @Invoker("getSkin")
    public static SpellHUDSkin getSkin(String key){
        throw new AssertionError("Unimplemented");
    }

    @Invoker("getFormattedSpellName")
    private static Component getFormattedSpellName(Spell spell, Player player, int cooldown) {
        throw new AssertionError("Unimplemented");
    }

    // NOT the best way to do it, I'm kinda scared doing THIS
    /**
     * @author ZettaSword
     * @reason Trying to allow rendering custom spell's names.
     */
    @Overwrite
    public static void renderSpellHUD(GuiGraphics guiGraphics, PoseStack stack, Player player, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer){
        if (!EBConfig.SHOW_SPELL_HUD.get()) return;

        if (!(wand.getItem() instanceof ICastItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        boolean flipX = EBConfig.SPELL_HUD_FLIP_X.get();
        boolean flipY = EBConfig.SPELL_HUD_FLIP_Y.get();

        if (EBConfig.SPELL_HUD_DYNAMIC_POSITIONING.get()) {
            flipX = flipX == ((mainHand ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.LEFT);
        }

        SpellHUDSkin skin = getSkin("default");
        if (skin == null) return;

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

            Component prevSpellName = getFormattedSpellName(previous, player, 0);
            Component spellName = getFormattedSpellName(current, player, cooldown);
            Component nextSpellName = getFormattedSpellName(next, player, 0);

            // My changes
            int currentIndex = CastItemDataHelper.getCurrentSpellIndex(wand);
            int previousIndex = CustomPlayerSpell.getAdjacentSpellIndex(wand, -1);
            int nextIndex = CustomPlayerSpell.getAdjacentSpellIndex(wand, 1);
            if (previousIndex != -999) {
                if (previous == ZSSpells.CUSTOM_PLAYER_SPELL.get()) {
                    String name = CustomPlayerSpell.getCustomSpellName(wand, previousIndex);
                    if (name != null) prevSpellName = Component.literal(name);
                }
            }

            if (current == ZSSpells.CUSTOM_PLAYER_SPELL.get()){
                String name = CustomPlayerSpell.getCustomSpellName(wand, currentIndex);
                if (name != null) spellName = Component.literal(name);
            }

            if (nextIndex != -999){
                if (next == ZSSpells.CUSTOM_PLAYER_SPELL.get()){
                    String name = CustomPlayerSpell.getCustomSpellName(wand, nextIndex);
                    if (name != null) nextSpellName = Component.literal(name);
                }
            }

            // My changes
            skin.drawText(guiGraphics, x, y, flipX, flipY, prevSpellName, spellName, nextSpellName, animationProgress);
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

            skin.drawBackground(stack, x, y, flipX, flipY, icon, progress, player.isCreative(), player.hasEffect(EBMobEffects.ARCANE_JAMMER.get()));
        }

        stack.popPose();
    }
}
