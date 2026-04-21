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

        Vec3 endpoint = origin.add(look.scale(range));
        HitResult rayTrace = RayTracer.rayTrace(world, caster, origin, endpoint, 0.0F, false, Entity.class,
                ctx.getMod("ignorelivingentities",false).getBoolean() ? EntityUtil::isLiving : RayTracer.ignoreEntityFilter(caster));
        if (rayTrace instanceof EntityHitResult entityHit) {
            ctx.addTarget(entityHit.getEntity());
            range = origin.distanceTo(rayTrace.getLocation());
        } else if (rayTrace instanceof BlockHitResult blockHit) {
            ctx.addTarget(blockHit.getBlockPos());
            ctx.setHitDirection(blockHit.getDirection());
            range = origin.distanceTo(rayTrace.getLocation());
        }
        // So caster can't get chosen.
        if (rayTrace.getType() == HitResult.Type.MISS){
            ctx.clearTargets();
        }

        if (world.isClientSide && ctx.getTarget().getTargetPos() != null){
            BlockPos targetPos = ctx.getTarget().getTargetPos();

            double x = targetPos.getX();
            double y = targetPos.getY();
            double z = targetPos.getZ();

            ParticleBuilder.create(EBParticles.BEAM).entity(caster).pos(origin.subtract(caster.position())).length(range).color(0xFF9800)
                    .scale(2).time(20).spawn(world);

            ParticleBuilder.create(EBParticles.SPHERE).pos(x + 0.5, y + 0.5, z + 0.5).color(0xFF9800).time(20).scale(1.0F)
                    .spawn(world);
        }
        return true;
    }
}
