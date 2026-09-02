package zettasword.zetta_spells.spells;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.registry.EBRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.spells.earth.SummonBee;
import zettasword.zetta_spells.spells.earth.SummonRabbit;
import zettasword.zetta_spells.spells.earth.TreeChop;
import zettasword.zetta_spells.spells.fire.ExplodeItem;
import zettasword.zetta_spells.spells.fire.ExplosionSpell;
import zettasword.zetta_spells.spells.fire.HeatHealing;
import zettasword.zetta_spells.spells.magic.*;
import zettasword.zetta_spells.spells.necromancy.Hex;
import zettasword.zetta_spells.spells.necromancy.Resurrect;
import zettasword.zetta_spells.spells.necromancy.TurnMinion;
import zettasword.zetta_spells.spells.sorcery.AdvancedLevitationSigil;
import zettasword.zetta_spells.spells.sorcery.LevitationSigil;
import zettasword.zetta_spells.spells.sorcery.OreConversion;
import zettasword.zetta_spells.spells.sorcery.Pull;

public class ZSSpells {
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
    public static final RegistryObject<Spell> CUSTOM_PLAYER_SPELL = SPELLS.register("custom_player_spell", CustomPlayerSpell::new);

    //1.6.0
    public static final RegistryObject<Spell> EXPLODE_ITEM = SPELLS.register("explode_item", ExplodeItem::new);
   // public static final RegistryObject<Spell> CHARGE_CANDLE = SPELLS.register("charge_candle", ChargeCandle::new); // Candle spell that charges different candles and allows it to locate ores, mark entities and etc.
    //public static final RegistryObject<Spell> CREATE_SPIRIT = SPELLS.register("create_spirit", CreateSpirit::new); // Creates Spirit from Dust in offhand, it will be your new pet. It's unrestricted version of Remnant. Through Evolving it you can turn it different forms: Slime, Anime Girl(GoG integration) or etc.
    
    /*
    public static final RegistryObject<Spell> MAGIC_TURRET = SPELLS.register("magic_turret", () -> new ConstructSpell<>(MagicalTurretEntity::new, false).floor(false)
            .assignProperties(SpellProperties.builder()
                    .assignBaseProperties(SpellTiers.ADVANCED, Elements.MAGIC, SpellTypes.CONSTRUCT, SpellAction.POINT, 50, 10, 100)
                    .add(DefaultProperties.DURATION, 1200)
                    .build()
            ));
*/
    // 2.0.0
    public static final RegistryObject<Spell> TREE_CHOP = SPELLS.register("tree_chop", TreeChop::new);

    //TODO: Add sounds to sounds.json for all spells...
}
