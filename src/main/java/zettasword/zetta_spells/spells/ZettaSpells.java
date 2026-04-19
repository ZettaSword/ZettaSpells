package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.construct.FireRingConstruct;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ConstructSpell;
import com.binaris.wizardry.core.registry.EBRegistries;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.blocks.LevitationSigilBlock;
import zettasword.zetta_spells.entity.construct.MagicalTurretEntity;

public class ZettaSpells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, "zetta_spells");

    public static final RegistryObject<Spell> HEAT_HEALING = SPELLS.register("heat_healing", HeatHealing::new);
    public static final RegistryObject<Spell> MAGIC_RESTORATION = SPELLS.register("magic_restoration", MagicRestoration::new);
    public static final RegistryObject<Spell> MAGIC_BARRIER = SPELLS.register("magic_barrier", MagicBarrier::new);
    public static final RegistryObject<Spell> TURN_MINION = SPELLS.register("turn_minion", TurnMinion::new);
    public static final RegistryObject<Spell> ORE_CONVERSION = SPELLS.register("ore_conversion", OreConversion::new);
    public static final RegistryObject<Spell> LEVITATION_SIGIL = SPELLS.register("levitation_sigil", LevitationSigil::new);
    public static final RegistryObject<Spell> ADVANCED_LEVITATION_SIGIL = SPELLS.register("advanced_levitation_sigil", AdvancedLevitationSigil::new);
    public static final RegistryObject<Spell> RESURRECT = SPELLS.register("resurrect", Resurrect::new);
    public static final RegistryObject<Spell> HEX = SPELLS.register("hex", Hex::new);
    public static final RegistryObject<Spell> SUMMON_RABBIT = SPELLS.register("summon_rabbit", SummonRabbit::new);
    public static final RegistryObject<Spell> ORIGIN = SPELLS.register("origin", Origin::new);
    public static final RegistryObject<Spell> SUMMON_BEE = SPELLS.register("summon_bee", SummonBee::new);
    // 1.1.0
    public static final RegistryObject<Spell> EXPLOSION = SPELLS.register("explosion", ExplosionSpell::new);
    public static final RegistryObject<Spell> PULL = SPELLS.register("pull", Pull::new);
    // 1.2.0
    //public static final RegistryObject<Spell> SUMMON_WARDEN = SPELLS.register("summon_warden", SummonWarden::new);

    //1.4.0
    public static final RegistryObject<Spell> TURN_SPELLCASTER = SPELLS.register("turn_spellcaster", TurnSpellcaster::new);
/*
    public static final RegistryObject<Spell> MAGIC_TURRET = SPELLS.register("magic_turret", () -> new ConstructSpell<>(MagicalTurretEntity::new, false).floor(false)
            .assignProperties(SpellProperties.builder()
                    .assignBaseProperties(SpellTiers.ADVANCED, Elements.MAGIC, SpellType.CONSTRUCT, SpellAction.POINT, 50, 10, 100)
                    .add(DefaultProperties.DURATION, 1200)
                    .build()
            ));
*/
    //TODO: Add sounds to sounds.json for all spells...
}
