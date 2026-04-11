package zettasword.zetta_spells.items;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoldenAsDiamondItem extends Item {
    public GoldenAsDiamondItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> tooltip, net.minecraft.world.item.@NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.zetta_spells.golden_astral_diamond.desc"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        double range = 14;
        Vec3 look = player.getLookAngle();
        Vec3 origin = new Vec3(player.getX(), player.getY() + (double)player.getEyeHeight() - (double)0.25F, player.getZ());
        if (level.isClientSide && ClientUtils.isFirstPerson(player)) {
            origin = origin.add(look.scale(1.2));
        }
        Vec3 endpoint = origin.add(look.scale(range));
        HitResult rayTrace = RayTracer.rayTrace(level, player, origin, endpoint, 0.0F, false, Entity.class, RayTracer.ignoreEntityFilter(player));
        if (rayTrace instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity instanceof Mob mob) {
                MinionData data = Services.OBJECT_DATA.getMinionData(mob);
                if (data != null && data.getLifetime() > 0) {
                    data.setLifetime(-1);
                    if (player.level().isClientSide) {
                        for (int j = 0; (float) j < 20; ++j) {
                            double x = mob.xo + level.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            double y = mob.yo + (double) mob.getEyeHeight() - (double) 0.5F + level.random.nextDouble();
                            double z = mob.zo + level.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z)
                                    .velocity((double) 0.0F, 0.1, (double) 0.0F)
                                    .color(0xFF9800)
                                    .spawn(level);
                        }
                        player.playSound(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.7F, 1.0F);
                    }
                    stack.shrink(1);
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
