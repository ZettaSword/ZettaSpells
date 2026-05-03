package zettasword.zetta_spells.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.blocks.ZSBlocks;
import zettasword.zetta_spells.items.spellbook.UnfinishedSpellbookItem;
import zettasword.zetta_spells.items.spellbook.FinishedSpellbookItem;

public class ZSItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, ZettaSpells.MODID);

    public static final RegistryObject<Item> UNFINISHED_SPELLBOOK = ITEMS.register(
        "unfinished_spellbook",
        () -> new UnfinishedSpellbookItem(new Item.Properties().stacksTo(1))
    );
    
    public static final RegistryObject<Item> FINISHED_SPELLBOOK = ITEMS.register(
        "finished_spellbook",
        () -> new FinishedSpellbookItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> GOLDEN_ASTRAL_DIAMOND = ITEMS.register("golden_astral_diamond", GoldenAsDiamondItem::new);
    public static final RegistryObject<Item> NECROMANCER_STAFF = ITEMS.register("necromancer_staff", NecromancerStaffItem::new);
    public static final RegistryObject<Item> SPELLWORD_ITEM = ITEMS.register("spellword_item", SpellWordItem::new);
    public static final RegistryObject<Item> LANTERN_OF_KNOWLEDGE = ITEMS.register("lantern_of_knowledge", LanternOfKnowledge::new);
   //public static final RegistryObject<Item> CUSTOM_SPELLBOOK = ITEMS.register("custom_spellbook", CustomSpellBook::new);

    // Block Items

    public static final RegistryObject<Item> PASSABLE_BLOCK_ITEM = ITEMS.register("passable_block",
            () -> new BlockItem(ZSBlocks.PASSABLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> WHITE_BLOCK_ITEM = ITEMS.register("white_block",
            () -> new BlockItem(ZSBlocks.WHITE_BLOCK.get(), new Item.Properties()));
}