package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.entity.goal.MinionFollowOwnerGoal;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.content.spell.necromancy.SummonZombie;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.blocks.ZSBlocks;

public class TurnMinion extends RaySpell {
    /**
     * Attribute Modifier id
     */
    public static final String HEALTH_MODIFIER = "minion_health";

    /**
     * Attribute Modifier id
     */
    public static final String POTENCY_ATTRIBUTE_MODIFIER = "potency";

    public TurnMinion(){

    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof Mob minion && (entityHit.getEntity() instanceof Enemy || entityHit.getEntity() instanceof NeutralMob)){
            if (ctx.caster() instanceof Player player && player.isCreative()){
                if (!ctx.world().isClientSide){
                    return turnMinion(ctx.caster(), ctx.modifiers(), minion);
                }
            }
            if (minion.getHealth() > ctx.caster().getHealth()) return false;
            if (!ctx.world().isClientSide){
                return turnMinion(ctx.caster(), ctx.modifiers(), minion);
            }
            return true;
        }
        return false;
    }

    public static boolean turnMinion(LivingEntity caster, SpellModifiers modifiers, Mob minion) {
        if (Services.OBJECT_DATA.isMinion(minion)) return false;
        minion.setCustomName(Component.translatable("entity.ebwizardry.minion_name", caster.getDisplayName(), minion.getDisplayName()));
        MinionData data = Services.OBJECT_DATA.getMinionData(minion);
        if (data == null) return false;
        data.setSummoned(true);
        data.setOwnerUUID(caster.getUUID());
        setLifetime(minion, (int) Math.max((2400 * modifiers.get(SpellModifiers.DURATION)), 1200));
        data.setShouldFollowOwner(true);
        data.updateGoals();
        if (minion.getAttribute(Attributes.ATTACK_DAMAGE) != null)
            minion.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, modifiers.get(SpellModifiers.POTENCY) - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (minion.getAttribute(Attributes.MAX_HEALTH) != null)
            minion.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER, modifiers.get(HEALTH_MODIFIER) - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        minion.getBrain().clearMemories(); // So it restarts minion.
        minion.setHealth(minion.getMaxHealth());
        return true;
    }

    public static void setLifetime(Mob minion, int lifetime) {
        MinionData data = Services.OBJECT_DATA.getMinionData(minion);
        data.setLifetime(lifetime);
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellType.ALTERATION, SpellAction.POINT, 300, 20, 10)
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }
}
