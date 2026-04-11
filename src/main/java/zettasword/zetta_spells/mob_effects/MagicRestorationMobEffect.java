package zettasword.zetta_spells.mob_effects;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MagicRestorationMobEffect extends MagicMobEffect {
    public MagicRestorationMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF9800);
    }

    public static boolean rechargeMana(@NotNull LivingEntity livingEntity, int amplifier, ItemStack stack) {
        if (stack.getItem() instanceof IManaStoringItem manaStoringItem){
            if (manaStoringItem.isManaFull(stack)) return false;
            manaStoringItem.rechargeMana(stack, 2 * amplifier);
            if (livingEntity instanceof Player player){
                player.inventoryMenu.broadcastChanges();
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 5 == 0;
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).color(0xFF9800).time(15 + world.random.nextInt(5)).spawn(world);
    }
}
