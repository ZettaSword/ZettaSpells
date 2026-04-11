package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonRabbit extends MinionSpell<Rabbit> {
    public SummonRabbit() {
        super(level -> new Rabbit(EntityType.RABBIT, level));
        this.soundValues(7, 0.6f, 0);
        this.requiresPacket();
    }

    @Override
    protected Rabbit createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        Rabbit minion = super.createMinion(world, caster, modifiers);
        minion.setVariant(Rabbit.Variant.EVIL); // Yeah, making sure it's Great Rabbits.
        Rabbit.RabbitGroupData data = new Rabbit.RabbitGroupData(Rabbit.Variant.EVIL);
        CompoundTag tag = minion.getPersistentData();
        tag.putInt("RabbitType", 99);
        minion.addAdditionalSaveData(tag);
        minion.readAdditionalSaveData(tag);
        if (!world.isClientSide && caster != null)
            minion.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, data, tag);
        return minion;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellType.MINION, SpellAction.SUMMON, 20, 0, 60)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.MINION_COUNT, 5)
                .add(DefaultProperties.SUMMON_RADIUS, 3)
                .build();
    }
}
