package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonWarden extends MinionSpell<Warden> {
    public SummonWarden() {// That would be horrifying
        super(level -> new Warden(EntityType.WARDEN, level));
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected Warden createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        return super.createMinion(world, caster, modifiers);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellType.MINION, SpellAction.SUMMON, 150, 60, 300)
                .add(DefaultProperties.MINION_LIFETIME, 1200)
                .add(DefaultProperties.MINION_COUNT, 1)
                .add(DefaultProperties.SUMMON_RADIUS, 3)
                .build();
    }
}
