package zettasword.zetta_spells.system.spellcreation.actions.shape;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;

public class RayWord extends SpellWord {

    public RayWord() {
        super("ray");
    }

    /**
     * Allows us to get if key things are considered. Like if there is a word, and is a current target is a spider, and more, obviously.
     *
     * @param ctx Context of the spell.
     * @param words   Words used in the spell.
     * @param i       Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean shouldCast(SpellCreateContext ctx, List<String> words, int i) {
        return words.get(i).equals("ray");
    }

    /**
     * Use this to cast the spell you've made and registered.
     *
     * @param ctx Context of the spell.
     * @param words   Words used in the spell.
     * @param i       Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        double range = ctx.getMod("range", 14).getInt();
        LivingEntity caster = ctx.getCaster();
        Level world = ctx.getWorld();
        Vec3 look = caster.getLookAngle();
        Vec3 origin = new Vec3(caster.getX(), caster.getY() + (double)caster.getEyeHeight() - (double)0.25F, caster.getZ());
        if (world.isClientSide && ClientUtils.isFirstPerson(caster)) {
            origin = origin.add(look.scale(1.2));
        }

        Vec3 targetPosClient = null;

        Vec3 endpoint = origin.add(look.scale(range));
        HitResult rayTrace = RayTracer.rayTrace(world, caster, origin, endpoint, 0.0F, false, Entity.class,
                ctx.getMod("ignoreliving",false).getBoolean() ? EntityUtil::isLiving : RayTracer.ignoreEntityFilter(caster));

        // So caster can't get chosen.
        if (rayTrace.getType() == HitResult.Type.MISS){
            ctx.clearTargets();
            ctx.stopSpell();
        }

        if (rayTrace instanceof EntityHitResult entityHit && rayTrace.getType() == HitResult.Type.ENTITY) {
            if (ctx.filter().test(entityHit.getEntity())) {
                ctx.clearTargets();
                ctx.addTarget(entityHit.getEntity());
                targetPosClient = entityHit.getEntity().getEyePosition();
            }else{
                ctx.clearTargets();
                ctx.stopSpell();
                targetPosClient = entityHit.getEntity().getEyePosition();
            }
            range = origin.distanceTo(rayTrace.getLocation());
        } else if (rayTrace instanceof BlockHitResult blockHit && rayTrace.getType() == HitResult.Type.BLOCK) {
            ctx.clearTargets();
            ctx.addTarget(blockHit.getBlockPos());
            targetPosClient = blockHit.getBlockPos().getCenter();
            ctx.setHitDirection(blockHit.getDirection());
            range = origin.distanceTo(rayTrace.getLocation());
        }


        if (world.isClientSide && targetPosClient != null){
            ParticleBuilder.create(EBParticles.BEAM).pos(origin).target(targetPosClient).length(range).color(0xFF9800)
                    .scale(1).time(20).spawn(world);
        }
        return true;
    }
}
