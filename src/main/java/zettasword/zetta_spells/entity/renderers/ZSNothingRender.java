package zettasword.zetta_spells.entity.renderers;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ZSNothingRender extends EntityRenderer<Entity> {

    public ZSNothingRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    public void render(@NotNull Entity entity, float p_114486_, float partialTicks,
                       @NotNull PoseStack poseStack, MultiBufferSource p_114489_, int p_114490_) {
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return null;
    }
}
