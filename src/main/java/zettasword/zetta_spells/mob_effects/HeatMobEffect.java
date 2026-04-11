package zettasword.zetta_spells.mob_effects;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

public class HeatMobEffect extends MagicMobEffect {
    public HeatMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0xC9562B);
    }


    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide && livingEntity.onGround()) {
            Level level = livingEntity.level();
            BlockPos entityPos = livingEntity.blockPosition();
            FireBlock fireBlock = (FireBlock) Blocks.FIRE;
            int radius = Math.min(8, 2 + amplifier); // Reduced radius to prevent excessive fire
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            // Iterate through a square area around the entity
            for (BlockPos blockPos : BlockPos.betweenClosed(
                    entityPos.offset(-radius, -1, -radius),
                    entityPos.offset(radius, -1, radius))) {

                // Check if the block is within circular range of the entity
                if (blockPos.closerThan(livingEntity.blockPosition(), radius)) {
                    // Check the block above this position
                    mutablePos.set(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
                    BlockState aboveBlockState = level.getBlockState(mutablePos);

                    // If the block above is air, we can potentially place fire there
                    if (aboveBlockState.isAir()) {
                        BlockState currentBlockState = level.getBlockState(blockPos);

                        // Check if fire can survive on the current block
                        if (fireBlock.canSurvive(fireBlock.defaultBlockState(), level, mutablePos) &&
                                level.isUnobstructed(fireBlock.defaultBlockState(), mutablePos, CollisionContext.empty()) &&
                                !ForgeEventFactory.onBlockPlace(livingEntity,
                                        BlockSnapshot.create(level.dimension(), level, mutablePos),
                                        Direction.UP)) {

                            // Place fire on top of the block
                            level.setBlockAndUpdate(mutablePos, fireBlock.defaultBlockState());

                            // Schedule a tick for the fire block to handle natural fire behavior
                            level.scheduleTick(mutablePos, Blocks.FIRE,
                                    Mth.nextInt(livingEntity.getRandom(), 30, 60));
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Apply effect every 20 ticks (1 second)
        return duration % 10 == 0;
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(15 + world.random.nextInt(5)).spawn(world);
    }
}
