package zettasword.zetta_spells.spells.sorcery;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.IStoredSpellVar;
import com.binaris.wizardry.api.content.data.StoredSpellVar;
import com.binaris.wizardry.api.content.data.VarPersistence;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.blocks.entities.WarpPointBlockEntity;

public class LinkWarpPoints extends RaySpell {

    // Variable name for storing the first warp point's location
    public static final IStoredSpellVar<String> WARP_POINT_VAR = new StoredSpellVar<>("linked_warp_point",
            Codec.STRING, VarPersistence.DIMENSION_CHANGE);

    public LinkWarpPoints() {
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos hitPos = blockHit.getBlockPos();
        if (!(ctx.caster() instanceof Player player)) return false;

        // Check if the hit block is a WarpPoint
        BlockEntity secondBE = ctx.world().getBlockEntity(hitPos);
        if (!(secondBE instanceof WarpPointBlockEntity secondWarpPoint)) {
            return false; // Not a warp point
        }

        // Only execute on server side
        if (ctx.world().isClientSide) {
            return true;
        }

        var spellManagerData = Services.OBJECT_DATA.getSpellManagerData(player);
        String storedData = spellManagerData.getVariable(WARP_POINT_VAR);

        if (storedData == null || storedData.isEmpty()) {
            // ── FIRST CAST: Store this warp point's location ─────────────
            BlockPos pos = secondWarpPoint.getBlockPos();
            ResourceKey<Level> dimension = ctx.world().dimension();

            String serialized = pos.getX() + "," + pos.getY() + "," + pos.getZ() + "," +
                    dimension.location().toString();

            spellManagerData.setVariable(WARP_POINT_VAR, serialized);

            if (ctx.caster() != null) {
                ctx.caster().sendSystemMessage(Component.translatable("spell.zetta_spells.link_warp_points.first_selected"));
            }
            return true;

        } else {
            // ── SECOND CAST: Link them bidirectionally ───────────────────
            String[] parts = storedData.split(",");
            if (parts.length != 4) {
                spellManagerData.setVariable(WARP_POINT_VAR, "");
                return false;
            }

            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                BlockPos firstPos = new BlockPos(x, y, z);
                ResourceKey<Level> firstDim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(parts[3]));

                ResourceKey<Level> secondDim = ctx.world().dimension();
                BlockPos secondPos = secondWarpPoint.getBlockPos();

                var server = ctx.world().getServer();
                if (server == null) return false;

                ServerLevel firstLevel = server.getLevel(firstDim);
                ServerLevel secondLevel = server.getLevel(secondDim);

                if (firstLevel == null || secondLevel == null) {
                    spellManagerData.setVariable(WARP_POINT_VAR, "");
                    if (ctx.caster() != null) {
                        ctx.caster().sendSystemMessage(Component.translatable("spell.zetta_spells.link_warp_points.invalid_dimension"));
                    }
                    return false;
                }

                // ── CRITICAL: Force-load the chunks so BlockEntities are guaranteed to exist ──
                firstLevel.getChunkSource().getChunk(firstPos.getX() >> 4, firstPos.getZ() >> 4, net.minecraft.world.level.chunk.ChunkStatus.FULL, true);
                secondLevel.getChunkSource().getChunk(secondPos.getX() >> 4, secondPos.getZ() >> 4, net.minecraft.world.level.chunk.ChunkStatus.FULL, true);

                // Now safely get the Block Entities
                BlockEntity loadedFirstBE = firstLevel.getBlockEntity(firstPos);
                if (!(loadedFirstBE instanceof WarpPointBlockEntity firstWarpPoint)) {
                    spellManagerData.setVariable(WARP_POINT_VAR, "");
                    if (ctx.caster() != null) {
                        ctx.caster().sendSystemMessage(Component.translatable("spell.zetta_spells.link_warp_points.no_linked_point"));
                    }
                    return false;
                }

                BlockEntity loadedSecondBE = secondLevel.getBlockEntity(secondPos);
                if (!(loadedSecondBE instanceof WarpPointBlockEntity verifiedSecondWarpPoint)) {
                    spellManagerData.setVariable(WARP_POINT_VAR, "");
                    if (ctx.caster() != null) {
                        ctx.caster().sendSystemMessage(Component.translatable("spell.zetta_spells.link_warp_points.no_target_point"));
                    }
                    return false;
                }

                // ── LINK BIDIRECTIONALLY ──
                firstWarpPoint.setTarget(secondPos, secondDim);
                verifiedSecondWarpPoint.setTarget(firstPos, firstDim);

                // Clear the variable
                spellManagerData.setVariable(WARP_POINT_VAR, "");

                if (ctx.caster() != null) {
                    ctx.caster().sendSystemMessage(Component.translatable("spell.zetta_spells.link_warp_points.success"));
                }

                return true;

            } catch (Exception e) {
                spellManagerData.setVariable(WARP_POINT_VAR, "");
                return false;
            }
        }
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE)
                .color(0x00FFFF) // Cyan color for linking
                .pos(x, y, z)
                .collide(true)
                .spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT, 75, 0, 50)
                .add(DefaultProperties.RANGE, 15F)
                .build();
    }
}