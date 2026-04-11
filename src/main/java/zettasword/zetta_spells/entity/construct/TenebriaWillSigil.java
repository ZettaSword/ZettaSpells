package zettasword.zetta_spells.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

public class TenebriaWillSigil extends ScaledConstructEntity {
    public TenebriaWillSigil(EntityType<?> type, Level world) {
        super(type, world);
    }

    public TenebriaWillSigil(Level world) {
        super(ZSEntities.TENEBRIA_WILL_SIGIL.get(), world);
    }
}
