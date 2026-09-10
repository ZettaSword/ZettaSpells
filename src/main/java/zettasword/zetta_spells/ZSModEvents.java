package zettasword.zetta_spells;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.living.WoodGolem;

@Mod.EventBusSubscriber(modid = ZettaSpells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ZSModEvents {

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ZSEntities.WOOD_GOLEM.get(), WoodGolem.createAttributes().build());
    }
}
