package zettasword.zetta_spells.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import zettasword.zetta_spells.entity.ZSEntities;

public class WarpPointBlockEntity extends BlockEntity {

    // Stores the target coordinates
    private BlockPos targetPos = null;
    // Stores the target dimension (e.g., Overworld, Nether)
    private ResourceKey<Level> targetDimension = null;

    public WarpPointBlockEntity(BlockPos pos, BlockState state) {
        super(ZSEntities.WARP_POINT.get(), pos, state);
    }

    // ==========================================
    // METHODS YOU REQUESTED
    // ==========================================

    /**
     * Stores the coordinates and dimension of the target block.
     */
    public void setTarget(BlockPos pos, ResourceKey<Level> dimension) {
        this.targetPos = pos;
        this.targetDimension = dimension;
        
        // Mark the block entity as "dirty" so it gets saved to disk
        this.setChanged(); 
        
        // Sync the data to the client so it knows the block is bound
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    /**
     * Checks if the target coordinates have been written/set.
     */
    public boolean hasTarget() {
        return this.targetPos != null && this.targetDimension != null;
    }

    /**
     * Clears the target coordinates.
     */
    public void clearTarget() {
        this.targetPos = null;
        this.targetDimension = null;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    // Getters for the target data
    public BlockPos getTargetPos() {
        return targetPos;
    }

    public ResourceKey<Level> getTargetDimension() {
        return targetDimension;
    }

    // ==========================================
    // NBT PERSISTENCE (Saving & Loading)
    // ==========================================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (hasTarget()) {
            tag.putInt("TargetX", targetPos.getX());
            tag.putInt("TargetY", targetPos.getY());
            tag.putInt("TargetZ", targetPos.getZ());
            tag.putString("TargetDim", targetDimension.location().toString());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("TargetX")) {
            int x = tag.getInt("TargetX");
            int y = tag.getInt("TargetY");
            int z = tag.getInt("TargetZ");
            this.targetPos = new BlockPos(x, y, z);
            
            // Reconstruct the Dimension ResourceKey from the saved string
            this.targetDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("TargetDim")));
        }
    }

    // ==========================================
    // CLIENT-SERVER SYNCING
    // ==========================================

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag); // Reuse save logic for the update tag
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag); // Reuse load logic to apply the synced data on the client
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Creates a packet to send the NBT data to the client when the block updates
        return ClientboundBlockEntityDataPacket.create(this);
    }
}