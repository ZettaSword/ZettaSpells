package zettasword.zetta_spells.entity.renderers;

import com.binaris.wizardry.api.content.entity.living.ISpellCaster;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.Spells;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.binaris.wizardry.api.content.spell.Spell;
import zettasword.zetta_spells.ZSConfig;
import zettasword.zetta_spells.ZettaSpells;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = ZettaSpells.MODID, value = Dist.CLIENT)
public class RenderCircles {

    public static final HashMap<Element , ResourceLocation> TEXTURES = new HashMap<>();

    public static void register(){
        TEXTURES.put(Elements.MAGIC, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_arcane.png"));
        TEXTURES.put(Elements.EARTH, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_earth.png"));
        TEXTURES.put(Elements.FIRE, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_fire.png"));
        TEXTURES.put(Elements.HEALING, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_healing.png"));
        TEXTURES.put(Elements.ICE, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_ice.png"));
        TEXTURES.put(Elements.LIGHTNING, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_lightning.png"));
        TEXTURES.put(Elements.NECROMANCY, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_necromancy.png"));
        TEXTURES.put(Elements.SORCERY, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/sigils/old/circle_sorcery.png"));
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (!ZSConfig.circlesWhenCastingContinuous) return;
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Spell spell = getCasting(player);
        if (spell == null || spell.isInstantCast()) return;

        Element element = spell.getElement();
        if (element == null) element = Elements.MAGIC;

        ResourceLocation texture = TEXTURES.get(element);
        if (texture == null) texture = TEXTURES.get(Elements.MAGIC);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // 2. Orient the circle to face the direction the player's head is looking
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        // 1. Rotate around Y (Yaw) - inverted for world-to-camera space
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        // 2. Rotate around X (Pitch)
        poseStack.mulPose(Axis.XP.rotationDegrees(-camera.getXRot()));
        //poseStack.mulPose(Axis.YP.rotationDegrees(-player.yHeadRot));

        // In camera space, -Z is forward.
        poseStack.translate(0.0F, 0.0F, -1.2F);

        // Rotate around Z axis (camera's forward direction) to make it spin
        float rotation = (float) (System.currentTimeMillis() % 3600) / 10.0F;
        poseStack.mulPose(Axis.ZP.rotationDegrees(-rotation));

        float scale = 0.6F;
        poseStack.scale(scale, scale, scale);

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false); // CRITICAL: Prevents the player's hand from occluding the circle
        RenderSystem.disableCull();    // CRITICAL: Makes the quad double-sided without drawing it twice
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        float alpha = 0.5F;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(poseStack.last().pose(), -0.5F,  0.5F, 0.0F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F,  0.5F, 0.0F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F, -0.5F, 0.0F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0.0F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        // Render Back face
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(poseStack.last().pose(), -0.5F,  0.5F, 0.01F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F,  0.5F, 0.01F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F, -0.5F, 0.01F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0.01F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();

        // Restore render states to prevent affecting other renders
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    // ==========================================
    // Third Person Rendering (Orbits with Head)
    // ==========================================
    @SubscribeEvent
    public static void onRenderPlayerEvent(RenderPlayerEvent.Post event) {
        if (!ZSConfig.circlesWhenCastingContinuous) return;

        Player player = event.getEntity();
        Spell spell = getCasting(player);
        if (spell == null || spell.isInstantCast()) return;

        Element element = spell.getElement();
        if (element == null) element = Elements.MAGIC;

        ResourceLocation texture = TEXTURES.get(element);
        if (texture == null) texture = TEXTURES.get(Elements.MAGIC);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // 1. Calculate 3D orbital position based on player's head rotation (Yaw AND Pitch)
        float yawRad = (float) Math.toRadians(player.yHeadRot);
        float pitchRad = (float) Math.toRadians(player.getXRot());
        double radius = 1.5;

        // Spherical coordinates to keep the circle at a fixed distance in front of the player's face
        double targetX = -Math.sin(yawRad) * Math.cos(pitchRad) * radius;
        double targetY = 1.4 - Math.sin(pitchRad) * radius; // Adjusts height based on looking up/down
        double targetZ =  Math.cos(yawRad) * Math.cos(pitchRad) * radius;

        poseStack.translate(targetX, targetY, targetZ);

        // 2. Orient the circle to face the direction the player's head is looking

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-player.yHeadRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-player.getXRot()));

        // 3. Apply magical spin around the axis pointing at the viewer (Z-axis)
        poseStack.mulPose(Axis.ZP.rotationDegrees(player.tickCount * -2.0F));
        poseStack.scale(0.8F, 0.8F, 0.8F);

        // 4. Setup Render System
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        float alpha = 1.0F;

        // Render Front face
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(poseStack.last().pose(), -0.5F,  0.5F, 0.01F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F,  0.5F, 0.01F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F, -0.5F, 0.01F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0.01F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        // Render Back face
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(poseStack.last().pose(), -0.5F,  0.5F, 0.01F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F,  0.5F, 0.01F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(),  0.5F, -0.5F, 0.01F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0.01F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();

        RenderSystem.disableBlend();
        poseStack.popPose();
    }



    public static Spell getCasting(LivingEntity caster) {
        if (caster instanceof Player player) {
            if (player.isUsingItem()) {
                ItemStack stack = player.getUseItem();
                if (stack.getItem() instanceof ICastItem castingItem) {
                    Spell spell = castingItem.getCurrentSpell(stack);
                    if (spell != Spells.NONE && player.getUseItemRemainingTicks() > spell.getChargeUp()) {
                        return spell;
                    }
                }
            }
        } else if (caster instanceof ISpellCaster spellCaster) {
            if (spellCaster.getContinuousSpell() != Spells.NONE) {
                return spellCaster.getContinuousSpell();
            }
        }
        return Spells.NONE;
    }
}