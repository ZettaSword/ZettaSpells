package zettasword.zetta_spells.capability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.capability.spellcaster.SpellcasterData;

public class Race {
    public static @NotNull LazyOptional<RaceDataHolder> get(Player player){
       return player.getCapability(RaceDataHolder.INSTANCE);
    }

    public static @NotNull LazyOptional<SpellcasterData> getSpellcastingData(LivingEntity caster){
        return caster.getCapability(SpellcasterData.INSTANCE);
    }
}
