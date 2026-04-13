package zettasword.zetta_spells.system.spellcreation.actions;

import com.binaris.wizardry.api.content.item.IManaStoringItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;

import javax.annotation.Nullable;
import java.util.List;

/** Base class of SpellAction to register stuff. Can be a shape, and action to do, or etc. **/
public abstract class SpellWord {
    /** Allows us to get if key things are considered. Like if there is a word, and is a current target is a spider, and more, obviously.
     * @param ctx Context of the spell.
     * @param words Words used in the spell.
     * @param i Current index in [words], so you can understand where we are at in the spell.
     * **/
    public abstract boolean shouldCast(SpellCreateContext ctx, List<String> words, int i);

    /** Use this to cast the spell you've made and registered.
     * @param ctx Context of the spell.
     * @param words Words used in the spell.
     * @param i Current index in [words], so you can understand where we are at in the spell.
     * **/
    public abstract boolean cast(SpellCreateContext ctx, List<String> words, int i);

    @Nullable
    public static LivingEntity living(Entity target){
        if (target instanceof LivingEntity living) return living;
        return null;
    }

    /** For getting spellbook that is casting the spell! **/
    public static ItemStack getSpellBook(SpellCreateContext ctx){
        return ctx.getCaster().getItemInHand(ctx.getHand());
    }

    /** Helper method to easily consume amount of mana from the book. **/
    public static boolean consumeMana(SpellCreateContext ctx,int cost){
        ItemStack stack = getSpellBook(ctx);
        if (stack.getItem() instanceof IManaStoringItem manaStoringItem && manaStoringItem.getMana(stack) >= cost){
            manaStoringItem.consumeMana(stack, cost, ctx.getCaster());
            return true;
        }
        return false;
    }

    public static int getCurrentMana(SpellCreateContext ctx){
        ItemStack stack = getSpellBook(ctx);
        if (stack.getItem() instanceof IManaStoringItem manaStoringItem){
            return manaStoringItem.getMana(stack);
        }
        return 0;
    }
}
