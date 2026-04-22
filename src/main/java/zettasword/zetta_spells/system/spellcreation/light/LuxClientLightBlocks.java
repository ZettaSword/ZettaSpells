package zettasword.zetta_spells.system.spellcreation.light;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.ZettaSpells;

import java.util.HashSet;
import java.util.Set;

// Client-side only dynamic lighting (actual game light)
@Mod.EventBusSubscriber(modid = ZettaSpells.MODID,value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LuxClientLightBlocks {
    // Tracks light blocks we placed for the local player
    private static final Set<BlockPos> clientLights = new HashSet<>();

    // Configurable parameters
    private static final int LIGHT_RADIUS = 1;        // Sphere radius (keep ≤ 3 for performance)
    private static final int LIGHT_LEVEL = 15;        // Max light level (0-15)
    private static final int RADIUS_SQUARED = LIGHT_RADIUS * LIGHT_RADIUS; // Pre-calc for sphere check

    /**
     * Main tick handler - runs 20x/sec on client
     * Places/removes LightBlocks based on held item state
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // Only process at end of tick to ensure game state is stable
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        boolean holdingLux = isHoldingLux(player);
        BlockPos center = player.blockPosition();
        Set<BlockPos> desiredLights = getBlockPos(holdingLux, center);

        // Remove lights that are no longer needed
        for (BlockPos pos : clientLights) {
            if (mc.level.getBlockState(pos).getBlock() == Blocks.LIGHT) {
                mc.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // 2 = Block.UPDATE_CLIENTS
            }
        }
        clientLights.clear();

        // Place new lights where needed
        if (holdingLux) {
            for (BlockPos pos : desiredLights) {
                // Only place in replaceable/empty blocks to avoid overwriting terrain
                if (mc.level.isEmptyBlock(pos) || mc.level.getBlockState(pos).canBeReplaced()) {
                    mc.level.setBlock(
                            pos,
                            Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, LIGHT_LEVEL),
                            2
                    );
                    clientLights.add(pos);
                }
            }
        }
    }

    private static @NotNull Set<BlockPos> getBlockPos(boolean holdingLux, BlockPos center) {
        Set<BlockPos> desiredLights = new HashSet<>();

        // Calculate which blocks SHOULD have light this tick
        if (holdingLux) {
            for (int x = -LIGHT_RADIUS; x <= LIGHT_RADIUS; x++) {
                for (int y = -LIGHT_RADIUS; y <= LIGHT_RADIUS; y++) {
                    for (int z = -LIGHT_RADIUS; z <= LIGHT_RADIUS; z++) {
                        // Spherical boundary: x² + y² + z² ≤ r²
                        if (x*x + y*y + z*z <= RADIUS_SQUARED) {
                            desiredLights.add(center.offset(x, y+2, z));
                        }
                    }
                }
            }
        }
        return desiredLights;
    }

    /**
     * Cleanup handler - removes all placed lights when player disconnects
     * Prevents orphaned lights from persisting in unloaded chunks
     */
    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        for (BlockPos pos : clientLights) {
            if (mc.level.getBlockState(pos).getBlock() == Blocks.LIGHT) {
                mc.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        clientLights.clear();
    }

    /**
     * Checks if player holds any item with the "lux" boolean NBT tag
     */
    private static boolean isHoldingLux(LocalPlayer player) {
        for (ItemStack stack : player.getHandSlots()) {
            CompoundTag tag = stack.getTag();
            if (!stack.isEmpty() && tag != null && tag.getBoolean("lux")) {
                return true;
            }
        }
        return false;
    }
}