package zettasword.zetta_spells.entity.construct.sigils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class ZSSigilMahou extends ZSSigil {
    public ZSSigilMahou(EntityType<?> type, Level world) {
        super(type, world);
    }
    public ZSSigilMahou(Level world) {
        super(ZSEntities.MAHOU_SIGIL.get(), world);
    }
}
