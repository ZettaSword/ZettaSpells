package zettasword.zetta_spells;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import zettasword.zetta_spells.capability.RaceDataHolder;
import zettasword.zetta_spells.network.RaceCapabilitySyncPacketS2C;

/**
 * Handles messages received on the client side, we may only call client-side methods from here because we don't want to
 * accidentally reference client-only code on the server side.
 */
public final class ZSClientMessageHandler {
    private ZSClientMessageHandler() {
    }

    public static void raceCapabilitySync(RaceCapabilitySyncPacketS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        player.getCapability(RaceDataHolder.INSTANCE).ifPresent(d -> d.deserializeNBT(m.getData()));
    }
}
