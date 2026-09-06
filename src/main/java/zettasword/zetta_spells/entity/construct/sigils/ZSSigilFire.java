package zettasword.zetta_spells.entity.construct.sigils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class ZSSigilFire extends ZSSigil {
    public ZSSigilFire(EntityType<?> type, Level world) {
        super(type, world);
    }
    public ZSSigilFire(Level world) {
        super(ZSEntities.SIGIL_FIRE.get(), world);
    }
}
