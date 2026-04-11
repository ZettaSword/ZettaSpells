package zettasword.zetta_spells.mob_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ZSEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "zetta_spells");

    public static final RegistryObject<MobEffect> HEAT = EFFECTS.register("heat", HeatMobEffect::new);
    public static final RegistryObject<MobEffect> UNDEAD = EFFECTS.register("undead", UndeadMobEffect::new);
    public static final RegistryObject<MobEffect> MAGIC_RESTORATION = EFFECTS.register("magic_restoration", MagicRestorationMobEffect::new);
}
