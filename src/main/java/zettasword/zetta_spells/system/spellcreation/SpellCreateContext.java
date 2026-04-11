package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Class for temporary storing some information about the spell being made. **/
public class SpellCreateContext {
    private Level world = null;
    private LivingEntity caster = null;
    private InteractionHand hand = null;

    public SpellCreateContext(Level world, LivingEntity caster, InteractionHand hand){
        this.world = world;
        this.caster = caster;
        this.hand = hand;
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
}
