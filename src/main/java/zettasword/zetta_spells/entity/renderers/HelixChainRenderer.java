package zettasword.zetta_spells.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import zettasword.zetta_spells.ZettaSpells;

public class HelixChainRenderer<T extends Entity> extends EntityRenderer<T> {

    // The texture for the chains
    private static final ResourceLocation CHAIN_TEXTURE = ZettaSpells.location("textures/sigils/system_call.png");
    
    // Configuration for the helix
    private static final float RADIUS = 2f;      // Distance from player
    private static final float HEIGHT = 2.5f;      // Total height of the spiral
    private static final int SEGMENTS = 80;        // How many chain links to render
    private static final float ROTATION_SPEED = 0.05f; // How fast it spins

    public HelixChainRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        // 1. Setup the PoseStack
        // We translate to the entity's position so the helix centers on them
        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() / 2.0D, 0.0D); // Center vertically on entity

        // 2. Get the VertexConsumer for rendering textured quads
        // entityCutoutNoCull allows transparency and doesn't cull backfaces
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(CHAIN_TEXTURE));

        // 3. Calculate Animation Offset
        // Use tickCount + partialTicks for smooth animation
        float timeOffset = (entity.tickCount + partialTicks) * ROTATION_SPEED;

        // 4. Render the Helix Segments
        for (int i = 0; i < SEGMENTS; i++) {
            // Calculate position along the helix
            // We spread the segments over 2 full rotations (4 * PI)
            float angle = (i / (float)SEGMENTS) * (4.0f * (float)Math.PI) + timeOffset;
            
            float x = (float) (RADIUS * Math.cos(angle));
            float z = (float) (RADIUS * Math.sin(angle));
            float y = ((i / (float)SEGMENTS) * HEIGHT) - (HEIGHT / 2.0f);

            // Push pose for this specific segment
            poseStack.pushPose();
            poseStack.translate(x, y, z);

            // Rotate the segment to face the center (optional, makes it look like a cage)
            // Or rotate to align with the path. Here we make them face outwards/radially.
            poseStack.mulPose(Axis.YP.rotation(angle)); 
            
            // Scale the chain link size
            poseStack.scale(0.15f, 0.15f, 0.15f);

            // Render a simple Quad (Chain Link)
            // Coordinates are -0.5 to 0.5 because of the scale above
            renderQuad(poseStack, consumer, packedLight);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    // Helper method to render a single textured quad (a chain link)
    private void renderQuad(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        // Normal matrix for lighting
        Matrix3f normal = poseStack.last().normal();
        
        // Vertex Format: x, y, z, u, v, normalX, normalY, normalZ, lightU, lightV
        // We draw a simple square facing the Z axis (since we rotated the pose, it will face radially)
        
        consumer.vertex(poseStack.last().pose(), -0.5f, -0.5f, 0.0f)
                .color(255, 255, 255, 255)
                .uv(0.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();

        consumer.vertex(poseStack.last().pose(), 0.5f, -0.5f, 0.0f)
                .color(255, 255, 255, 255)
                .uv(1.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();

        consumer.vertex(poseStack.last().pose(), 0.5f, 0.5f, 0.0f)
                .color(255, 255, 255, 255)
                .uv(1.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();

        consumer.vertex(poseStack.last().pose(), -0.5f, 0.5f, 0.0f)
                .color(255, 255, 255, 255)
                .uv(0.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        // This is required by the abstract class, but we use a specific texture in render()
        return CHAIN_TEXTURE; 
    }
}