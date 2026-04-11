package zettasword.zetta_spells.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpellsMod;

public class ZSEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ZettaSpellsMod.MODID);

    // Register the "Enhance Armament" Enchantment
    // It is set as Treasure Only so it can't be gotten via Enchanting Table (fits SAO lore)
    public static final RegistryObject<Enchantment> ENHANCE_ARMAMENT = ENCHANTMENTS.register("enhance_armament",
            () -> new EnhanceArmamentEnchantment(
                    Enchantment.Rarity.RARE,
                    EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS}
            ));

    public static final RegistryObject<Enchantment> RELEASE_RECOLLECTION = ENCHANTMENTS.register("release_recollection",
            () -> new ReleaseRecollectionEnchantment(
                    Enchantment.Rarity.VERY_RARE,
                    net.minecraft.world.item.enchantment.EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS}
            ));

    public static final RegistryObject<Enchantment> FROST_ASPECT = ENCHANTMENTS.register("frost_aspect",
            () -> new FrostAspectEnchantment(
                    Enchantment.Rarity.RARE,
                    net.minecraft.world.item.enchantment.EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS}
            ));
}
