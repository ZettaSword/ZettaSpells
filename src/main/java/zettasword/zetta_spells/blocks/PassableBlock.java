package zettasword.zetta_spells.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;

public class PassableBlock extends Block {

    // Define an empty shape for collision purposes
    private static final VoxelShape EMPTY_SHAPE = Shapes.empty();
    // Define a voxel shape that is 50% taller in the Y-axis
    private static final VoxelShape BARRIER_SHAPE = Shapes.box(
            0.0, 0.0, 0.0, // Minimum bounds (X, Y, Z)
            1.0, 1.5, 1.0  // Maximum bounds (X, Y, Z)
    );


    public PassableBlock(Properties properties) {
        super(properties.noLootTable());
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof Player || !(entity instanceof Enemy)) {
                return EMPTY_SHAPE; // Players can pass through
            }
        }
        return BARRIER_SHAPE; // Mobs can't
    }

    @Override
    public VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

}