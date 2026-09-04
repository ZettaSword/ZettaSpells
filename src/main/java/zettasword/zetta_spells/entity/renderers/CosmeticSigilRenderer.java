package zettasword.zetta_spells.entity.renderers;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.construct.CosmeticSigil;

public class CosmeticSigilRenderer extends EntityRenderer<CosmeticSigil> {
    private final float rotationSpeed;
    private final boolean invisibleToEnemies;

    public CosmeticSigilRenderer(EntityRendererProvider.Context context, float rotationSpeed, boolean invisibleToEnemies) {
        super(context);
        this.rotationSpeed = rotationSpeed;
        this.invisibleToEnemies = invisibleToEnemies;
    }

    @Override
    public void render(@NotNull CosmeticSigil entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

        // === FADE CALCULATION ===
        float alpha = 1.0F;
        int lifetime = entity.getLifetime(); // Use getter to ensure we get the synced default (600) if setLifetime wasn't called
        if (lifetime != -1) {
            int fadeDuration = 20;
            int fadeStartTick = Math.max(0, lifetime - fadeDuration);
            float progress = Math.max(0.0F, (entity.tickCount + partialTicks - fadeStartTick) / (float) fadeDuration);
            alpha = 1.0F - progress;
        }

        if (alpha <= 0.01F) return;

        poseStack.pushPose();

        // Force full brightness so the sigil is visible even in dark areas
        int light = LightTexture.FULL_BRIGHT;

        float yOffset = 0.0F;
        poseStack.translate(0.0F, yOffset, 0.0F);

        // Rotate to face up
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));

        // Apply rotation over time
        if (this.rotationSpeed != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) entity.tickCount * this.rotationSpeed));
        }

        // Prevent scale from becoming 0. Fallback to 0.5F if getBbWidth() is 0.
        float width = Math.max(0.5F, entity.getBbWidth());
        float s = width * ClientUtils.smoothScaleFactor(lifetime, entity.tickCount, partialTicks, 10, 10);
        poseStack.scale(s, s, s);

        // Use MultiBufferSource and RenderType for reliable, properly blended transparent rendering
        RenderType renderType = RenderType.entityTranslucent(getTextureLocation(entity));
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        // Render TOP face (Local normal pointing +Z, which becomes +Y after the -90 X rotation)
        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(0.0F, 1.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F) // <-- FIXED: Uses Matrix3f
                .endVertex();

        buffer.vertex(poseStack.last().pose(), 0.5F, -0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(1.0F, 1.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();

        buffer.vertex(poseStack.last().pose(), 0.5F, 0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(1.0F, 0.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();

        buffer.vertex(poseStack.last().pose(), -0.5F, 0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(0.0F, 0.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();


        // Render BOTTOM face (Local normal pointing -Z, flipped by the 180 X rotation)
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        buffer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(0.0F, 1.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, -1.0F) // <-- FIXED: Uses Matrix3f
                .endVertex();

        buffer.vertex(poseStack.last().pose(), 0.5F, -0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(1.0F, 1.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, -1.0F)
                .endVertex();

        buffer.vertex(poseStack.last().pose(), 0.5F, 0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(1.0F, 0.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, -1.0F)
                .endVertex();

        buffer.vertex(poseStack.last().pose(), -0.5F, 0.5F, 0.01F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(0.0F, 0.0F)
                .uv2(light)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0.0F, 0.0F, -1.0F)
                .endVertex();

        poseStack.popPose(); // Pop bottom face pose
        poseStack.popPose(); // Pop main pose
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CosmeticSigil entity) {
        return entity.getLocation();
    }
}