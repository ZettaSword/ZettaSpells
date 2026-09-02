package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class TreeChop extends RaySpell {
    public TreeChop(){
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return breakLogs(ctx.world(), blockHit.getBlockPos(), ctx.caster());
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!ctx.world().isClientSide){
            if (entityHit.getEntity().isAlive() && entityHit.getEntity() instanceof LivingEntity living){
                living.hurt(living.damageSources().inWall(), 4);
            }
        }
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).time(15 + ctx.world().random.nextInt(5)).spawn(ctx.world());
        //ctx.world().addParticle(ParticleTypes.ASH, x,y,z, vx,vy,vz);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.UTILITY, SpellAction.POINT, 100, 0, 40)
                .add(DefaultProperties.RANGE, 9F)
                .build();
    }

    // AI generated (using Qwen) and then modified to fit the spell.

    /**
     * Removes all connected log blocks from a starting position.
     *
     * @param level    The world/level instance
     * @param startPos The position of the first log broken by the player
     * @param living   The player who broke the log (used for item drops/permissions)
     */
    public static boolean breakLogs(Level level, BlockPos startPos, LivingEntity living) {
        BlockState startState = level.getBlockState(startPos);
        if (!startState.is(BlockTags.LOGS)) return false;
        if (level.isClientSide) return true;

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        // Safety limits to prevent server lag
        int maxLogs = 64;
        int maxDistanceSq = 12 * 12; // 12 block radius limit
        int logsBroken = 0;

        while (!queue.isEmpty() && logsBroken < maxLogs) {
            BlockPos current = queue.poll();

            // Stop searching if we've gone too far from the original block
            if (current.distSqr(startPos) > maxDistanceSq) {
                continue;
            }

            BlockState state = level.getBlockState(current);
            if (state.is(BlockTags.LOGS)) {
                // destroyBlock removes the block and drops the item.
                // Passing the player ensures drops are attributed to them.
                level.destroyBlock(current, true, living);
                logsBroken++;
            }

            // Check the 6 immediate neighbors (Up, Down, North, South, East, West)
            // Using 6 directions prevents the algorithm from eating adjacent trees
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    // Only add to the queue if the neighbor is actually a log
                    if (level.getBlockState(neighbor).is(BlockTags.LOGS)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
        return true;
    }
}
