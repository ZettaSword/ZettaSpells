package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.custom.ExplodeItemEntity;

public class ExplodeItem extends RaySpell {
    public ExplodeItem(){
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        ItemStack stack = ctx.caster().getOffhandItem().getItem() instanceof ICastItem ? ctx.caster().getMainHandItem().getItem() instanceof ICastItem ? null : ctx.caster().getMainHandItem() : ctx.caster().getOffhandItem();
        if (stack == null) return false;
        if (!ctx.world().isClientSide && !stack.isEmpty() && stack.isStackable()){
            ExplodeItemEntity explodeItem = new ExplodeItemEntity(ctx.world());
            explodeItem.setItem(new ItemStack(stack.copy().getItem()));
            explodeItem.setDefaultPickUpDelay();
            stack.shrink(1);
            explodeItem.setPos(blockHit.getLocation());
            ctx.world().addFreshEntity(explodeItem);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        ItemStack stack = ctx.caster().getOffhandItem().getItem() instanceof ICastItem ? ctx.caster().getMainHandItem().getItem() instanceof ICastItem ? null : ctx.caster().getMainHandItem() : ctx.caster().getOffhandItem();
        if (stack == null) return false;
        if (!ctx.world().isClientSide && !stack.isEmpty() && stack.isStackable()){
            ExplodeItemEntity explodeItem = new ExplodeItemEntity(ctx.world());
            explodeItem.setItem(new ItemStack(stack.copy().getItem()));
            stack.shrink(1);
            explodeItem.setPos(entityHit.getLocation());
            ctx.world().addFreshEntity(explodeItem);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        ItemStack stack = ctx.caster().getOffhandItem().getItem() instanceof ICastItem ? ctx.caster().getMainHandItem().getItem() instanceof ICastItem ? null : ctx.caster().getMainHandItem() : ctx.caster().getOffhandItem();
        if (stack == null) return false;
        if (!ctx.world().isClientSide && !stack.isEmpty() && stack.isStackable()){
            ExplodeItemEntity explodeItem = new ExplodeItemEntity(ctx.world());
            explodeItem.setItem(new ItemStack(stack.copy().getItem()));
            stack.shrink(1);
            explodeItem.setPos(direction);
            ctx.world().addFreshEntity(explodeItem);
            return true;
        }
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(15 + ctx.world().random.nextInt(5)).spawn(ctx.world());
        ctx.world().addParticle(ParticleTypes.ASH, x,y,z, vx,vy,vz);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT, 40, 0, 40)
                .add(DefaultProperties.RANGE, 8F)
                .build();
    }
}
