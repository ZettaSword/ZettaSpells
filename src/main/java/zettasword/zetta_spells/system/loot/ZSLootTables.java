package zettasword.zetta_spells.system.loot;

import com.binaris.wizardry.core.config.EBConfig;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import zettasword.zetta_spells.ZettaSpells;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public final class ZSLootTables {
    private static final Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
    public static final ResourceLocation DUNGEON_ADDITIONS = register("chests/dungeon_additions");

    private static final List<Pair<ResourceLocation, LootPool>> LOOT_INJECTIONS = new ArrayList<>();

    private ZSLootTables() {
    }

    private static ResourceLocation register(String location) {
        return register(ZettaSpells.location(location));
    }

    private static ResourceLocation register(ResourceLocation location) {
        if (LOOT_TABLES.add(location)) {
            return location;
        }
        throw new IllegalArgumentException(location + " is already a registered built-in loot table");
    }

    // =================
    // LOOT INJECTION - Used to add custom loot to existing loot tables
    // =================

    /**
     * You should add your injections here, not only add them to the list or just creating the members.
     */
    public static void initInjections() {
        EBConfig.LOOT_INJECTION_LOCATIONS_TO_STRUCTURES.get().forEach(
                location -> LOOT_INJECTIONS.add(Pair.of(location, createAdditivePool(DUNGEON_ADDITIONS, 1)))
        );

        LOOT_INJECTIONS.add(Pair.of(ResourceLocation.parse("chests/gameplay/fishing/junk"), createAdditivePool(DUNGEON_ADDITIONS, 4)));
        LOOT_INJECTIONS.add(Pair.of(ResourceLocation.parse("chests/gameplay/fishing/treasure"), createAdditivePool(DUNGEON_ADDITIONS, 4)));
        LOOT_INJECTIONS.add(Pair.of(ResourceLocation.parse("chests/jungle_temple_dispenser"), createAdditivePool(DUNGEON_ADDITIONS, 1)));

        // For each entity, if it's in the modifiableMobs or a hostile mob, add the loot pool
        /*
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
            ResourceLocation lootTable = entityType.getDefaultLootTable();
            ResourceLocation entityName = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            if (EBConfig.INJECT_LOOT_TO_HOSTILE_MOBS.get() && !entityType.getCategory().isFriendly()) {
                LOOT_INJECTIONS.add(Pair.of(lootTable, createAdditivePool(WizardryMainMod.location("entities/mob_additions"), 1)));
                continue;
            }

            if (EBConfig.LOOT_INJECTION_TO_MOBS.get().contains(entityName)) {
                LOOT_INJECTIONS.add(Pair.of(lootTable, createAdditivePool(WizardryMainMod.location("entities/mob_additions"), 1)));
            }
        }*/
    }

    public static void applyInjections(BiConsumer<ResourceLocation, LootPool> injector) {
        if (LOOT_INJECTIONS.isEmpty()) initInjections();
        LOOT_INJECTIONS.forEach(loot -> injector.accept(loot.getFirst(), loot.getSecond()));
    }

    private static LootPool createAdditivePool(ResourceLocation entry, int weight) {
        return LootPool.lootPool().add(LootTableReference.lootTableReference(entry).setWeight(weight).setQuality(0)).setRolls(ConstantValue.exactly(1)).setBonusRolls(UniformGenerator.between(0, 1)).build();
    }
}