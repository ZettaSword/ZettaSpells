package zettasword.zetta_spells.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class Race {
    public static @NotNull LazyOptional<RaceDataHolder> get(Player player){
       return player.getCapability(RaceDataHolder.INSTANCE);
    }
}
