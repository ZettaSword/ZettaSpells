package zettasword.zetta_spells.capability.spellcaster;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.entity.living.AbstractWizard;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.networking.s2c.NPCSpellCastS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Basic spellcasting goal compatible with any Mob that has spellcasting data.
 * <p>
 * Supports two modes:
 * <ul>
 *   <li><b>ISpellCaster mode:</b> For AbstractWizard subclasses - uses direct method calls</li>
 *   <li><b>Capability mode:</b> For any Mob with SPELLCASTER_CAPABILITY - uses capability data</li>
 * </ul>
 * <p>
 * Handles spell selection, cooldowns, casting logic, continuous spells, events, and networking.
 */
public class SpellcastingGoal extends Goal {

    /** The mob that will use this goal */
    private final Mob attacker;

    /** Base cooldown between spell casts */
    private final int baseCooldown;

    /** Duration for continuous spells */
    private final int continuousSpellDuration;

    /** Maximum attack distance squared */
    private final float maxAttackDistance;

    /** The current target entity */
    private LivingEntity target;

    /** Current cooldown timer */
    private int cooldown;

    /** Timer for continuous spell casting */
    private int continuousSpellTimer;

    /** Time the target has been visible */
    private int seeTime;

    /** Cached spellcasting data (avoids repeated capability lookups) */
    private SpellCastingContext spellContext;

    /**
     * Default constructor.
     *
     * @param attacker                Mob that will use this goal
     * @param maxDistance             Maximum distance to the target for casting spells
     * @param baseCooldown            Base cooldown between spell casts
     * @param continuousSpellDuration Duration for continuous spells
     */
    public SpellcastingGoal(Mob attacker, float maxDistance, int baseCooldown, int continuousSpellDuration) {
        this.cooldown = -1;
        this.attacker = attacker;
        this.baseCooldown = baseCooldown;
        this.continuousSpellDuration = continuousSpellDuration;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.attacker.getTarget();
        if (livingEntity == null) return false;

        // Initialize spell context on first use
        if (this.spellContext == null) {
            this.spellContext = SpellCastingContext.from(attacker);
            if (this.spellContext == null || !this.spellContext.hasSpells()) {
                return false; // No spells available, goal cannot be used
            }
        }

        this.target = livingEntity;
        return this.spellContext.isActive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.cooldown = -1;
        this.setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
        this.continuousSpellTimer = 0;
        if (this.spellContext != null) {
            this.spellContext.setContinuousSpell(Spells.NONE);
            this.spellContext.setSpellCounter(0);
        }
    }

    /**
     * Sets the continuous spell for the attacker and notifies tracking clients.
     * Works with both ISpellCaster and capability-based mobs.
     */
    private void setContinuousSpellAndNotify(Spell spell, SpellModifiers modifiers) {
        if (this.spellContext != null) {
            this.spellContext.setContinuousSpell(spell);
        }

        // Send network packet for visual sync on clients
        if (!attacker.level().isClientSide && spell != Spells.NONE) {
            Services.NETWORK_HELPER.sendToTracking(attacker,
                    new NPCSpellCastS2C(attacker.getId(),
                            target == null ? -1 : target.getId(),
                            InteractionHand.MAIN_HAND,
                            spell, modifiers));
        }
    }

