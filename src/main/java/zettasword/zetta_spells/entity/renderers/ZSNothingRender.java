package zettasword.zetta_spells.entity.renderers;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZSNothingRender extends EntityRenderer<MagicConstructEntity> {

    public ZSNothingRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    public void render(@NotNull MagicConstructEntity entity, float p_114486_, float partialTicks,
                       @NotNull PoseStack poseStack, MultiBufferSource p_114489_, int p_114490_) {
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicConstructEntity entity) {
        return null;
    }
}
