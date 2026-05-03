package zettasword.zetta_spells.entity.renderers;

import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.core.AllyDesignation;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.CustomSigil;

public class CustomSigilRenderer extends EntityRenderer<CustomSigil> {
    private final float rotationSpeed;
    private final boolean invisibleToEnemies;

    public CustomSigilRenderer(EntityRendererProvider.Context p_174008_, float rotationSpeed, boolean invisibleToEnemies) {
        super(p_174008_);
        this.rotationSpeed = rotationSpeed;
        this.invisibleToEnemies = invisibleToEnemies;
    }

    public void render(@NotNull CustomSigil entity, float p_114486_, float partialTicks,
                       @NotNull PoseStack poseStack, MultiBufferSource p_114489_, int p_114490_) {
        if (this.invisibleToEnemies && entity.getCaster() != Minecraft.getInstance().player) {
            LivingEntity var8 = entity.getCaster();
            if (var8 instanceof Player) {
                Player player = (Player) var8;
                if (!AllyDesignation.isPlayerAlly((Player) entity.getCaster(), player)) {
                    return;
                }
            }
        }

        // === FADE CALCULATION ===
        float alpha = 1.0F;
        if (entity.getLifetime() != -1) {
            int fadeDuration = 20; // Adjust for slower/faster fade
            int fadeStartTick = Math.max(0, entity.getLifetime() - fadeDuration);
            float progress = Math.max(0.0F, (entity.tickCount + partialTicks - fadeStartTick) / (float) fadeDuration);
            alpha = 1.0F - progress;
        }
        //ZettaSpells.LOGGER.warn("Sigil fade: tick={}, alpha={}, progress={}, lifetime={}", entity.tickCount, alpha, progress, entity.lifetime);

        if (alpha <= 0.01F) return; // Skip if fully transparent
        // === END FADE ===

        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        // Apply alpha via shader color (Minecraft 1.20.1 compatible)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        float yOffset = 0.0F;
        poseStack.translate(0.0F, yOffset, 0.0F);
        RenderSystem.setShaderTexture(0, entity.getLocation());

        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;

        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        if (this.rotationSpeed != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) entity.tickCount * this.rotationSpeed));
        }

        float s = entity.getBbWidth() * DrawingUtils.smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);
        poseStack.scale(s, s, s);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        // Render TOP face
        buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, 0.01F).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, 0.01F).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, 0.01F).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, 0.01F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        // Render BOTTOM face
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, 0.01F).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, 0.01F).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, 0.01F).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, 0.01F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();

        // Reset shader color to avoid affecting other renders
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull CustomSigil entity) {
        return null;
    }
}
