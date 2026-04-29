package zettasword.zetta_spells.system.spellcreation.actions.action;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import zettasword.zetta_spells.entity.construct.CosmeticSigil;
import zettasword.zetta_spells.spells.TurnMinion;
import zettasword.zetta_spells.system.ArcaneColor;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;

import java.util.List;
import java.util.UUID;

public class SummonWord extends SpellWord {
    public SummonWord() {
        super("summon");
    }

    /**
     * Allows us to get if key things are considered. Like if there is a word, and is a current target is a spider, and more, obviously.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean shouldCast(SpellCreateContext ctx, List<String> words, int i) {
        return ctx.getPrevious().equals("summon");
    }

    /**
     * Use this to cast the spell you've made and registered.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    /**
     * Summons any registered entity at the target position.
     * Supports flexible name matching: "zombie", "minecraft:creeper", "modid:custom_entity"
     *
     * @param ctx   Spell casting context
     * @param words Command words list
     * @param i     Index of the entity name in words
     * @return true if entity was successfully summoned, false otherwise
     */
    @Override
    public boolean cast(SpellCreateContext ctx, List<String> words, int i) {
        SpellTarget target = ctx.getTarget();
        if (target == null) return false;
        // ── Validate input ────────────────────────────────────────────────
        if (i >= words.size()) return false;
        String entityName = words.get(i);
        if (entityName == null || entityName.isBlank()) return false;

        // ── Resolve EntityType with flexible matching ─────────────────────
        EntityType<?> entityType = findEntityType(entityName);
        if (entityType == null) return false;


        // ── Get target position ───────────────────────────────────────────
        Vec3 targetPos = target.getTargetPos().getCenter();

        Level level = ctx.getWorld();

        // ── Parse modifiers ──────────────────────────────────────
        int count = ctx.getMods().getOrDefault("count", SVar.init(1)).getIntSafe();
        count = Math.max(1, Math.min(count, 20)); // Prevent abuse (configurable)

        int duration = ctx.getMods().getOrDefault("duration", SVar.init(10)).getIntSafe();
        int health = ctx.getMods().getOrDefault("health", SVar.init(1)).getIntSafe();
        int attack_value = ctx.getMod("attack", 1).getInt();

        SVar name = ctx.getMod("name");

        // ── Mana cost: base + scaling ────────────────────────────
        int baseCost = 10;
        int totalCost = (baseCost + (count * 5)) + duration + (health / 5) + (attack_value * 5);
        if (!consumeMana(ctx, totalCost)) return false;

        // ── Server-side: spawn entities ───────────────────────────────────
        if (!level.isClientSide) {
            for (int c = 0; c < count; c++) {
                boolean summon = false;
                Entity entity = entityType.create(level);
                if (entity == null) break;

                // Offset position slightly for multiple spawns (prevents clipping)
                double offsetX = (count > 1) ? (level.random.nextFloat() - 0.5) * 0.5 : 0;
                double offsetZ = (count > 1) ? (level.random.nextFloat() - 0.5) * 0.5 : 0;

                entity.moveTo(
                        targetPos.x + offsetX,
                        targetPos.y,
                        targetPos.z + offsetZ,
                        level.random.nextFloat() * 360f,
                        0f
                );

                if (entity instanceof Mob mob) {
                    if (mob instanceof TamableAnimal tamable){
                        tamable.setTame(true);
                        tamable.setOwnerUUID(ctx.getCaster().getUUID());
                    }
                    TurnMinion.turnMinion(ctx.getCaster(), new SpellModifiers(), mob);
                    TurnMinion.setLifetime(mob, duration * 20);
                    AttributeInstance attack = mob.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (attack != null){
                        attack.addPermanentModifier(new AttributeModifier("summon_attack", attack_value,
                                AttributeModifier.Operation.ADDITION));
                    }
                    if (name != null) mob.setCustomName(Component.literal(name.getString()));
                    summon = true;
                }

                if (entity instanceof MagicConstructEntity construct) {
                    construct.setCaster(ctx.getCaster());
                    construct.lifetime = duration * 20;
                    if (construct instanceof ScaledConstructEntity scaled){
                        SVar width = ctx.getMod("width");
                        SVar height = ctx.getMod("height");
                        if (width != null && height != null && consumeMana(ctx, (width.getInt() * 10)+(height.getInt() * 10)))
                            scaled.setSize(width.getInt(), height.getInt());
                    }
                    summon = true;
                }
                // Optional: set persistent if needed (prevents despawn)
                // entity.setPersistenceRequired();
                if (summon) {
                    // We spawn entity
                    level.addFreshEntity(entity);
                }
            }
            ctx.addCooldown(Math.max(duration/20, 1));
        }

        // ── Client-side: visual feedback only ─────────────────────────────
        // We create sigil at the place where entity will be!
        if (!level.isClientSide && ctx.canCreateFx()) {
            CosmeticSigil sigil = new CosmeticSigil(ctx.getWorld());
            sigil.setLocation(WizardryMainMod.location("textures/entity/arcane_workbench_rune.png"));
            sigil.setLifetime(40);
            sigil.setCaster(ctx.getCaster());
            sigil.setPos(targetPos);
            sigil.addDeltaMovement(new Vec3(0, 0.5,0));
            ctx.getWorld().addFreshEntity(sigil);
        }

        // Particle effect
        if (level.isClientSide && ctx.canCreateFx()) {
            ParticleBuilder.create(EBParticles.BUFF)
                    .pos(targetPos)
                    .color(ArcaneColor.ARCANE)
                    .time(40)
                    .spawn(level);
        }
        return true;
    }

    /**
     * Resolves an EntityType from a name string with flexible matching:
     * 1. Exact ResourceLocation match (e.g., "minecraft:zombie", "modid:boss")
     * 2. Fallback to "minecraft:" prefix for vanilla entities (e.g., "cow" → "minecraft:cow")
     * 3. Optional: fuzzy match by path only (use with caution for ambiguity)
     *
     * @param name The entity name from user input
     * @return The resolved EntityType, or null if not found
     */
    private EntityType<?> findEntityType(String name) {
        if (name == null || name.isBlank()) return null;

        // ── 3. Optional: Fuzzy match by path only
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            ResourceLocation loc = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (loc != null && loc.getPath().equalsIgnoreCase(name)) {
                return type; // First match wins
            }
        }

        // ── 1. Try exact ResourceLocation match ───────────────────────────
        try {
            ResourceLocation exactLoc = ResourceLocation.parse(name);
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(exactLoc);
            if (type != null) return type;
        } catch (ResourceLocationException e) {
            // Invalid format, continue to fallback
        }

        // ── 2. Try vanilla fallback: assume "minecraft:" prefix ───────────
        if (!name.contains(":")) {
            ResourceLocation vanillaLoc = ResourceLocation.tryBuild("minecraft", name);
            if (vanillaLoc != null) {
                return ForgeRegistries.ENTITY_TYPES.getValue(vanillaLoc);
            }
        }

        return null;
    }
}
