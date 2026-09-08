package zettasword.zetta_spells.entity.renderers;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.core.AllyDesignation;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.sigils.ZSSigil;

public class StarFloorRenderer extends EntityRenderer<ZSSigil> {
    private final ResourceLocation texture;
    private final float rotationSpeed;
    private final boolean invisibleToEnemies;

    public StarFloorRenderer(EntityRendererProvider.Context p_174008_, ResourceLocation texture, float rotationSpeed, boolean invisibleToEnemies) {
        super(p_174008_);
        this.texture = texture;
        this.rotationSpeed = rotationSpeed;
        this.invisibleToEnemies = invisibleToEnemies;
    }

    public void render(@NotNull ZSSigil entity, float p_114486_, float partialTicks,
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

        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        float yOffset = 0.0F;
        poseStack.translate(0.0F, yOffset, 0.0F);
        RenderSystem.setShaderTexture(0, this.texture);

        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;

        // Rotate to face upward
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        if (this.rotationSpeed != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) entity.tickCount * this.rotationSpeed));
        }

        float s = entity.getBbWidth() * ClientUtils.smoothScaleFactor(entity.getLifetime(), entity.tickCount, partialTicks, 10, 10);
        poseStack.scale(s, s, s);
        float alpha = ClientUtils.smoothScaleFactor(entity.getLifetime(), entity.tickCount, partialTicks, 10, 10);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        // Render TOP face
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, 0.01F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, 0.01F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, 0.01F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, 0.01F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        // Render BOTTOM face (rotate 180° around X axis)
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, 0.01F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, 0.01F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, 0.01F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, 0.01F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();

        // ===== STAR LAYER (End Portal Style) =====
        renderStarLayer(poseStack, entity, partialTicks, f6, f7, f8, s, 0.5F);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderStarLayer(PoseStack poseStack, ZSSigil entity, float partialTicks, 
                                  float f6, float f7, float f8, float scale, float alpha) {
        poseStack.pushPose();
        
        // Slightly larger than main sigil
        float starScale = scale * 0.1F;
        poseStack.scale(starScale, starScale, starScale);
        
        // Calculate animation time
        float totalTime = entity.tickCount + partialTicks;
        
        // Render multiple star layers with different speeds (like End Portal)
        // Layer 1
        renderStarQuad(poseStack, f6, f7, f8, alpha * 0.5F, totalTime * 0.15F, 0F);
        // Layer 2 (faster rotation)
        renderStarQuad(poseStack, f6, f7, f8, alpha * 0.3F, totalTime * 0.25F, 0F);
        // Layer 3 (even faster)
        renderStarQuad(poseStack, f6, f7, f8, alpha * 0.2F, totalTime * 0.35F, 0F);
        
        poseStack.popPose();
    }

    private void renderStarQuad(PoseStack poseStack, float f6, float f7, float f8, 
                                 float alpha, float rotation, float zOffset) {
        poseStack.pushPose();
        
        // Rotate the star layer
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        
        // Use additive blending for glowing effect
        RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TheEndPortalRenderer.END_PORTAL_LOCATION);
        
        // Calculate UV offset for animated scrolling effect
        float uOffset = (rotation * 0.01F) % 1.0F;
        float vOffset = (rotation * 0.007F) % 1.0F;
        
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        
        // Render quad with animated UVs
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, zOffset)
              .uv(0.0F + uOffset, 1.0F + vOffset)
              .color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, zOffset)
              .uv(1.0F + uOffset, 1.0F + vOffset)
              .color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, zOffset)
              .uv(1.0F + uOffset, 0.0F + vOffset)
              .color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, zOffset)
              .uv(0.0F + uOffset, 0.0F + vOffset)
              .color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        
        BufferUploader.drawWithShader(buffer.end());
        
        // Restore normal blending
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        
        poseStack.popPose();
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull ZSSigil entity) {
        return null;
    }
}