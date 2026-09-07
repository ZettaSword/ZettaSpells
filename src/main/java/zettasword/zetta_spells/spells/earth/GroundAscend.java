package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;
import zettasword.zetta_spells.system.SigilCreator;

import java.util.List;

public class GroundAscend extends RaySpell {

    public GroundAscend(){
    }

    /**
     * Called when the ray hits an entity. Override this method to perform an action when the ray hits an entity, such as
     * dealing damage or applying a status effect. Return true if the spell should be considered successfully cast
     * when it hits an entity, or false if the spell should not be cast when it hits an entity (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param entityHit The result of the entity hit, containing information about the hit entity and hit position.
     * @param origin    The starting point of the ray.
     * @return true if the spell should be considered successfully cast when it hits an entity, false if the spell should
     * not be cast when it hits an entity.
     */
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!ctx.world().isClientSide){
            ServerLevel level = (ServerLevel) ctx.world();
            return liftArea(level, entityHit.getEntity().getOnPos(), 16);
        }
        return true;
    }

    /**
     * Called when the ray hits a block. Override this method to perform an action when the ray hits a block, such as
     * creating an explosion or spawning particles. Return true if the spell should be considered successfully cast
     * when it hits a block, or false if the spell should not be cast when it hits a block (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx      The cast context of the spell.
     * @param blockHit The result of the block hit, containing information about the hit position and block.
     * @param origin   The starting point of the ray.
     * @return true if the spell should be considered successfully cast when it hits a block, false if the spell should
     * not be cast when it hits a block.
     */
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!ctx.world().isClientSide){
            ServerLevel level = (ServerLevel) ctx.world();
            return liftArea(level, blockHit.getBlockPos(), 16);
        }
        return true;
    }

    /**
     * Called when the ray does not hit any entities or blocks. Override this method to perform an action when the ray
     * misses, such as spawning particles at the endpoint. Return true if the spell should be considered successfully
     * cast even when it misses, or false if the spell should not be cast when it misses (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param origin    The starting point of the ray.
     * @param direction The normalized direction vector of the ray.
     * @return true if the spell should be considered successfully cast even when it misses, false if the spell should
     * not be cast when it misses.
     */
    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    /**
     * This method is where you should set the default properties for your spell when creating a new spell class. This
     * method is called in the constructor of the Spell class, and the properties returned by this method are assigned
     * to the spell's properties field.
     *
     * @return A SpellProperties object with the default properties for your spell.
     */
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellTypes.ALTERATION, SpellAction.POINT, 2500, 120, 1200)
                .add(DefaultProperties.RANGE, 30F)
                .build();
    }

    /** Generated with Qwen (AI).
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
