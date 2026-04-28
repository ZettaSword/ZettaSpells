package zettasword.zetta_spells.system.mixins;


import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.client.gui.elements.TooltipElementSpellEntry;
import com.binaris.wizardry.content.item.ScrollItem;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zettasword.zetta_spells.spells.CustomPlayerSpell;
import zettasword.zetta_spells.spells.ZSSpells;

@Mixin(value = TooltipElementSpellEntry.class, remap = false)
public abstract class TooltipElementSpellEntryMixin {
    @Final
    @Shadow
    private int index;
    @Final
    @Shadow private com.binaris.wizardry.client.gui.screens.ArcaneWorkbenchScreen screen;

    @Shadow private Spell getSpell(ItemStack stack) {
        ItemStack spellBook = screen.getMenu().getSlot(index).getItem();

        if (!spellBook.isEmpty() && (spellBook.getItem() instanceof SpellBookItem || spellBook.getItem() instanceof ScrollItem)) {
            return RegistryUtils.getSpell(spellBook);
        } else {
            Spell spell = ((ICastItem) stack.getItem()).getSpells(stack)[index];
            return spell == null ? Spells.NONE : spell;
        }
    }

    @Inject(
        method = "getText(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onGetText(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        Spell spell = getSpell(stack);
        if (spell == ZSSpells.CUSTOM_PLAYER_SPELL.get()) {
            String name = CustomPlayerSpell.getCustomSpellName(stack, index);
            if (name != null) cir.setReturnValue(Component.literal(name));
        }
    }
}