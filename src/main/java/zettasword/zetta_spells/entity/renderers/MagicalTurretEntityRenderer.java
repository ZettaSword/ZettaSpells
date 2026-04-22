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
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.construct.MagicalTurretEntity;

public class MagicalTurretEntityRenderer extends EntityRenderer<MagicalTurretEntity> {
    private final float rotationSpeed;
    private final boolean invisibleToEnemies;

    public MagicalTurretEntityRenderer(EntityRendererProvider.Context p_174008_, float rotationSpeed, boolean invisibleToEnemies) {
        super(p_174008_);
        this.rotationSpeed = rotationSpeed;
        this.invisibleToEnemies = invisibleToEnemies;
    }

    @Override
    public void render(@NotNull MagicalTurretEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

        // ========== VISIBILITY CHECK ==========
        if (this.invisibleToEnemies && entity.getCaster() != Minecraft.getInstance().player) {
            LivingEntity caster = entity.getCaster();
            if (caster instanceof Player player) {
                Player viewer = Minecraft.getInstance().player;
                if (viewer != null && !AllyDesignation.isPlayerAlly(player, viewer)) {
                    return;
                }
            }
        }

        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        // Position at entity center
        poseStack.translate(0.0F, 0.0F, 0.0F);
        RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/block/passable_block.png"));

        // Common rendering params
        float f6 = 1.0F;  // width
        float f7 = 0.5F;  // offset X
        float f8 = 0.5F;  // offset Y
        float s = entity.getBbWidth() * DrawingUtils.smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        // ========== HORIZONTAL CIRCLE (floor rune) ==========
        poseStack.pushPose();
        poseStack.scale(s, s, s);

        // Rotate to lay flat on XZ plane
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        if (this.rotationSpeed != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) entity.tickCount * this.rotationSpeed));
        }
        drawDoubleSidedQuad(poseStack, buffer, f7, f8, f6);
        poseStack.popPose();

        // ========== VERTICAL CIRCLE (facing turret direction) ==========
        poseStack.pushPose();
        poseStack.scale(s * 0.2F, s * 0.2F, s * 0.2F);
        poseStack.translate(0.0F, 0.2F, 0.0F);

        // Rotate to face the turret's current yaw direction
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));

        // Optional: slight tilt forward for better visibility
        poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));

        // Optional: subtle bobbing animation
        //float bob = 0.02F * (float) Math.sin(entity.tickCount * 0.3F + partialTicks);
        //poseStack.translate(0.0F, bob, 0.0F);

        drawDoubleSidedQuad(poseStack, buffer, f7, f8, f6);
        poseStack.popPose();

        RenderSystem.disableBlend();
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    /**
     * Draws a double-sided textured quad (front + back face) for proper visibility from all angles.
     * Quad is centered at origin, sized by f6, with offsets f7/f8.
     */
    private static void drawDoubleSidedQuad(@NotNull PoseStack poseStack, BufferBuilder buffer,
                                            float offsetX, float offsetY, float size) {
        // FRONT face
        buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(poseStack.last().pose(), 0.0F - offsetX, 0.0F - offsetY, 0.01F).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), size - offsetX, 0.0F - offsetY, 0.01F).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), size - offsetX, size - offsetY, 0.01F).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - offsetX, size - offsetY, 0.01F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        // BACK face (flip winding order via rotation)
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(poseStack.last().pose(), 0.0F - offsetX, 0.0F - offsetY, 0.01F).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), size - offsetX, 0.0F - offsetY, 0.01F).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), size - offsetX, size - offsetY, 0.01F).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - offsetX, size - offsetY, 0.01F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicalTurretEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "textures/block/passable_block.png");
    }
}