package zettasword.zetta_spells.system.spellcreation.light;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import zettasword.zetta_spells.ZettaSpellsMod;

import java.util.*;

//@Mod.EventBusSubscriber(modid = ZettaSpellsMod.MODID,value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WordsDynamicLighting {

    private static final float LIGHT_RADIUS = 3.0f;
    private static final float ALPHA = 0.15f;
    private static final int SEGMENTS = 10;
    private static final float R = 1.0f, G = 0.9f, B = 0.6f;

    //@SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (!isHoldingLux(player)) return;

        PoseStack poseStack = getPoseStack(event, player, mc);

        // 3. Setup render state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest(); // Allows glow to render through blocks

        // 4. Get buffer source (correct 1.20.1 API)
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        // 5. Render & submit
        renderLightSphere(poseStack.last().pose(), buffer, LIGHT_RADIUS, ALPHA);
        bufferSource.endBatch(RenderType.translucent());

        // 6. Restore state
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static @NotNull PoseStack getPoseStack(RenderLevelStageEvent event, LocalPlayer player, Minecraft mc) {
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // 1. Interpolated player position
        float partial = event.getPartialTick();
        double px = player.xo + (player.getX() - player.xo) * partial;
        double py = player.yo + (player.getY() - player.yo) * partial + player.getEyeHeight() / 2.0;
        double pz = player.zo + (player.getZ() - player.zo) * partial;

        // 2. Convert to camera-relative space
        Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.translate(px - camPos.x, py - camPos.y, pz - camPos.z);
        return poseStack;
    }

    public static boolean isHoldingLux(LocalPlayer player) {
        for (ItemStack stack : player.getHandSlots()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.getBoolean("lux")) return true;
        }
        return false;
    }

    private static void renderLightSphere(Matrix4f matrix, VertexConsumer buffer, float radius, float alpha) {
        int fullBright = LightTexture.FULL_BRIGHT; // 0xF000F0

        for (int phi = 0; phi < SEGMENTS; phi++) {
            for (int theta = 0; theta < SEGMENTS; theta++) {
                float phi0 = phi / (float) SEGMENTS * (float) Math.PI;
                float phi1 = (phi + 1) / (float) SEGMENTS * (float) Math.PI;
                float theta0 = theta / (float) SEGMENTS * (float) Math.PI * 2f;
                float theta1 = (theta + 1) / (float) SEGMENTS * (float) Math.PI * 2f;

                Vec3 v00 = toCartesian(radius, phi0, theta0);
                Vec3 v10 = toCartesian(radius, phi1, theta0);
                Vec3 v11 = toCartesian(radius, phi1, theta1);
                Vec3 v01 = toCartesian(radius, phi0, theta1);

                // Normals for a sphere centered at origin = normalized position
                Vec3 n00 = v00.normalize();
                Vec3 n10 = v10.normalize();
                Vec3 n11 = v11.normalize();
                Vec3 n01 = v01.normalize();

                // ⚠️ FIX: Provide ALL required vertex elements in exact order
                buffer.vertex(matrix, (float) v00.x, (float) v00.y, (float) v00.z)
                        .color(R, G, B, alpha)
                        .uv(0f, 0f)
                        .uv2(fullBright)
                        .normal((float) n00.x, (float) n00.y, (float) n00.z)
                        .endVertex();

                buffer.vertex(matrix, (float) v10.x, (float) v10.y, (float) v10.z)
                        .color(R, G, B, alpha)
                        .uv(1f, 0f)
                        .uv2(fullBright)
                        .normal((float) n10.x, (float) n10.y, (float) n10.z)
                        .endVertex();

                buffer.vertex(matrix, (float) v11.x, (float) v11.y, (float) v11.z)
                        .color(R, G, B, alpha)
                        .uv(1f, 1f)
                        .uv2(fullBright)
                        .normal((float) n11.x, (float) n11.y, (float) n11.z)
                        .endVertex();

                buffer.vertex(matrix, (float) v01.x, (float) v01.y, (float) v01.z)
                        .color(R, G, B, alpha)
                        .uv(0f, 1f)
                        .uv2(fullBright)
                        .normal((float) n01.x, (float) n01.y, (float) n01.z)
                        .endVertex();
            }
        }
    }

    private static Vec3 toCartesian(float r, float phi, float theta) {
        return new Vec3(
                r * Math.sin(phi) * Math.cos(theta),
                r * Math.cos(phi),
                r * Math.sin(phi) * Math.sin(theta)
        );
    }
}