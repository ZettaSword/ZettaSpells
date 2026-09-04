package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.ZSConfig;
import zettasword.zetta_spells.system.particles.Alteria;

import java.util.Collections;
import java.util.List;

public class FlowerField extends Spell {
    public FlowerField() {
        super();
    }

    /// This cast method is meant to be used for spells that are cast by a player source. This is useful for spells that
    /// are meant to be cast by players, as it provides more information about the caster and the context of the cast.
    ///
    /// Override this method to implement the casting behavior for spells that are meant to be cast by players.
    ///
    /// @param ctx The context of the spell cast, containing information about the world, caster, hand used, modifiers, etc.
    /// @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
    /// considered as having been cast, so no cooldown will be applied.
    @Override
    public boolean cast(PlayerCastContext ctx) {
        BlockPos center = ctx.caster().blockPosition();
        Level level = ctx.world();
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos.relative(Direction.UP)) == Blocks.AIR.defaultBlockState()) {
                        if (!level.isClientSide) {
                            List<Block> flowers = new java.util.ArrayList<>(getFlowerPool());
                            Collections.shuffle(flowers);
                            Block pick = flowers.get(0);
                            BlockState flowerState = pick.defaultBlockState();
                            BlockPos placePos = pos.above();

                            if (pick instanceof TallFlowerBlock) {
                                if (!level.getBlockState(placePos.above()).isAir()) continue;
                                if (!flowerState.canSurvive(level, placePos)) continue;
                                DoublePlantBlock.placeAt(level, flowerState, placePos, Block.UPDATE_ALL);
                            } else {
                                if (!flowerState.canSurvive(level, placePos)) continue;
                                ctx.world().setBlockAndUpdate(placePos, flowerState);
                            }
                        }
                        if (level.isClientSide){
                            if (level.random.nextFloat() <= 0.2) {
                                ParticleBuilder.create(EBParticles.SPARKLE).pos(pos.above()).gravity(true).color(0.3f, 0.7f, 0).spawn(ctx.world());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /// This method is where you should set the default properties for your spell when creating a new spell class. This
    /// method is called in the constructor of the Spell class, and the properties returned by this method are assigned
    /// to the spell's properties field.
    ///
    /// @return A SpellProperties object with the default properties for your spell.
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.ALTERATION, SpellAction.POINT_DOWN, 200, 0, 60)
                .build();
    }

    /**
     * Spawns random flowers in a square area around the given entity.
     *
     * @param level    the world
     * @param entity   the entity to center the area on
     * @param radius   half-width of the area (radius = 2 → 5×5)
     * @param density  chance (0.0–1.0) that any given column actually gets a flower
     * @return the number of flowers placed
     */
    public static boolean spawnRandomFlowers(Level level, Entity entity, int radius, float density) {
        List<Block> flowers = getFlowerPool();
        if (flowers.isEmpty()) return false;

        if (level.isClientSide){
            Alteria.spawnBlockOutlineParticles(level, entity.blockPosition().above(), ParticleTypes.END_ROD, 20);
        }

        BlockPos center = entity.blockPosition();
        RandomSource rng = level.random;
        int placed = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                //if (rng.nextFloat() > density) continue;

                BlockPos columnTop = center.offset(dx, 0, dz);
                BlockPos solidTop = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, columnTop);
                BlockPos placePos = solidTop.above();

                if (level.isClientSide){
                    Alteria.spawnBlockOutlineParticles(level, placePos, ParticleTypes.END_ROD, 20);
                }

                if (!level.getBlockState(placePos).isAir()) continue;

                Block pick = flowers.get(rng.nextInt(flowers.size()));
                BlockState flowerState = pick.defaultBlockState();

                if (pick instanceof TallFlowerBlock) {
                    if (!level.getBlockState(placePos.above()).isAir()) continue;
                    if (!flowerState.canSurvive(level, placePos)) continue;
                    if (!level.isClientSide)
                        DoublePlantBlock.placeAt(level, flowerState, placePos, Block.UPDATE_ALL);
                    if (level.isClientSide){
                        Alteria.spawnBlockOutlineParticles(level, placePos, ParticleTypes.END_ROD, 10);
                        Alteria.spawnBlockOutlineParticles(level, placePos.above(), ParticleTypes.END_ROD, 10);
                    }
                } else {
                    if (!flowerState.canSurvive(level, placePos)) continue;
                    if (!level.isClientSide)
                        level.setBlock(placePos, flowerState, Block.UPDATE_ALL);
                    if (level.isClientSide){
                        Alteria.spawnBlockOutlineParticles(level, placePos, ParticleTypes.END_ROD, 10);
                    }
                }
                placed++;
            }
        }
        return placed > 0;
    }

    /** Convenience: 5×5 area with 60% density. */
    public static boolean spawnRandomFlowers(Level level, Entity entity) {
        return spawnRandomFlowers(level, entity, 2, 0.6f);
    }

    public static List<Block> getFlowerPool(){
        return List.of(EBBlocks.CRYSTAL_FLOWER.get(), Blocks.CORNFLOWER, Blocks.DANDELION, Blocks.POPPY, Blocks.OXEYE_DAISY, Blocks.AZURE_BLUET, Blocks.BLUE_ORCHID);
    }
}
