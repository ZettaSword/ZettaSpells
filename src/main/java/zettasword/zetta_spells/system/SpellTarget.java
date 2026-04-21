package zettasword.zetta_spells.system;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class SpellTarget {
    private Entity targetEntity = null;
    private BlockPos targetPos = null;
    private boolean isMarkedForRemoval = false;

    public SpellTarget(Entity targetEntity){
        setTarget(targetEntity);
    }

    public SpellTarget(BlockPos targetPos){
        setTarget(targetPos);
    }

    public SpellTarget(){}

    public Entity getTargetEntity(){return targetEntity;}
    public BlockPos getTargetPos(){return targetPos;}

    public void setTarget(Entity entity){
        this.targetEntity = entity;
        if (targetEntity != null) this.targetPos = entity.getOnPos();
    }

    public void setTarget(BlockPos pos){
        this.targetPos = pos;
        // Since we just raytrace the target and there is nobody.
        this.targetEntity = null;
    }

    public void setTargetPos(BlockPos pos){
        this.targetPos = pos;
    }

    public boolean isMarkedForRemoval() {
        return isMarkedForRemoval;
    }

    public void setMarkedForRemoval(boolean markedForRemoval) {
        isMarkedForRemoval = markedForRemoval;
    }
}
