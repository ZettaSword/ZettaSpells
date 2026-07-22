package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.TextProcessingUtil;

import java.util.List;

@Mod.EventBusSubscriber(modid = ZettaSpells.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagicWordsCreator {

    @SubscribeEvent
    public static void chatEventServer(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String rawText = event.getRawText(); // Gets the exact string typed by the player

        List<String> words = TextProcessingUtil.extractWords(rawText);
        SpellCreateContext ctx = new SpellCreateContext(player.level(), player, InteractionHand.MAIN_HAND);

        // spellCast now returns true if a spell was actually activated
        boolean spellActivated = spellCast(ctx, words);

        if (spellActivated) {
            // Cancel the event so the spell command does not appear in public chat
            event.setCanceled(true);
        }
    }

    /**
     * Processes the spell. Returns true if any spell action was successfully triggered.
     */
    public static boolean spellCast(SpellCreateContext context, List<String> words) {
        Level level = context.world();
        LivingEntity caster = context.getCaster();

        // Safety check: Ensure this logic ONLY runs on the logical server
        if (caster == null || level.isClientSide) {
            return false;
        }

        SpellTarget target = new SpellTarget(caster);
        ItemStack stack = caster.getMainHandItem();
        boolean spellActivated = false;
        String previous = "";

        for (int i = 0; i < words.size(); ++i) {
            String current = words.get(i);
            String next = getNext(words, i, 1);

            if (current.equals("lux")) {
                if (!stack.isEmpty()) {
                    CompoundTag tag = stack.getOrCreateTag();
                    tag.putBoolean("lux", true);
                    spellActivated = true;
                }
            }

            if (current.equals("delumos")) {
                if (!stack.isEmpty()) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null && tag.contains("lux")) {
                        tag.remove("lux");
                        spellActivated = true;
                    }
                }
            }

            if (target.getTargetEntity() instanceof LivingEntity living) {
                if (current.equals("watero")) {
                    Alchemy.apply(living, 60, 0, MobEffects.WATER_BREATHING);
                    spellActivated = true;
                }
            }

            previous = current;
        }

        return spellActivated;
    }

    public static String getNext(List<String> words, int i, int next) {
        return i + next < words.size() ? words.get(i + next) : "";
    }
}