package zettasword.zetta_spells.entity.construct.sigils;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.custom.FallingStarEntity;

public class StarFloor extends ZSSigil {
    public StarFloor(EntityType<?> type, Level world) {
        super(type, world);
    }
    public StarFloor(Level world) {
        super(ZSEntities.STARFLOOR.get(), world);
    }
}
