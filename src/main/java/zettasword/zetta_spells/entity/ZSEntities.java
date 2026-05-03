package zettasword.zetta_spells.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.construct.*;

public class ZSEntities {
    // 1. Create the DeferredRegister for EntityTypes
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ZettaSpells.MODID);

    public static final RegistryObject<EntityType<TenebriaWillSigil>> TENEBRIA_WILL_SIGIL = ENTITY_TYPES.register("tenebria_will_sigil",
            () -> EntityType.Builder.<TenebriaWillSigil>of(TenebriaWillSigil::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("tenebria_will_sigil")
    );

    public static final RegistryObject<EntityType<CosmeticSigil>> COSMETIC_SIGIL = ENTITY_TYPES.register("cosmetic_sigil",
            () -> EntityType.Builder.<CosmeticSigil>of(CosmeticSigil::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("cosmetic_sigil")
    );

    public static final RegistryObject<EntityType<SystemCall>> SYSTEM_CALL = ENTITY_TYPES.register("system_call",
            () -> EntityType.Builder.<SystemCall>of(SystemCall::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("system_call")
    );

    public static final RegistryObject<EntityType<TenebriaProtectionSigil>> TENEBRIA_PROTECTION_SIGIL = ENTITY_TYPES.register("tenebria_protection_sigil",
            () -> EntityType.Builder.<TenebriaProtectionSigil>of(TenebriaProtectionSigil::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("tenebria_protection_sigil")
    );

    public static final RegistryObject<EntityType<CosmeticSigil>> NOX_SIGIL = ENTITY_TYPES.register("nox_sigil",
            () -> EntityType.Builder.<CosmeticSigil>of(CosmeticSigil::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("nox_sigil")
    );

    public static final RegistryObject<EntityType<MagicChains>> MAGIC_CHAINS = ENTITY_TYPES.register("magic_chains",
            () -> EntityType.Builder.<MagicChains>of(MagicChains::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("magic_chains")
    );

    public static final RegistryObject<EntityType<DeathVesselEntity>> DEATH_VESSEL = ENTITY_TYPES.register("death_vessel",
            () -> EntityType.Builder.<DeathVesselEntity>of(DeathVesselEntity::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("death_vessel")
    );

    public static final RegistryObject<EntityType<MagicalTurretEntity>> MAGICAL_TURRET = ENTITY_TYPES.register("magical_turret",
            () -> EntityType.Builder.<MagicalTurretEntity>of(MagicalTurretEntity::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("magical_turret")
    );

    public static final RegistryObject<EntityType<CustomSigil>> CUSTOM_SIGIL = ENTITY_TYPES.register("custom_sigil",
            () -> EntityType.Builder.<CustomSigil>of(CustomSigil::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f) // Width, Height (Adjust to fit your model)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("custom_sigil")
    );

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_E_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ZettaSpells.MODID);
}
