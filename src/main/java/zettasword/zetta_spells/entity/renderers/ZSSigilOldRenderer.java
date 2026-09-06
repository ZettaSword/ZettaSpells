package zettasword.zetta_spells.entity.renderers;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.core.AllyDesignation;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.ZettaSpells;

public class ZSSigilOldRenderer extends EntityRenderer<MagicConstructEntity> {
    private final ResourceLocation texture;
    private final float rotationSpeed;
    private final boolean invisibleToEnemies;

    public ZSSigilOldRenderer(EntityRendererProvider.Context p_174008_, ResourceLocation texture, float rotationSpeed, boolean invisibleToEnemies) {
        super(p_174008_);
        this.texture = texture;
        this.rotationSpeed = rotationSpeed;
        this.invisibleToEnemies = invisibleToEnemies;
    }

    public void render(@NotNull MagicConstructEntity entity, float p_114486_, float partialTicks,
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

        float s = entity.getBbWidth() * smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);
        poseStack.scale(s, s, s);
        float alpha = smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);
        ZettaSpells.LOGGER.warn("ALPHA: {}",alpha);


        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

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

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicConstructEntity entity) {
        return null;
    }

    public static float smoothScaleFactor(int lifetime, int ticksExisted, float partialTicks, int startLength, int endLength) {
        float age = ticksExisted + partialTicks;
        ZettaSpells.LOGGER.warn("[Initial data]: 1) lifetime: {} ; 2) ticksExisted {} ; 3) partialTicks {} ; 4) startLength {} ; 5) endLength {} ; 6) age {}",
                lifetime, ticksExisted, partialTicks, startLength, endLength, age);

        float s;

        // 1. Handle invalid/infinite lifetime (defaults to -1 in many Minecraft classes)
        if (lifetime < 0) {
            s = Mth.clamp(age / startLength, 0.0F, 1.0F);
            ZettaSpells.LOGGER.warn("[lifetime < 0 scenario]: 1) age / startLength: {} ; 2) s: {}", age / startLength, s);
        }
        // 2. Phase 1: Fade in
        else if (age < startLength) {
            s = age / startLength;
            ZettaSpells.LOGGER.warn("[age < startLength scenario]: 1) age / startLength: {} ; 2) s: {}", age / startLength, s);
        }
        // 3. Phase 3: Fade out
        else if (age > lifetime - endLength) {
            s = (lifetime - age) / endLength;
            ZettaSpells.LOGGER.warn("[age > lifetime - endLength scenario]: 1) (lifetime - age): {} ; 2) (lifetime - age) / endLength : {} ; 3) s: {}", (lifetime - age), (lifetime - age) / endLength, s);
        }
        // 4. Phase 2: Fully visible (middle duration)
        else {
            s = 1.0F;
            ZettaSpells.LOGGER.warn("[Fully visible (middle duration) scenario]: 1) {}", s);
        }

        // Ensure strict bounds just in case
        s = Mth.clamp(s, 0.0F, 1.0F);
        ZettaSpells.LOGGER.warn("[After clamp]: 1) {}", s);

        // Apply your easing curve
        s = (float) Math.pow(s, 0.4);
        ZettaSpells.LOGGER.warn("[After easing curve]: 1) {}", s);
        return s;
    }
}
