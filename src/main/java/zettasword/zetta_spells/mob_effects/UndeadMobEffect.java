package zettasword.zetta_spells.mob_effects;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.ZettaSpellsMod;

@Mod.EventBusSubscriber(modid = ZettaSpellsMod.MODID)
public class UndeadMobEffect extends MagicMobEffect {
    public UndeadMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x791ABB);
    }

    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().isDay() && !livingEntity.level().isClientSide) {
            if (livingEntity.hasEffect(EBMobEffects.DECAY.get())) livingEntity.removeEffect(EBMobEffects.DECAY.get());
            // Replace Curse of Undeath
            if (livingEntity.hasEffect(EBMobEffects.CURSE_OF_UNDEATH.get())) livingEntity.removeEffect(EBMobEffects.CURSE_OF_UNDEATH.get());

            float f = livingEntity.getLightLevelDependentMagicValue();
            if (f > 0.5F && livingEntity.level().random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && livingEntity.level().canSeeSky(new BlockPos((int)livingEntity.getX(), (int)(livingEntity.getY() + (double)livingEntity.getEyeHeight()), (int)livingEntity.getZ()))) {
                boolean flag = true;
                ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + livingEntity.level().random.nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            if (itemstack.getItem() instanceof WizardArmorItem) {
                                livingEntity.setSecondsOnFire(8);
                            } else {
                                livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                                livingEntity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                            }
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    livingEntity.setSecondsOnFire(8);
                }
            }
        }
    }


    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        //ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(15 + world.random.nextInt(5)).spawn(world);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTarget(LivingChangeTargetEvent event){
        if (event.getNewTarget() != null && event.getNewTarget().hasEffect(ZSEffects.UNDEAD.get()) && event.getEntity().getMobType() == MobType.UNDEAD){
            if (event.getEntity() instanceof Mob mob){
                if (mob.getLastAttacker() != event.getNewTarget()) {
                    event.setNewTarget(null);
                }
            }
        }
    }
}
