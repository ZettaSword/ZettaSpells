package zettasword.zetta_spells.items.spellbook;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.item.SpellBookItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.spells.ZSSpells;

public class CustomSpellBook extends SpellBookItem {
    public CustomSpellBook() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        RegistryUtils.setSpell(stack, ZSSpells.CUSTOM_PLAYER_SPELL.get());
        stack.getOrCreateTag();
        if (level.isClientSide) ClientUtils.openSpellBook(stack);
        return super.use(level, player, interactionHand);
    }
}
