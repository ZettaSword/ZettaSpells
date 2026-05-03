package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.TextProcessingUtil;

import java.util.List;

@Mod.EventBusSubscriber(modid = ZettaSpells.MODID)
public class MagicWordsCreator {

   // @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent()
    public static void chatEventServer(ServerChatEvent event){
        ServerPlayer player = event.getPlayer();
        SpellCreateContext ctx = new SpellCreateContext(player.level(), player, InteractionHand.MAIN_HAND);
        spellCast(ctx, event.getRawText());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void chatEventClient(ClientChatEvent event){
        Minecraft mc = Minecraft.getInstance();
        SpellCreateContext ctx = new SpellCreateContext(mc.level, mc.player, InteractionHand.MAIN_HAND);
        spellCast(ctx, event.getMessage());
    }

    public static void spellCast(SpellCreateContext context, String spell) {
        Level level = context.world();
        LivingEntity caster = context.getCaster();
        if (caster == null) return;
        SpellTarget target = new SpellTarget(caster);
        ItemStack stack = caster.getMainHandItem();
        String previous = "";

        // Starting the spell-creation.
        List<String> words = TextProcessingUtil.extractWords(spell);
        for (int i = 0; i < words.size(); ++i) {
            String current = words.get(i);
            String next = getNext(words, i, 1);
            //TODO: Make it registry based system instead

            // Because of WordsDynamicLightning class we just need to put one simple tag and everything is ready!
            if (current.equals("lux")) {
                if (!level.isClientSide) {
                    if (!stack.isEmpty()) {
                        CompoundTag tag = stack.getOrCreateTag();
                        tag.putBoolean("lux", true);
                    }
                }
            }

            if (current.equals("delumos")){
                if (!level.isClientSide){
                    if (!stack.isEmpty()){
                        CompoundTag tag = stack.getTag();
                        if (tag != null && tag.contains("lux")){
                            tag.remove("lux");
                        }
                    }
                }
            }

            if (target.getTargetEntity() instanceof LivingEntity living) {
                if (current.equals("watero")) {
                    if (!level.isClientSide) {
                        Alchemy.apply(living, 60, 0, MobEffects.WATER_BREATHING);
                    }
                }
            }



            previous = current;
        }
    }

    public static String getNext(List<String> words, int i, int next){
        return i+next < words.size() ? words.get(i+next) : "";
    }
}
