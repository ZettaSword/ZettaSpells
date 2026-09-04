package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

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
        return spawnRandomFlowers(ctx.world(), ctx.caster()) > 0;
    }

    /// This method is where you should set the default properties for your spell when creating a new spell class. This
    /// method is called in the constructor of the Spell class, and the properties returned by this method are assigned
    /// to the spell's properties field.
    ///
    /// @return A SpellProperties object with the default properties for your spell.
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.ALTERATION, SpellAction.POINT_DOWN, 100, 0, 60)
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
    public static int spawnRandomFlowers(Level level, Entity entity, int radius, float density) {
        List<Block> flowers = getFlowerPool();
        if (flowers.isEmpty()) return 0;

        BlockPos center = entity.blockPosition();
        RandomSource rng = level.random;
        int placed = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                //if (rng.nextFloat() > density) continue;

                BlockPos columnTop = center.offset(dx, 0, dz);
                BlockPos solidTop = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, columnTop);
                BlockPos placePos = solidTop.above();

                if (!level.getBlockState(placePos).isAir()) continue;

                Block pick = flowers.get(rng.nextInt(flowers.size()));
                BlockState flowerState = pick.defaultBlockState();

                if (pick instanceof TallFlowerBlock) {
                    if (!level.getBlockState(placePos.above()).isAir()) continue;
                    if (!flowerState.canSurvive(level, placePos)) continue;
                    if (!level.isClientSide)
                        DoublePlantBlock.placeAt(level, flowerState, placePos, Block.UPDATE_ALL);
                } else {
                    if (!flowerState.canSurvive(level, placePos)) continue;
                    if (!level.isClientSide)
                        level.setBlock(placePos, flowerState, Block.UPDATE_ALL);
                }
                placed++;
            }
        }
        return placed;
    }

    /** Convenience: 5×5 area with 60% density. */
    public static int spawnRandomFlowers(Level level, Entity entity) {
        return spawnRandomFlowers(level, entity, 2, 0.6f);
    }

    public static List<Block> getFlowerPool(){
        return List.of(Blocks.ROSE_BUSH, EBBlocks.CRYSTAL_FLOWER.get(), Blocks.PEONY, Blocks.CORNFLOWER, Blocks.DANDELION, Blocks.POPPY, Blocks.OXEYE_DAISY, Blocks.AZURE_BLUET, Blocks.BLUE_ORCHID);
    }
}
