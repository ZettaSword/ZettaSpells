package zettasword.zetta_spells.system.mixins;

import com.binaris.wizardry.content.entity.goal.MinionFollowOwnerGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinionFollowOwnerGoal.class, remap = false)
public abstract class MinionFollowOwnerGoalMixin extends Goal {

    @Final
    @Shadow
    private Mob minion;
    
    /**
     * Prevents teleportation if the minion has the "sitting" NBT tag set to true.
     */
    @Inject(
        method = "teleportToOwner",
            at = @At("HEAD"),
        cancellable = true
    )
    private void zetta_spells$preventTeleportWhenSitting(CallbackInfo ci) {
        if (this.minion.getPersistentData().getBoolean("sitting")){
            ci.cancel();
        }
    }

}