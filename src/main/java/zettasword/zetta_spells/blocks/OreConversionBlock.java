package zettasword.zetta_spells.blocks;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreConversionBlock extends Block {

    public OreConversionBlock(Properties properties) {
        super(properties.noLootTable());
    }

    @Override
    public VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (entity instanceof ItemEntity itemEntity && !itemEntity.getItem().isEmpty() && !itemEntity.getTags().contains("converted")){
            ItemStack stack = itemEntity.getItem();
            ItemStack result = convertStack(itemEntity,stack);

            if (result != null){
                itemEntity.setItem(result);
                if (level.isClientSide){
                    Vec3 itemPos = itemEntity.getPosition(1.0F).relative(Direction.DOWN, 1.0D);
                    Vec3 endPos = new Vec3(itemPos.x, 255, itemPos.z);
                    ParticleBuilder.create(EBParticles.BEAM).pos(itemPos).target(endPos).time(20).spawn(level);

                    level.playLocalSound(blockPos, EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), SoundSource.BLOCKS, 0.7F, 0.7F, true);
                }
                itemEntity.addTag("converted");
                //level.destroyBlock(blockPos, false);
            }
        }
    }

    private static @Nullable ItemStack convertStack(ItemEntity itemEntity,ItemStack stack) {
        ItemStack result = null;
        int count, left;
        boolean finished = false;
        Level world = itemEntity.level();
        if (stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL){
            count = stack.getCount() / 9;
            left = stack.getCount() % 9;
            if (count > 0) {
                result = new ItemStack(Items.IRON_INGOT, count);
                spawnLeftover(itemEntity, left, world);
                finished = true;
            }
        }

        if (!finished && stack.getItem() == Items.IRON_INGOT){
            count = stack.getCount() / 6;
            left = stack.getCount() % 6;
            if (count > 0) {
                result = new ItemStack(Items.EMERALD, count);
                spawnLeftover(itemEntity, left, world);
                finished = true;
            }
        }

        if (!finished && stack.getItem() == Items.EMERALD){
            count = stack.getCount() / 24;
            left = stack.getCount() % 24;
            if (count > 0) {
                result = new ItemStack(Items.DIAMOND, count);
                spawnLeftover(itemEntity, left, world);
                finished = true;
            }
        }

        if (!finished && stack.getItem() == Items.DIAMOND){
            count = stack.getCount() / 64;
            left = stack.getCount() % 64;
            if (count > 0) {
                result = new ItemStack(EBItems.ASTRAL_DIAMOND.get(), count);
                spawnLeftover(itemEntity, left, world);
            }
        }
        return result;
    }

    private static void spawnLeftover(ItemEntity itemEntity, int left, Level world) {
        if (left > 0 && !world.isClientSide){
            ItemEntity leftover = new ItemEntity(world, itemEntity.xo, itemEntity.yo, itemEntity.zo, itemEntity.getItem());
            leftover.addTag("converted");
            ItemStack stack = leftover.getItem();
            stack.setCount(left);
            leftover.setItem(stack);
            world.addFreshEntity(leftover);
        }
    }
}