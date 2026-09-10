package zettasword.zetta_spells.entity.renderers.living;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.living.WoodGolem;
import zettasword.zetta_spells.entity.renderers.living.models.WoodGolemModel;

@OnlyIn(Dist.CLIENT)
public class WoodGolemRenderer extends MobRenderer<WoodGolem, WoodGolemModel<WoodGolem>> {
   private static final ResourceLocation GOLEM_LOCATION = ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID,
           "textures/entity/wood_golem.png");

   public WoodGolemRenderer(EntityRendererProvider.Context p_174188_) {
      super(p_174188_, new WoodGolemModel<>(p_174188_.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
   }

   public ResourceLocation getTextureLocation(WoodGolem p_115012_) {
      return GOLEM_LOCATION;
   }

   protected void setupRotations(WoodGolem p_115014_, PoseStack p_115015_, float p_115016_, float p_115017_, float p_115018_) {
      super.setupRotations(p_115014_, p_115015_, p_115016_, p_115017_, p_115018_);
      if (!((double)p_115014_.walkAnimation.speed() < 0.01D)) {
         float f = 13.0F;
         float f1 = p_115014_.walkAnimation.position(p_115018_) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         p_115015_.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
      }
   }
}