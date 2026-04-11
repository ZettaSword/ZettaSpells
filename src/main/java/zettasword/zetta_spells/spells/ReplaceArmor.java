package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.ZettaSpellsMod;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.ArcaneColor;

import javax.annotation.Nullable;

public class ReplaceArmor extends RaySpell {
    public ReplaceArmor(){
        this.requiresPacket();
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof Mob mob) {
            InteractionHand first_hand = ctx.caster().getMainHandItem().getItem() instanceof ISpellCastingItem ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack stack1 = ctx.caster().getItemInHand(first_hand).copy();
            if (!stack1.isEmpty()) {
                EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack1);
                ItemStack in_slot = mob.getItemBySlot(slot);
                dropCurrentSlotItem(ctx.world(), mob, in_slot);
                mob.setItemSlot(slot, ItemStack.EMPTY);
                // Setting ours in its place
                mob.setItemSlot(slot, stack1);
                ctx.caster().setItemInHand(first_hand, ItemStack.EMPTY);
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.NECROMANCY, SpellType.UTILITY, SpellAction.POINT, 10,  0, 20)
                .add(DefaultProperties.RANGE, 14F)
                .build();
    }

    public static void dropCurrentSlotItem(@NotNull Level level, Mob mob, ItemStack in_slot) {
        if (!in_slot.isEmpty() && !level.isClientSide) {
            // Dropping current item
            mob.spawnAtLocation(in_slot, 0.5F);
        }
    }
}