    @Override
    public void tick() {
        if (this.target == null || this.spellContext == null) {
            return;
        }

        // Refresh context in case capability was added/removed dynamically
        if (!this.spellContext.refresh()) {
            this.stop();
            return;
        }

        double distanceSq = this.attacker.distanceToSqr(this.target);
        boolean targetIsVisible = this.attacker.getSensing().hasLineOfSight(this.target);

        if (targetIsVisible) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        // Handle continuous spell casting
        if (this.continuousSpellTimer > 0) {
            this.continuousSpellTimer--;
            int currentTick = this.continuousSpellDuration - this.continuousSpellTimer;

            EntityCastContext ctx = new EntityCastContext(
                    attacker.level(), attacker, InteractionHand.MAIN_HAND,
                    currentTick, target, this.spellContext.getModifiers());

            this.spellContext.setSpellCounter(currentTick);

            Spell continuousSpell = this.spellContext.getContinuousSpell();

            // Conditions to stop casting the continuous spell
            boolean shouldStop = distanceSq > (double) this.maxAttackDistance
                    || !targetIsVisible
                    || WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(
                    SpellCastEvent.Source.NPC, continuousSpell, attacker,
                    this.spellContext.getModifiers(), currentTick))
                    || !continuousSpell.cast(ctx)
                    || this.continuousSpellTimer == 0;

            if (shouldStop) {
                this.continuousSpellTimer = 0;
                this.cooldown = continuousSpell.getCooldown() + this.baseCooldown;
                setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
                this.spellContext.setSpellCounter(0);
            } else if (currentTick == 1) {
                // First tick of continuous cast completed
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(
                        SpellCastEvent.Source.NPC, continuousSpell, attacker,
                        this.spellContext.getModifiers()));
            }

        }
        // Handle instant spell casting when cooldown expires
        else if (--this.cooldown == 0) {
            if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible || this.seeTime < 5) {
                this.cooldown = 10;
                return;
            }

            List<Spell> spells = new ArrayList<>(this.spellContext.getSpells());

            if (!spells.isEmpty() && !attacker.level().isClientSide) {
                Spell spell;

                while (!spells.isEmpty()) {
                    spell = spells.get(attacker.level().random.nextInt(spells.size()));

                    if (spell != null && spell != Spells.NONE && attemptCastSpell(spell)) {
                        return;
                    } else {
                        spells.remove(spell);
                    }
                }
            }

            // If no spell was cast, reset the cooldown
            this.cooldown = this.baseCooldown;

        } else if (this.cooldown < 0) {
            this.cooldown = this.baseCooldown;
        }
    }

    /**
     * Try to cast the given spell, handling pre-cast and post-cast events.
     *
     * @param spell The spell to cast.
     * @return true if the spell was successfully cast, false otherwise.
     */
    private boolean attemptCastSpell(Spell spell) {
        SpellModifiers modifiers = this.spellContext.getModifiers();

        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(
                SpellCastEvent.Source.NPC, spell, attacker, modifiers))) {
            return false;
        }

        EntityCastContext ctx = new EntityCastContext(
                attacker.level(), attacker, InteractionHand.MAIN_HAND, 0, target, modifiers);

        if (!spell.cast(ctx)) {
            return false;
        }

        // Handle instant and continuous spells
        if (spell.isInstantCast()) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(
                    SpellCastEvent.Source.NPC, spell, attacker, modifiers));
            this.cooldown = this.baseCooldown + spell.getCooldown();

            // Send network packet for visual sync
            if (!attacker.level().isClientSide && spell.requiresPacket()) {
                NPCSpellCastS2C msg = new NPCSpellCastS2C(
                        attacker.getId(), target.getId(), InteractionHand.MAIN_HAND, spell, modifiers);
                Services.NETWORK_HELPER.sendToTracking(attacker, msg);
            }

        } else {
            // Start casting continuous spell
            this.continuousSpellTimer = this.continuousSpellDuration - 1;
            setContinuousSpellAndNotify(spell, modifiers);
            attacker.setTarget(target);

            // Sync target ID for AbstractWizard compatibility
            if (attacker instanceof AbstractWizard wizard) {
                wizard.setSpellTargetId(target.getId());
            }
        }

        return true;
    }

    /**
     * Helper class to abstract spellcasting data access between ISpellCaster and capability systems.
     */
    private static class SpellCastingContext {
        private final Mob mob;
        private final boolean isWizard;
        private ISpellcasterData capabilityData;

        private SpellCastingContext(Mob mob, boolean isWizard, ISpellcasterData capabilityData) {
            this.mob = mob;
            this.isWizard = isWizard;
            this.capabilityData = capabilityData;
        }

        /**
         * Creates a SpellCastingContext from a mob, detecting which system to use.
         * @return context if spellcasting is available, null otherwise
         */
        static SpellCastingContext from(Mob mob) {
            boolean isWizard = mob instanceof AbstractWizard;
            ISpellcasterData capData = null;

            if (!isWizard) {
                // Try to get capability data
                Optional<SpellcasterData> capOpt = mob.getCapability(SpellcasterData.INSTANCE).resolve();
                if (capOpt.isPresent()) {
                    capData = capOpt.get();
                } else {
                    return null; // No spellcasting system available
                }
            }

            return new SpellCastingContext(mob, isWizard, capData);
        }

        /**
         * Refreshes the capability reference (in case it was added dynamically).
         * @return true if spellcasting is still available
         */
        boolean refresh() {
            if (isWizard) return true;
            if (capabilityData != null) return true;

            // Try to reacquire capability
            Optional<SpellcasterData> capOpt = mob.getCapability(SpellcasterData.INSTANCE).resolve();
            if (capOpt.isPresent()) {
                capabilityData = capOpt.get();
                return true;
            }
            return false;
        }

        boolean isActive() {
            return hasSpells();
        }

        boolean hasSpells() {
            List<Spell> spells = getSpells();
            return spells != null && !spells.isEmpty() &&
                    spells.stream().anyMatch(s -> s != null && s != Spells.NONE);
        }

        List<Spell> getSpells() {
            if (isWizard && mob instanceof AbstractWizard wizard) {
                return wizard.getSpells();
            } else if (capabilityData != null) {
                return capabilityData.getSpells();
            }
            return List.of();
        }

        Spell getContinuousSpell() {
            if (isWizard && mob instanceof AbstractWizard wizard) {
                return wizard.getContinuousSpell();
            } else if (capabilityData != null) {
                return capabilityData.getCurrentSpell();
            }
            return Spells.NONE;
        }

        void setContinuousSpell(Spell spell) {
            if (isWizard && mob instanceof AbstractWizard wizard) {
                wizard.setContinuousSpell(spell);
            } else if (capabilityData != null) {
                capabilityData.setCurrentSpell(spell);
            }
        }

        int getSpellCounter() {
            if (isWizard && mob instanceof AbstractWizard wizard) {
                return wizard.getSpellCounter();
            } else if (capabilityData != null) {
                // Capability doesn't track counter by default - return 0
                return 0;
            }
            return 0;
        }

        void setSpellCounter(int count) {
            if (isWizard && mob instanceof AbstractWizard wizard) {
                wizard.setSpellCounter(count);
            }
            // Capability mode: counter handled internally by capability if needed
        }

        SpellModifiers getModifiers() {
            if (isWizard && mob instanceof AbstractWizard wizard) {
                return wizard.getModifiers();
            } else if (capabilityData != null) {
                return capabilityData.getModifiers();
            }
            return new SpellModifiers();
        }
    }
}