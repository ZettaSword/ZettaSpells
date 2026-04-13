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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.EnchantedGoldenAppleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.ArcaneColor;

import javax.annotation.Nullable;

public class Origin extends RaySpell {
    public Origin(){
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.caster().isShiftKeyDown()) return originEffect(ctx, ctx.caster());
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        LivingEntity target = null;

        if (entityHit.getEntity() instanceof LivingEntity living) target = living;
        if (ctx.caster().isShiftKeyDown()) target = ctx.caster();

        return originEffect(ctx, target);
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        if (ctx.caster().isShiftKeyDown()) return originEffect(ctx, ctx.caster());
        return false;
    }

    private static boolean originEffect(CastContext ctx, @Nullable LivingEntity target) {
        if (target != null){
            ItemStack stack = ctx.caster().getMainHandItem().getItem() instanceof ISpellCastingItem ? ctx.caster().getOffhandItem() : ctx.caster().getMainHandItem();
            if (stack.getItem() instanceof ISpellCastingItem) return false;
            boolean flag = false;
            if (!ctx.world().isClientSide) {
                if ((stack.getItem() == Items.BLAZE_POWDER || stack.getItem() == Items.FIREWORK_STAR) && !MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
                    target.setSecondsOnFire(30);
                    MagicDamageSource.causeMagicDamage(ctx.caster(), target, 4, EBDamageSources.FIRE);
                    flag=true;
                }
                if (stack.getItem() == Items.GUNPOWDER) {
                    ctx.world().explode(target, target.xo, target.yo, target.zo, 4.0F, Level.ExplosionInteraction.MOB);
                    flag=true;
                }
                if (stack.getItem() == Items.APPLE){
                    if (target.getMobType() == MobType.UNDEAD){
                        target.hurt(target.damageSources().inFire(), 8);
                    }else{
                        target.heal(4);
                    }
                    flag=true;
                }
                if (stack.getItem() == Items.POISONOUS_POTATO || stack.getItem() == Items.SPIDER_EYE){
                    Alchemy.applyNotHiding(target, MobEffects.POISON, 30, 0, ctx.caster());
                    flag=true;
                }
                if (stack.getItem() == Items.FEATHER){
                    Alchemy.applyNotHiding(target, MobEffects.LEVITATION, 30, 0, ctx.caster());
                    flag=true;
                }

                if (stack.getItem() == Items.POTION){
                    for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(stack)) {
                        if (mobeffectinstance.getEffect().isInstantenous()) {
                            mobeffectinstance.getEffect().applyInstantenousEffect(ctx.caster(), ctx.caster(), target, mobeffectinstance.getAmplifier(), 1.0D);
                        } else {
                            target.addEffect(new MobEffectInstance(mobeffectinstance));
                        }
                    }
                    flag = true;
                }

                if (stack.getItem() == Items.GOLDEN_APPLE){
                    target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
                    flag = true;
                }

                if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE){
                    target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3));
                    flag = true;
                }

                if (flag) stack.shrink(1);
            }
            if (ctx.world().isClientSide){
                ParticleBuilder.create(EBParticles.BUFF).entity(target).color(ArcaneColor.ARCANE).spawn(ctx.world());
            }
            return true;
        }
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(ArcaneColor.ARCANE).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.MAGIC, SpellType.ATTACK, SpellAction.POINT, 30, 10, 40)
                .add(DefaultProperties.RANGE, 14F)
                .build();
    }
}
