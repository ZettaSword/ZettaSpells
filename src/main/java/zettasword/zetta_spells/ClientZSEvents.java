package zettasword.zetta_spells;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.renderers.*;

@Mod.EventBusSubscriber(modid = ZettaSpellsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientZSEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Hook up your existing renderer to the Entity Type
        event.registerEntityRenderer(ZSEntities.COSMETIC_SIGIL.get(),
                (ctx) -> new CosmeticSigilRenderer(ctx, 0.2F, false));

        event.registerEntityRenderer(ZSEntities.TENEBRIA_WILL_SIGIL.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpellsMod.location("textures/sigils/tenebria_will.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.SYSTEM_CALL.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpellsMod.location("textures/sigils/system_call.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.TENEBRIA_PROTECTION_SIGIL.get(),
                (ctx) -> new ZSSigilRenderer(ctx, ZettaSpellsMod.location("textures/sigils/tenebria_will.png"), 0.2F, false));

        event.registerEntityRenderer(ZSEntities.MAGICAL_TURRET.get(),
                (ctx) -> new MagicalTurretEntityRenderer(ctx, 0.2F, false));

        // Entities that are invisible to the player's eyes.
       // event.registerEntityRenderer(ZSEntities.GRAVITATIONAL_PULL_ENTITY.get(), ZSNothingRender::new);
        event.registerEntityRenderer(ZSEntities.MAGIC_CHAINS.get(), HelixChainRenderer::new);
        event.registerEntityRenderer(ZSEntities.DEATH_VESSEL.get(), ZSNothingRender::new);

        // Block Entities

    }
}