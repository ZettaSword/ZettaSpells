package zettasword.zetta_spells.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.blocks.WarpPointBlock;
import zettasword.zetta_spells.blocks.ZSBlocks;
import zettasword.zetta_spells.blocks.entities.WarpPointBlockEntity;
import zettasword.zetta_spells.entity.construct.*;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigilFire;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigilMahou;
import zettasword.zetta_spells.entity.custom.ExplodeItemEntity;
import zettasword.zetta_spells.entity.custom.FallingStarEntity;
import zettasword.zetta_spells.entity.living.WoodGolem;
import zettasword.zetta_spells.entity.projectiles.OrbitingIceLanceEntity;

public class ZSEntities {
    // Sigils
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ZettaSpells.MODID);

    public static final RegistryObject<EntityType<TenebriaWillSigil>> TENEBRIA_WILL_SIGIL = ENTITY_TYPES.register("tenebria_will_sigil",
            () -> EntityType.Builder.<TenebriaWillSigil>of(TenebriaWillSigil::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("tenebria_will_sigil")
    );

    public static final RegistryObject<EntityType<CosmeticSigil>> COSMETIC_SIGIL = ENTITY_TYPES.register("cosmetic_sigil",
            () -> EntityType.Builder.<CosmeticSigil>of(CosmeticSigil::new, MobCategory.MISC)
                    .sized(0.5F, 0.1F)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("cosmetic_sigil")
    );

    public static final RegistryObject<EntityType<SystemCall>> SYSTEM_CALL = ENTITY_TYPES.register("system_call",
            () -> EntityType.Builder.<SystemCall>of(SystemCall::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("system_call")
    );

    public static final RegistryObject<EntityType<TenebriaProtectionSigil>> TENEBRIA_PROTECTION_SIGIL = ENTITY_TYPES.register("tenebria_protection_sigil",
            () -> EntityType.Builder.<TenebriaProtectionSigil>of(TenebriaProtectionSigil::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("tenebria_protection_sigil")
    );

    public static final RegistryObject<EntityType<CosmeticSigil>> NOX_SIGIL = ENTITY_TYPES.register("nox_sigil",
            () -> EntityType.Builder.<CosmeticSigil>of(CosmeticSigil::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("nox_sigil")
    );

    public static final RegistryObject<EntityType<MagicChains>> MAGIC_CHAINS = ENTITY_TYPES.register("magic_chains",
            () -> EntityType.Builder.<MagicChains>of(MagicChains::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("magic_chains")
    );

    public static final RegistryObject<EntityType<DeathVesselEntity>> DEATH_VESSEL = ENTITY_TYPES.register("death_vessel",
            () -> EntityType.Builder.<DeathVesselEntity>of(DeathVesselEntity::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("death_vessel")
    );

    public static final RegistryObject<EntityType<MagicalTurretEntity>> MAGICAL_TURRET = ENTITY_TYPES.register("magical_turret",
            () -> EntityType.Builder.<MagicalTurretEntity>of(MagicalTurretEntity::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("magical_turret")
    );

    public static final RegistryObject<EntityType<CustomSigil>> CUSTOM_SIGIL = ENTITY_TYPES.register("custom_sigil",
            () -> EntityType.Builder.<CustomSigil>of(CustomSigil::new, MobCategory.MISC)
                    .sized(10.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("custom_sigil")
    );

    public static final RegistryObject<EntityType<ExplodeItemEntity>> EXPLODE_ITEM_ENTITY = ENTITY_TYPES.register("explode_item_entity",
            () -> EntityType.Builder.<ExplodeItemEntity>of(ExplodeItemEntity::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("explode_item_entity")
    );

    // Sigils for cosmetic.
    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_MAGIC = ENTITY_TYPES.register("sigil_magic",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_magic")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_SORCERY = ENTITY_TYPES.register("sigil_sorcery",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_sorcery")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_FIRE = ENTITY_TYPES.register("sigil_fire",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_fire")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_ICE = ENTITY_TYPES.register("sigil_ice",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_ice")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_EARTH = ENTITY_TYPES.register("sigil_earth",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_earth")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_LIGHTNING = ENTITY_TYPES.register("sigil_lightning",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_lightning")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_NECROMANCY = ENTITY_TYPES.register("sigil_necromancy",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_necromancy")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> SIGIL_HEALING = ENTITY_TYPES.register("sigil_healing",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("sigil_healing")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> STARFALL_SIGIL = ENTITY_TYPES.register("starfall_sigil",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("starfall_sigil")
    );

    public static final RegistryObject<EntityType<ZSSigilMahou>> MAHOU_SIGIL = ENTITY_TYPES.register("mahou_sigil",
            () -> EntityType.Builder.<ZSSigilMahou>of(ZSSigilMahou::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("mahou_sigil")
    );

    public static final RegistryObject<EntityType<ZSSigilFire>> STARFLOOR = ENTITY_TYPES.register("starfloor",
            () -> EntityType.Builder.<ZSSigilFire>of(ZSSigilFire::new, MobCategory.MISC)
                    .sized(2.0f, 0.5f)
                    .clientTrackingRange(160)
                    .updateInterval(10)
                    .build("starfloor")
    );

    // Entity Living
    public static final RegistryObject<EntityType<WoodGolem>> WOOD_GOLEM = ENTITY_TYPES.register("wood_golem",
            ()-> EntityType.Builder.<WoodGolem>of(WoodGolem::new, MobCategory.CREATURE)
                    .sized(1.4f, 2.9f)
                    .clientTrackingRange(125)
                    .updateInterval(10)
                    .build("wood_golem"));

    // Projectiles.
    public static final RegistryObject<EntityType<OrbitingIceLanceEntity>> ORBITING_ICE_LANCE = ENTITY_TYPES.register("orbiting_ice_lance",
            ()-> EntityType.Builder.<OrbitingIceLanceEntity>of(OrbitingIceLanceEntity::new, MobCategory.MISC)
                    .sized(1,1)
                    .clientTrackingRange(125)
                    .updateInterval(10)
                    .build("orbiting_ice_lance"));

    public static final RegistryObject<EntityType<FallingStarEntity>> FALLING_STAR = ENTITY_TYPES.register("falling_star",
            ()-> EntityType.Builder.<FallingStarEntity>of(FallingStarEntity::new, MobCategory.MISC)
                    .sized(2.0F,2.0F)
                    .clientTrackingRange(125)
                    .updateInterval(10)
                    .build("falling_star"));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_E_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ZettaSpells.MODID);

    public static final RegistryObject<BlockEntityType<WarpPointBlockEntity>> WARP_POINT = BLOCK_E_TYPES.register("warp_point",
            () -> BlockEntityType.Builder.of(WarpPointBlockEntity::new, ZSBlocks.WARP_POINT.get()).build(null));


}
