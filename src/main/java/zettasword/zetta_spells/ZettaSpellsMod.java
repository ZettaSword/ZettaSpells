package zettasword.zetta_spells;

import com.mojang.logging.LogUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.slf4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import zettasword.zetta_spells.blocks.ZSBlocks;
import zettasword.zetta_spells.enchantments.ZSEnchantments;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.items.ZSItems;
import zettasword.zetta_spells.network.PacketHandler;
import zettasword.zetta_spells.mob_effects.ZSEffects;
import zettasword.zetta_spells.spells.ZettaSpells;
import zettasword.zetta_spells.system.commands.SpellKnowledgeCommand;
import zettasword.zetta_spells.system.loot.ZSLootFunctions;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWords;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ZettaSpellsMod.MODID)
public class ZettaSpellsMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "zetta_spells";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public ZettaSpellsMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        //BLOCKS.register(modEventBus);

        SpellWords.register(modEventBus);

        ZSEffects.EFFECTS.register(modEventBus);
        ZSEntities.ENTITY_TYPES.register(modEventBus);
        ZSEnchantments.ENCHANTMENTS.register(modEventBus);
        ZSBlocks.BLOCKS.register(modEventBus);
        // Register items
        ZSItems.ITEMS.register(modEventBus);
        ZettaSpells.SPELLS.register(modEventBus);
        //ZSLootFunctions.register(modEventBus);
        MixinBootstrap.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Registering all EBWiz Redux stuff
        ZSEvents.register();

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        event.enqueueWork(PacketHandler::register);

       // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ZSItems.WRITER_ITEM);
            event.accept(ZSItems.WRITTEN_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ZSItems.NECROMANCER_STAFF);
            event.accept(ZSItems.GOLDEN_ASTRAL_DIAMOND);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        SpellKnowledgeCommand.register(event);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
        }
    }


    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath("zetta_spells", path);
    }
}
