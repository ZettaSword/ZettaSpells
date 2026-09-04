package zettasword.zetta_spells.entity.construct.sigils;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class ZSSigilHealing extends ZSSigil {
    public ZSSigilHealing(EntityType<?> type, Level world) {
        super(type, world);
    }
    public ZSSigilHealing(Level world) {
        super(ZSEntities.SIGIL_HEALING.get(), world);
    }
}
