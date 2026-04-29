package zettasword.zetta_spells.blocks;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.system.Alchemy;

public class LevitationSigilBlock extends Block {

    private int amplify = 0;

    public LevitationSigilBlock(Properties properties, int amplify) {
        super(properties.noLootTable());
        this.setAmplify(amplify);
    }

    @Override
    public VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living && !living.hasEffect(MobEffects.LEVITATION)){
            if (!world.isClientSide) Alchemy.apply(living, MobEffects.LEVITATION, this.getAmplify() > 0 ? 10 : 5, this.getAmplify());
            if (world.isClientSide){
                for (int j = 0; (float) j < 20; ++j) {
                    double x = living.xo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                    double y = living.yo + (double) living.getEyeHeight() - (double) 0.5F + world.random.nextDouble();
                    double z = living.zo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                    ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z)
                            .velocity((double) 0.0F, 0.1, (double) 0.0F)
                            .color(0xFF9800)
                            .spawn(world);
                }

                living.playSound(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.5F, 1.0F);
            }
        }
        if (entity instanceof ItemEntity item && this.getAmplify() > 0){
            if (!world.isClientSide) item.addDeltaMovement(new Vec3(0, 2, 0));
        }
    }

    public int getAmplify() {
        return amplify;
    }

    public void setAmplify(int amplify) {
        this.amplify = amplify;
    }
}