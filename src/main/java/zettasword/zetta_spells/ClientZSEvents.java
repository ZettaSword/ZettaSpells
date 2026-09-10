package zettasword.zetta_spells;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.renderer.entity.MagicArrowRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.renderers.*;
import zettasword.zetta_spells.entity.renderers.living.WoodGolemRenderer;

@Mod.EventBusSubscriber(modid = ZettaSpells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientZSEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Hook up your existing renderer to the Entity Type
        event.registerEntityRenderer(ZSEntities.COSMETIC_SIGIL.get(),
                (ctx) -> new CosmeticSigilRenderer(ctx, 0.2F, false));

        event.registerEntityRenderer(ZSEntities.CUSTOM_SIGIL.get(),
                (ctx) -> new CustomSigilRenderer(ctx, 0.2F, false));

        event.registerEntityRenderer(ZSEntities.TENEBRIA_WILL_SIGIL.get(),
                (ctx) -> new ZSSigilOldRenderer(ctx, ZettaSpells.location("textures/sigils/tenebria_will.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SYSTEM_CALL.get(),
                (ctx) -> new ZSSigilOldRenderer(ctx, ZettaSpells.location("textures/sigils/system_call.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.TENEBRIA_PROTECTION_SIGIL.get(),
                (ctx) -> new ZSSigilOldRenderer(ctx, ZettaSpells.location("textures/sigils/tenebria_will.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.MAGICAL_TURRET.get(),
                (ctx) -> new MagicalTurretEntityRenderer(ctx, 0.2F, false));

        // Entities that are invisible to the player's eyes.
       // event.registerEntityRenderer(ZSEntities.GRAVITATIONAL_PULL_ENTITY.get(), ZSNothingRender::new);
        event.registerEntityRenderer(ZSEntities.MAGIC_CHAINS.get(), HelixChainRenderer::new);
        event.registerEntityRenderer(ZSEntities.DEATH_VESSEL.get(), ZSNothingRender::new);
        event.registerEntityRenderer(ZSEntities.EXPLODE_ITEM_ENTITY.get(), ItemEntityRenderer::new);

        // Block Entities


        // Sigils Cosmetic
        event.registerEntityRenderer(ZSEntities.SIGIL_MAGIC.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_arcane.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_SORCERY.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_sorcery.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_EARTH.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_earth.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_FIRE.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_fire.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_HEALING.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_healing.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_ICE.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_ice.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_LIGHTNING.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_lightning.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SIGIL_NECROMANCY.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_necromancy.png"), 0.2F, false));

        // Other Custom Sigils
        event.registerEntityRenderer(ZSEntities.STARFALL_SIGIL.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpells.location("textures/sigils/circle_sorcery.png"), 0.4F, false));

        event.registerEntityRenderer(ZSEntities.STARFLOOR.get(),
                (ctx) -> new StarFloorRenderer(ctx, ZettaSpells.location("textures/sigils/circle_sorcery.png"), 0.4F, false));


        // Living entities
        event.registerEntityRenderer(ZSEntities.WOOD_GOLEM.get(), WoodGolemRenderer::new);

        // Projectiles
        event.registerEntityRenderer(ZSEntities.ORBITING_ICE_LANCE.get(), ctx -> new MagicArrowRenderer<>(ctx, WizardryMainMod.location("textures/entity/ice_lance.png")));
        event.registerEntityRenderer(ZSEntities.FALLING_STAR.get(), ZSNothingRender::new);

    }
}