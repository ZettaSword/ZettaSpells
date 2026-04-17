package zettasword.zetta_spells.system.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpellsMod;

public class ZSLootFunctions {

    // ✅ Use Registries.LOOT_FUNCTION_TYPE (not ForgeRegistries)
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ZettaSpellsMod.MODID);

    // ✅ Register with Serializer instance
    public static final RegistryObject<LootItemFunctionType> SET_SPELLWORD =
            LOOT_FUNCTION_TYPES.register("set_spellword",
                    () -> new LootItemFunctionType(new SetSpellWordFunction.Serializer()));

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTION_TYPES.register(modEventBus);
    }
}