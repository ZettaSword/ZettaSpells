package zettasword.zetta_spells.blocks;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.client.renderer.entity.RemnantRenderer;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zettasword.zetta_spells.blocks.entities.WarpPointBlockEntity;
import zettasword.zetta_spells.items.ZSItems;

public class WarpPointBlock extends Block implements EntityBlock {

    public WarpPointBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living && !level.isClientSide()){
            // Return if it's player with Lost Teleporter Ring.
            if (living instanceof Player player && ArtifactChannel.isEquipped(player, ZSItems.LOST_TELEPORTER_RING.get()))
                return;
            BlockEntity e = level.getBlockEntity(blockPos);
            if (e instanceof WarpPointBlockEntity warp){
                if (warp.hasTarget()){
                    if (living.getPortalCooldown() > 0){
                        return;
                    }
                    teleport(living, blockPos);
                }
            }
        }
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (!player.isShiftKeyDown())
            return InteractionResult.PASS;

        if (player.getPortalCooldown() > 0){
            return InteractionResult.PASS;
        }

        if (!ArtifactChannel.isEquipped(player, ZSItems.LOST_TELEPORTER_RING.get())) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof WarpPointBlockEntity waystone)) {
            return InteractionResult.PASS;
        }

        // ── Not linked yet → inform the player ────────────────
        if (!waystone.hasTarget()) {
            player.sendSystemMessage(
                    Component.translatable("spell.zetta_spells.link_warp_points.not_linked")
            );
            return InteractionResult.CONSUME;
        }


        // ── Attempt teleport ──────────────────────────────────
        boolean success = teleport(player, pos);

        if (!success) {
            player.sendSystemMessage(
                    Component.translatable("spell.zetta_spells.link_warp_points.no_target_point")
            );
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);
        if (level.getBlockEntity(blockPos) instanceof WarpPointBlockEntity warp) {
            if (!warp.hasTarget()) return;
                //    if (randomSource.nextInt(10) == 0) {}
            ChatFormatting fo = Elements.SORCERY.getColor();
            if (fo != null && fo.getColor() != null) {
                ParticleBuilder.create(EBParticles.FLASH).pos(blockPos.getCenter().add(0,0.5,0))
                        .scale(2).time(100).velocity(0, 0.01,0).color(0x7df2b4).spawn(level);
            }
        }
    }

    /**
     * Teleports an entity from the waystone at {@code departurePos} to its linked target.
     *
     * @param entity       The entity to teleport (usually a ServerPlayer)
     * @param departurePos The position of the waystone the entity is interacting with
     * @return true if the teleport succeeded, false otherwise
     */
    public static boolean teleport(LivingEntity entity, BlockPos departurePos) {
        Level level = entity.level();

        // ── 1. Client-side guard ──────────────────────────────
        if (level.isClientSide) return false;

        // ── 2. Read the departure waystone ────────────────────
        BlockEntity departureBE = level.getBlockEntity(departurePos);
        if (!(departureBE instanceof WarpPointBlockEntity departureWaystone)) {
            return false; // Not a waystone
        }
        if (!departureWaystone.hasTarget()) {
            return false; // Not linked yet
        }

        BlockPos targetPos = departureWaystone.getTargetPos();
        ResourceKey<Level> targetDim = departureWaystone.getTargetDimension();

        // ── 3. Resolve the target ServerLevel ─────────────────
        MinecraftServer server = level.getServer();
        if (server == null) return false;

        ServerLevel targetLevel = server.getLevel(targetDim);
        if (targetLevel == null) {
            return false; // Dimension doesn't exist (e.g., modded dim removed)
        }

        // ── 4. Validate the target waystone still exists ──────
        // Force-load the chunk so we can read the BlockEntity
        BlockEntity targetBE = targetLevel.getBlockEntity(targetPos);
        if (!(targetBE instanceof WarpPointBlockEntity)) {
            // The target waystone was destroyed or replaced
            departureWaystone.clearTarget(); // Clean up stale link
            return false;
        }

        // ── 5. Calculate safe arrival position ────────────────
        // Stand on top of the waystone block, centered on the block
        double arriveX = targetPos.getX() + 0.5;
        double arriveY = targetPos.getY() + 1.0;
        double arriveZ = targetPos.getZ() + 0.5;

        // ── 6. Play departure effects ─────────────────────────
        playTeleportEffects((ServerLevel) level, departurePos);

        // ── 7. Dismount if riding ─────────────────────────────
        if (entity.isPassenger()) {
            entity.stopRiding();
        }

        // ── 8. Perform the teleport ───────────────────────────
        if (entity instanceof ServerPlayer player) {
            // teleportTo handles both same-dimension and cross-dimension
            player.teleportTo(
                    targetLevel,
                    arriveX, arriveY, arriveZ,
                    player.getYRot(),   // keep facing direction
                    player.getXRot()
            );
            // Reset fall distance so the player doesn't take damage on arrival
            player.fallDistance = 0.0f;
        } else {
            // Non-player entities (e.g., mobs pushed onto a waystone)
            if (level.dimension() == targetDim) {
                // Same dimension: simple move
                entity.teleportTo(arriveX, arriveY, arriveZ);
            } else {
                // Cross-dimension: use the server's entity teleport
                entity.changeDimension(targetLevel);
                entity.teleportTo(arriveX, arriveY, arriveZ);
            }
            entity.fallDistance = 0.0f;
        }

        entity.setPortalCooldown(60);

        // ── 9. Play arrival effects ───────────────────────────
        playTeleportEffects(targetLevel, targetPos);

        return true;
    }

    /**
     * Plays sound + particle burst at the given waystone position.
     */
    private static void playTeleportEffects(ServerLevel level, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;

        // Enderman-like teleport sound
        level.playSound(
                null, x, y, z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.BLOCKS,
                1.0f, 1.0f
        );

        // Spawn portal particles in a small burst
        for (int i = 0; i < 32; i++) {
            level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.PORTAL,
                    x, y, z,
                    1,
                    (level.random.nextDouble() - 0.5) * 0.5,
                    level.random.nextDouble() * 0.5,
                    (level.random.nextDouble() - 0.5) * 0.5,
                    0.1
            );
        }
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Returns a new instance of your BlockEntity
        return new WarpPointBlockEntity(pos, state);
    }
}