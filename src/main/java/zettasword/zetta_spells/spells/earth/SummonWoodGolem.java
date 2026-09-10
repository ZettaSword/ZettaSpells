package zettasword.zetta_spells.spells.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.living.WoodGolem;

public class SummonWoodGolem extends MinionSpell<WoodGolem> {
    public SummonWoodGolem() {// Knock-knock! Who's there?
        super(level -> new WoodGolem(ZSEntities.WOOD_GOLEM.get(), level));
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected WoodGolem createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        return super.createMinion(world, caster, modifiers);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.MINION, SpellAction.SUMMON, 100, 0, 120)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.MINION_COUNT, 1)
                .add(DefaultProperties.SUMMON_RADIUS, 3)
                .build();
    }
}
