package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import zettasword.zetta_spells.system.SpellTarget;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/** Class for temporary storing some information about the spell being made. **/
public class SpellCreateContext {
    private Level world = null;
    private LivingEntity caster = null;
    private InteractionHand hand = null;
    private HashMap<String, SVar> mods = null;
    private List<SpellTarget> targets = Lists.newArrayList();
    private Direction hit_direction = Direction.UP;
    private String previous = "";
    private Predicate<Entity> filter = entity -> true;
    private Predicate<SpellCreateContext> ifs = context -> true;
    // Switch it with stopSpell() only if there is real need. It stops the spell completely.
    private boolean stopSpell = false;
    // To ensure we calculate cooldown and stuff ONLY if spell is finished for CustomPlayerSpell class and FinishedSpellbookItem class.
    private boolean spellFinished = false;
    /** Add ticks to this cooldown to make spell cooldown be bigger. 20 ticks = 1 second. **/
    private int cooldown = 0;
    /** Switch this false to disable casting visual effects and sounds, it is recommended to check it if you're doing something visual with the spell!**/
    private boolean createFx = true;
    private boolean isExternalCast = false;
    /** This allows to understand what the spell cost should be. **/
    private int preCost = 0;
    private int lastExternalMana = 0;


    public SpellCreateContext(Level world, LivingEntity caster, InteractionHand hand){
        this.world = world;
        this.caster = caster;
        this.targets.add(new SpellTarget(caster));
        this.hand = hand;
        // Default modifications are stored here!
        this.mods = new HashMap<>();
        this.mods.put("range", SVar.init(14));
        this.mods.put("amplification", SVar.init(1));
        this.mods.put("duration", SVar.init(10));
        this.mods.put("power", SVar.init(1));
        this.mods.put("ignoreliving", SVar.init(false));
    }

    public SpellCreateContext(Level world, LivingEntity caster, Entity target){
        this.world = world;
        this.caster = caster;
        this.targets.add(new SpellTarget(target));
        this.hand = InteractionHand.MAIN_HAND;
        // Default modifications are stored here!
        this.mods = new HashMap<>();
        this.mods.put("range", SVar.init(14));
        this.mods.put("amplification", SVar.init(1));
        this.mods.put("duration", SVar.init(10));
        this.mods.put("power", SVar.init(1));
        this.mods.put("ignoreliving", SVar.init(false));
    }

    public SpellCreateContext(){}

    public Level world() {
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

    public HashMap<String, SVar> getMods() {
        return mods;
    }

    public void setMods(HashMap<String, SVar> mods) {
        this.mods = mods;
    }

    public SpellTarget getTarget() {
        return targets.get(0);
    }

    public List<SpellTarget> getTargets() {
        return targets;
    }

    // Difference between SET and ADD is that when SET we make it singular target only.

    public void setTarget(Entity target) {
        this.targets.clear();
        this.targets.add(new SpellTarget(target));
    }

    public void setTarget(BlockPos target) {
        this.targets.clear();
        this.targets.add(new SpellTarget(target));
    }

    public void setTarget(SpellTarget target){
        this.targets.clear();
        this.targets.add(target);
    }

    public void addTarget(Entity target){
        this.targets.add(new SpellTarget(target));
    }

    public void addTarget(BlockPos target){
        this.targets.add(new SpellTarget(target));
    }

    public void addTarget(SpellTarget target){
        this.targets.add(target);
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

    public boolean isCreative(){
        return this.caster instanceof Player player && player.isCreative();
    }

    /** Quick method to get SVar without making code look too messy!
     * @param mod Name of SVar player defined before.
     * @param fallback Fallback in case something goes wrong, like there is no defined value in the spell.
     * @return Returns SVar, modification of the spellword spell.
     */
    public SVar getMod(String mod, int fallback){
        return getMods().getOrDefault(mod, SVar.init(fallback));
    }

    /** Quick method to get SVar without making code look too messy!
     * @param mod Name of SVar player defined before.
     * @param fallback Fallback in case something goes wrong, like there is no defined value in the spell.
     * @return Returns SVar, modification of the spellword spell.
     */
    public SVar getMod(String mod, boolean fallback){
        return getMods().getOrDefault(mod, SVar.init(fallback));
    }

    /** Quick method to get SVar without making code look too messy!
     * @param mod Name of SVar player defined before.
     * @param fallback Fallback in case something goes wrong, like there is no defined value in the spell.
     * @return Returns SVar, modification of the spellword spell.
     */
    public SVar getMod(String mod, String fallback){
        return getMods().getOrDefault(mod, SVar.init(fallback));
    }

    /** Quick method to get SVar without making code look too messy!
     * @param mod Name of SVar player defined before.
     * @param fallback Fallback in case something goes wrong, like there is no defined value in the spell.
     * @return Returns SVar, modification of the spellword spell.
     */
    public SVar getMod(String mod, Vec3 fallback){
        return getMods().getOrDefault(mod, SVar.init(fallback));
    }

    @Nullable
    public SVar getMod(String mod){
        return getMods().get(mod) == null ? null : getMods().get(mod);
    }

    public void clearTargets() {
        this.targets.clear();
    }

    public Predicate<Entity> filter() {
        return filter;
    }

    public void setFilter(Predicate<Entity> filter) {
        this.filter = filter;
    }

    public Predicate<SpellCreateContext> getIf() {
        return ifs;
    }

    public void setIf(Predicate<SpellCreateContext> ifs) {
        this.ifs = ifs;
    }

    /** You can use this to stop spell from further building up. **/
    public void stopSpell(){
        this.stopSpell = true;
    }

    public boolean shouldStopSpell() {
        return stopSpell;
    }

    public boolean isSpellFinished() {
        return spellFinished;
    }

    public void spellFinished() {
        this.spellFinished = true;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void addCooldown(int cooldown) {
        this.cooldown += cooldown;
    }

    public boolean canCreateFx() {
        return createFx;
    }

    public void setCreateFx(boolean createFx) {
        this.createFx = createFx;
    }

    public boolean isExternalCast() {
        return isExternalCast;
    }

    public void externalCast() {
        isExternalCast = true;
    }

    public int getPreCost() {
        return preCost;
    }

    public void setPreCost(int preCost) {
        this.preCost = preCost;
    }

    public void addPreCost(int addition) {
        this.preCost += addition;
    }

    public int lastExternalMana() {
        return lastExternalMana;
    }

    public void setLastExternalMana(int lastExternalMana) {
        this.lastExternalMana = lastExternalMana;
    }
}
