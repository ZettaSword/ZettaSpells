package zettasword.zetta_spells.entity.construct.sigils;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class ZSSigilIce extends ZSSigil {
    public ZSSigilIce(EntityType<?> type, Level world) {
        super(type, world);
    }
    public ZSSigilIce(Level world) {
        super(ZSEntities.SIGIL_ICE.get(), world);
    }
}
