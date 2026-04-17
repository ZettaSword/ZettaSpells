package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.capability.spellcaster.SpellcasterData;
import zettasword.zetta_spells.capability.spellcaster.SpellcastingGoal;
import zettasword.zetta_spells.system.ArcaneColor;

import java.util.ArrayList;
import java.util.List;

public class TurnSpellcaster extends RaySpell {

    public TurnSpellcaster(){

    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    /**
     * Called when the ray does not hit any entities or blocks. Override this method to perform an action when the ray
     * misses, such as spawning particles at the endpoint. Return true if the spell should be considered successfully
     * cast even when it misses, or false if the spell should not be cast when it misses (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param origin    The starting point of the ray.
     * @param direction The normalized direction vector of the ray.
     * @return true if the spell should be considered successfully cast even when it misses, false if the spell should
     * not be cast when it misses.
     */
    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof Mob minion){
            return turnSpellcaster(ctx.caster(), ctx.modifiers(), minion);
        }
        return false;
    }

    public static boolean turnSpellcaster(LivingEntity caster, SpellModifiers modifiers, Mob target) {
        // Get or create capability
        target.getCapability(SpellcasterData.INSTANCE).ifPresent(data -> {
            // Configure spellcasting
            // Assign spells based on target type
            List<Spell> spells = new ArrayList<>();
            spells.add(Spells.MAGIC_MISSILE);
            EntityUtil.populateSpells(spells, Elements.MAGIC, false, 3, target.getRandom());
            data.setSpells(spells);

            // Scale power based on modifiers
            data.getModifiers().set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY));
            data.getModifiers().set(SpellModifiers.DURATION, modifiers.get(SpellModifiers.DURATION));

            // Add AI goal if not already present
            boolean hasGoal = target.goalSelector.getAvailableGoals().stream()
                    .anyMatch(g -> g.getGoal() instanceof SpellcastingGoal);
            if (!hasGoal) {
                //((MobGoalsAccessor) target).getTargetSelector().removeAllGoals((goal) -> true);
                //((MobGoalsAccessor) target).getGoalSelector().removeAllGoals((goal) -> true);
                target.getBrain().clearMemories();
                target.goalSelector.addGoal(1, new SpellcastingGoal(target, 14.0F, 30, 50));
            }
        });
        return target.getCapability(SpellcasterData.INSTANCE).isPresent();
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(ArcaneColor.ARCANE).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.MAGIC, SpellType.ALTERATION, SpellAction.POINT, 1500, 80, 20)
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }
}
