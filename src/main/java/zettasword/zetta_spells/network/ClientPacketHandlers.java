package zettasword.zetta_spells.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zettasword.zetta_spells.capability.RaceDataHolder;
import zettasword.zetta_spells.capability.spellcaster.SpellcasterData;
import zettasword.zetta_spells.items.spellbook.SpellbookWriterScreen;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {
    
    // ✅ This method can safely reference Screen because this class is NEVER loaded on server
    public static void openWriterScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new SpellbookWriterScreen());
    }

    // ✅ Handle race capability sync - SAFE to use Minecraft/Player here
    public static void applyRaceSync(CompoundTag data) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || data == null) return;

        player.getCapability(RaceDataHolder.INSTANCE).ifPresent(cap -> {
            cap.deserializeNBT(data);
            // ✅ Optional: Trigger UI update if your HUD listens to capability changes
            // MinecraftForge.EVENT_BUS.post(new RaceDataUpdatedEvent(player, cap));
        });
    }

}