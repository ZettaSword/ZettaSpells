package zettasword.zetta_spells.system.spellcreation.actions;

import com.binaris.wizardry.api.content.item.IManaItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.items.SpellWordItem;
import zettasword.zetta_spells.system.GalacticTranslator;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;

import javax.annotation.Nullable;
import java.util.List;

/** Base class of SpellAction to register stuff. Can be a shape, and action to do, or etc. **/
public abstract class SpellWord {
    private final ResourceLocation registryName;
    /** This one exists and used for defining if action was success or not **/
    private boolean success = false;

    public SpellWord(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public SpellWord(String spellword) {
        this.registryName = ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, spellword);
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    /**
     * Returns the display name for this spellword when held in an ItemStack
     * Override in subclasses for custom naming
     */
    public Component getDisplayName(ItemStack context) {
        return Component.translatable("spellword." + registryName.getNamespace() + "." + registryName.getPath());
    }

    /**
     * Adds tooltip lines for this spellword
     * Override in subclasses for custom descriptions
     */
    public void addTooltip(ItemStack context, @Nullable Level level,
                           List<Component> tooltip, TooltipFlag flag) {
        boolean discovered = context.getOrCreateTag().getBoolean(SpellWordItem.DISCOVERED_TAG);

        // I know this is messy, but at least it works for now I guess.
        Component description = discovered ? Component.translatable(
                "spellword." + registryName.getNamespace() + "." + registryName.getPath() + ".description").withStyle(ChatFormatting.GOLD)
                : Component.literal(GalacticTranslator.toGalactic(Component.translatable("spellword." + registryName.getNamespace() +
                    "." + registryName.getPath() + ".description").toString())).withStyle(ChatFormatting.GOLD);

        if (!description.getString().isEmpty()) {
            tooltip.add(description);
            if (!discovered) tooltip.add(Component.translatable("spellword.zetta_spells.general_item_not_discovered").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

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
        if (ctx.isExternalCast()){
            ctx.addPreCost(cost);
            return ctx.lastExternalMana() >= ctx.getPreCost();
        }
        ItemStack stack = getSpellBook(ctx);
        if (stack.getItem() instanceof IManaItem manaStoringItem && manaStoringItem.getMana(stack) >= cost){
            manaStoringItem.consumeMana(stack, cost, ctx.getCaster());
            ctx.addPreCost(cost);
            return true;
        }
        return false;
    }

    public static int getLastUsedMana(SpellCreateContext ctx){
        ItemStack stack = getSpellBook(ctx);
        if (stack.getItem() instanceof IManaItem manaStoringItem){
            return manaStoringItem.getMana(stack);
        }
        return 0;
    }

    public static int getCurrentMana(SpellCreateContext ctx){
        if (ctx.isExternalCast()){
            return ctx.lastExternalMana() - ctx.getPreCost();
        }
        if (ctx.getCaster() == null) return 0;
        ItemStack stack = getSpellBook(ctx);
        if (stack.getItem() instanceof IManaItem manaStoringItem){
            return manaStoringItem.getMana(stack);
        }
        return 0;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void success(){
        this.success = true;
    }

    public void fail(){
        this.success = false;
    }

    /** Override this one to change if spellword should have item. **/
    public boolean doesHaveItem(){return true;}
}
