package zettasword.zetta_spells.system;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.ZettaSpellsMod;

@Mod.EventBusSubscriber(modid = ZettaSpellsMod.MODID)
public class MinionSitManager {
    // Track sitting state (Server side)
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide) return; // Only handle on server
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (event.getEntity() instanceof Player) return;

        // If this minion is sitting, lock its movement
        if (mob.getPersistentData().getBoolean("sitting")){
            // 1. Stop Navigation
            mob.getNavigation().stop();
            
            // 2. Zero out velocity (prevents sliding/pushing)
            mob.setDeltaMovement(0, 0, 0);
            mob.hurtMarked = true; // Force movement update packet
        }
    }
}