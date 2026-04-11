package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class SystemCall extends ScaledConstructEntity {
    public SystemCall(EntityType<?> type, Level world) {
        super(type, world);
    }

    public SystemCall(Level world) {
        super(ZSEntities.SYSTEM_CALL.get(), world);
    }
}
