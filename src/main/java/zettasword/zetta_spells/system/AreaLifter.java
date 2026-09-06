package zettasword.zetta_spells.system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;

import java.util.List;

// Generated with Qwen.
public class AreaLifter {

    /**
     * Lifts a 16x32x16 area of blocks centered on the given BlockPos.
     * Cascades upwards if the destination is occupied.
     * Safely moves BlockEntities without duplicating items.
     * Excludes players from entity movement.
     *
     * @return true if the operation was successful, false if aborted (e.g., height > 64).
     */
    public static boolean liftArea(ServerLevel level, BlockPos center, int liftDistance) {
        if (liftDistance == 0) return false;

        // 1. Calculate the initial bounds of the 16x32x16 area
        int minX = center.getX() - 8;
        int maxX = center.getX() + 7;  // 16 blocks wide
        int minY = center.getY() - 16;
        int maxY = center.getY() + 15; // 32 blocks high
        int minZ = center.getZ() - 8;
        int maxZ = center.getZ() + 7;  // 16 blocks deep

        // 2. CASCADE CHECK: Find the highest block in the 16x16 footprint above the area
        int worldMax = level.getMaxBuildHeight() - 1;

        if (liftDistance > 0) {
            for (int y = maxY + 1; y <= worldMax; y++) {
                boolean hasBlock = false;
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                            hasBlock = true;
                            break;
                        }
                    }
                    if (hasBlock) break;
                }

                if (hasBlock) {
                    maxY = y;
                } else {
                    break;
                }
            }
        }

        // --- NEW FEATURE 1: CHECK TOTAL HEIGHT ---
        int totalHeight = maxY - minY + 1;
        if (totalHeight > 64) {
            return false; // Abort if the structure is taller than 64 blocks
        }

        // 3. Determine iteration direction
        int yStart, yEnd, yStep;
        if (liftDistance > 0) {
            yStart = maxY;
            yEnd = minY - 1;
            yStep = -1;
        } else {
            yStart = minY;
            yEnd = maxY + 1;
            yStep = 1;
        }

        // 4. Move the blocks and BlockEntities
        for (int y = yStart; y != yEnd; y += yStep) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos sourcePos = new BlockPos(x, y, z);
                    BlockPos destPos = new BlockPos(x, y + liftDistance, z);

                    if (!level.hasChunkAt(sourcePos) || !level.hasChunkAt(destPos)) continue;

                    // Prevent moving out of world bounds
                    if (destPos.getY() < level.getMinBuildHeight() || destPos.getY() > worldMax) continue;

                    // --- NEW FEATURE 2: CHECK DESTINATION BEFORE TOUCHING SOURCE ---
                    // If the destination is not air, skip this block entirely.
                    // Because we do this FIRST, we avoid breaking double chests, doors, etc.
                    if (!level.getBlockState(destPos).isAir()) {
                        continue;
                    }

                    BlockState state = level.getBlockState(sourcePos);

                    if (state.isAir()) {
                        if (level.getBlockEntity(destPos) != null) level.removeBlockEntity(destPos);
                        level.setBlock(destPos, Blocks.AIR.defaultBlockState(), 2);
                        level.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), 2);
                        continue;
                    }

                    // --- HANDLE BLOCK ENTITY (TILE ENTITY) ---
                    // We only reach here if the destination is guaranteed to be air.
                    BlockEntity be = level.getBlockEntity(sourcePos);
                    CompoundTag beTag = null;
                    if (be != null) {
                        beTag = be.saveWithoutMetadata();
                        ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType());
                        if (id != null) {
                            beTag.putString("id", id.toString());
                        }
                        beTag.putInt("x", destPos.getX());
                        beTag.putInt("y", destPos.getY());
                        beTag.putInt("z", destPos.getZ());
                    }

                    // Clear destination BlockEntity (redundant due to isAir check, but safe)
                    if (level.getBlockEntity(destPos) != null) {
                        level.removeBlockEntity(destPos);
                    }

                    // Place the block at the destination
                    level.setBlock(destPos, state, 2);

                    // Recreate the BlockEntity at the new position
                    if (beTag != null) {
                        BlockEntity newBe = BlockEntity.loadStatic(destPos, state, beTag);
                        if (newBe != null) {
                            level.setBlockEntity(newBe);
                        }
                    }

                    // Clear source BlockEntity BEFORE setting to air to prevent item drops
                    if (be != null) {
                        level.removeBlockEntity(sourcePos);
                    }

                    // Clear the original source block
                    level.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        // 5. Move Entities (EXCLUDING PLAYERS)
        // We use the updated `maxY` so entities standing on the cascaded blocks are also moved
        AABB areaBox = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
        AABB searchBox = areaBox.inflate(0, 2, 0); // Inflate Y slightly to catch entities standing on top

        List<Entity> entities = level.getEntities(null, searchBox);

        for (Entity entity : entities) {

            // Only lift entities strictly within the X/Z bounds of the area
            if (entity.getX() >= minX && entity.getX() < maxX + 1 &&
                    entity.getZ() >= minZ && entity.getZ() < maxZ + 1) {
                double newY = entity.getY() + liftDistance;

                if (entity instanceof Player player) {
                    player.teleportTo(player.getX(), newY, player.getZ());
                }

                // Move the entity
                entity.moveTo(entity.getX(), newY, entity.getZ(), entity.getYRot(), entity.getXRot());

                // Reset fall distance to prevent damage
                entity.fallDistance = 0.0F;
            }
        }

        ZSSigil sigil = SigilCreator.create(level, center.getCenter(), 200, "earth");
        sigil.setSizeMultiplier(16);
        level.addFreshEntity(sigil);

        return true;
    }
}