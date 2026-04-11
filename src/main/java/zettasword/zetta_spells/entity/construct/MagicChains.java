package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class MagicChains extends ScaledConstructEntity {
    public MagicChains(EntityType<?> type, Level world) {
        super(type, world);
    }

    public MagicChains(Level world) {
        super(ZSEntities.MAGIC_CHAINS.get(), world);
    }
}
