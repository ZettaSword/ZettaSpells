package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.mixin.accessor.FallingBlockEntityAccessor;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FallingGround extends RaySpell {

    public FallingGround(){

    }

    /// Called when the ray hits an entity. Override this method to perform an action when the ray hits an entity, such as
    /// dealing damage or applying a status effect. Return true if the spell should be considered successfully cast
    /// when it hits an entity, or false if the spell should not be cast when it hits an entity (e.g. if you want to prevent
    /// casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
    ///
    /// @param ctx       The cast context of the spell.
    /// @param entityHit The result of the entity hit, containing information about the hit entity and hit position.
    /// @param origin    The starting point of the ray.
    /// @return true if the spell should be considered successfully cast when it hits an entity, false if the spell should
    /// not be cast when it hits an entity.
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        Level w = ctx.world();
        Entity t = entityHit.getEntity();
        if (!w.isClientSide()) {
            List<BlockPos> sphere = BlockUtil.getBlockSphere(t.blockPosition(), 4);
            sphere.forEach(pos -> {
                FallingBlockEntity blockEntity = FallingBlockEntityAccessor.createFallingBlockEntity(w, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Blocks.DIRT.defaultBlockState());
                blockEntity.setDeltaMovement(blockEntity.getDeltaMovement().x, 0.3 * (4 - (t.blockPosition().getY() - pos.getY())), blockEntity.getDeltaMovement().z);
                w.addFreshEntity(blockEntity);
            });
        }
        return true;
    }

    /// Called when the ray hits a block. Override this method to perform an action when the ray hits a block, such as
    /// creating an explosion or spawning particles. Return true if the spell should be considered successfully cast
    /// when it hits a block, or false if the spell should not be cast when it hits a block (e.g. if you want to prevent
    /// casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
    ///
    /// @param ctx      The cast context of the spell.
    /// @param blockHit The result of the block hit, containing information about the hit position and block.
    /// @param origin   The starting point of the ray.
    /// @return true if the spell should be considered successfully cast when it hits a block, false if the spell should
    /// not be cast when it hits a block.
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        Level w = ctx.world();
        if (!w.isClientSide()) {
            List<BlockPos> sphere = BlockUtil.getBlockSphere(blockHit.getBlockPos(), 4);
            sphere.forEach(pos -> {
                FallingBlockEntity blockEntity = FallingBlockEntityAccessor.createFallingBlockEntity(w, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Blocks.DIRT.defaultBlockState());
                blockEntity.setDeltaMovement(blockEntity.getDeltaMovement().x, 0.3 * (4 - (blockHit.getBlockPos().getY() - pos.getY())), blockEntity.getDeltaMovement().z);
                w.addFreshEntity(blockEntity);
            });
        }
        return true;
    }

    /// Called when the ray does not hit any entities or blocks. Override this method to perform an action when the ray
    /// misses, such as spawning particles at the endpoint. Return true if the spell should be considered successfully
    /// cast even when it misses, or false if the spell should not be cast when it misses (e.g. if you want to prevent
    /// casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
    ///
    /// @param ctx       The cast context of the spell.
    /// @param origin    The starting point of the ray.
    /// @param direction The normalized direction vector of the ray.
    /// @return true if the spell should be considered successfully cast even when it misses, false if the spell should
    /// not be cast when it misses.
    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    /// This method is where you should set the default properties for your spell when creating a new spell class. This
    /// method is called in the constructor of the Spell class, and the properties returned by this method are assigned
    /// to the spell's properties field.
    ///
    /// @return A SpellProperties object with the default properties for your spell.
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellTypes.ALTERATION, SpellAction.POINT_UP,
                1000, 60, 60).build();
    }
}
