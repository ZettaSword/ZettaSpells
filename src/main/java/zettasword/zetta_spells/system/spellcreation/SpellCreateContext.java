package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.system.SpellTarget;

import java.util.Map;

/** Class for temporary storing some information about the spell being made. **/
public class SpellCreateContext {
    private Level world = null;
    private LivingEntity caster = null;
    private InteractionHand hand = null;
    private Map<String, SVar> mods = null;
    private SpellTarget target = null;
    private Direction hit_direction = Direction.UP;
    private String previous = "";


    public SpellCreateContext(Level world, LivingEntity caster, InteractionHand hand){
        this.world = world;
        this.caster = caster;
        this.target = new SpellTarget(caster);
        this.hand = hand;
        // Default modifications are stored here!
        this.mods = Map.of("range", SVar.init(14),
                "amplification", SVar.init(1),
                "duration", SVar.init(10),
                "power", SVar.init(1),
                "ignorelivingentities", SVar.init(false));
    }

    public SpellCreateContext(){}

    public Level getWorld() {
        return world;
    }

    public void setWorld(Level world) {
        this.world = world;
    }

    public LivingEntity getCaster() {
        return caster;
    }

    public void setCaster(Player caster) {
        this.caster = caster;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public void setHand(InteractionHand hand) {
        this.hand = hand;
    }

    public Map<String, SVar> getMods() {
        return mods;
    }

    public void setMods(Map<String, SVar> mods) {
        this.mods = mods;
    }

    public SpellTarget getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = new SpellTarget(target);
    }

    public void setTarget(BlockPos target) {
        this.target = new SpellTarget(target);
    }

    public void setTarget(SpellTarget target){
        this.target = target;
    }

    public Direction getHitDirection() {
        return hit_direction;
    }

    public void setHitDirection(Direction hit_direction) {
        this.hit_direction = hit_direction;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
