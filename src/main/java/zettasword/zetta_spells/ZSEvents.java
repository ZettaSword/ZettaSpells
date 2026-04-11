package zettasword.zetta_spells;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.SpellManagerData;
import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.Forfeit;
import com.binaris.wizardry.content.ForfeitRegistry;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.zetta_spells.capability.Race;
import zettasword.zetta_spells.capability.RaceDataHolder;
import zettasword.zetta_spells.enchantments.ZSEnchantments;
import zettasword.zetta_spells.entity.ZSEntities;
import zettasword.zetta_spells.entity.construct.CosmeticSigil;
import zettasword.zetta_spells.entity.construct.DeathVesselEntity;
import zettasword.zetta_spells.entity.construct.SystemCall;
import zettasword.zetta_spells.items.ZSItems;
import zettasword.zetta_spells.mob_effects.ZSEffects;
import zettasword.zetta_spells.system.Alchemy;
import zettasword.zetta_spells.system.particles.Alteria;

import java.util.Random;

import static zettasword.zetta_spells.mob_effects.MagicRestorationMobEffect.rechargeMana;

@Mod.EventBusSubscriber(modid = ZettaSpellsMod.MODID)
public class ZSEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            // Check Main Hand
            ItemStack stack = attacker.getMainHandItem();

            if (!stack.isEmpty()) {
                // Enhance Armament enchantment
                int level = stack.getEnchantmentLevel(ZSEnchantments.ENHANCE_ARMAMENT.get());
                LivingEntity living = event.getEntity();
                Level world = living.level();

                if (level > 0) {
                    // Apply 1.5x Damage Multiplier
                    float multiplier = 1.5f;
                    event.setAmount(event.getAmount() * multiplier);
                    if (world.isClientSide) {
                        for (int j = 0; (float) j < 20; ++j) {
                            double x = living.xo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            double y = living.yo + (double) living.getEyeHeight() - (double) 0.5F + world.random.nextDouble();
                            double z = living.zo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z)
                                    .velocity((double) 0.0F, 0.1, (double) 0.0F)
                                    .color(0xFF9800).time(20)
                                    .spawn(world);
                        }
                    }

                    if (!world.isClientSide){
                        //Spawning System call sigil
                        SystemCall sigil = new SystemCall(world);
                        sigil.lifetime = 20;
                        sigil.setCaster(attacker);
                        sigil.setPos(living.getPosition(1.0F));
                        world.addFreshEntity(sigil);
                    }
                }

                // Release Recollection enchantment
                level = stack.getEnchantmentLevel(ZSEnchantments.RELEASE_RECOLLECTION.get());
                if (level > 0) {
                    // Apply 1.5x Damage Multiplier
                    float multiplier = 1.5f;
                    event.setAmount(event.getAmount() * multiplier);
                    if (!world.isClientSide){
                        //Spawning System call sigil
                        SystemCall sigil = new SystemCall(world);
                        sigil.lifetime = 20;
                        sigil.setCaster(attacker);
                        sigil.setPos(living.getPosition(1.0F));
                        world.addFreshEntity(sigil);
                    }
                }

                // Some aspects are available through spell infusions, but most are obtainable only through Release Recollection.
                //Frost Aspect Enchantment
                level = stack.getEnchantmentLevel(ZSEnchantments.FROST_ASPECT.get());
                if (level > 0) {
                    if (world.isClientSide) {
                        for (int j = 0; (float) j < 20; ++j) {
                            double x = living.xo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            double y = living.yo + (double) living.getEyeHeight() - (double) 0.5F + world.random.nextDouble();
                            double z = living.zo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            ParticleBuilder.create(EBParticles.ICE).pos(x, y, z).time(20)
                                    .velocity((double) 0.0F, 0.1, (double) 0.0F)
                                    .spawn(world);
                        }
                        if (Elements.ICE.getColor().getColor() != null)
                            ParticleBuilder.create(EBParticles.BUFF).entity(living).color(Elements.ICE.getColor().getColor()).spawn(world);
                    }

                    if (!world.isClientSide){
                        //This aspect makes entities freezes entities.
                        Alchemy.apply(living, EBMobEffects.FROST.get(), Math.max(10, 10*level), 0);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMiningSpeed(PlayerEvent.BreakSpeed event){
        if (event.getEntity().getMainHandItem().getEnchantmentLevel(ZSEnchantments.ENHANCE_ARMAMENT.get()) > 0){
            event.setNewSpeed(event.getNewSpeed() * 1.5F);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event){
        Player livingEntity = event.player;

        if (!livingEntity.level().isClientSide && livingEntity.tickCount % 10 == 0 && livingEntity.hasEffect(ZSEffects.MAGIC_RESTORATION.get())) {
            MobEffectInstance effect = livingEntity.getEffect(ZSEffects.MAGIC_RESTORATION.get());
            if (effect == null) return;
            ItemStack stack = livingEntity.getMainHandItem();
            if (rechargeMana(livingEntity, effect.getAmplifier(), stack)) return;
            stack = livingEntity.getOffhandItem();
            rechargeMana(livingEntity, effect.getAmplifier(), stack);
        }
        if (!livingEntity.level().isClientSide && livingEntity.tickCount % 150 == 0) {
            Race.get(livingEntity).ifPresent((cap) -> {
                if (cap.getRace().equals("vampire")){
                    //Alchemy.apply(livingEntity, MobEffects., 15, 0);
                }
            });
        }

        // Race ability cooldown
        if (!event.player.level().isClientSide && livingEntity.tickCount % 20 == 0)
            Race.get(livingEntity).ifPresent((race)-> {if (race.getAbilityCooldown() > 0) race.reduceAbilityCooldown(20);});
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        Player player = event.getEntity();

        if (!Race.get(player).isPresent()) return;
        if (target.getMobType() != MobType.UNDEAD) return;

        /*Race.get(player).ifPresent((race)->{
            if (race.getRace().equals("vampire") && player.isCrouching()){
                if (race.getAbilityCooldown() == 0) {
                    race.setAbilityCooldown(200);
                    target.hurt(target.damageSources().magic(), 4);
                    player.heal(3);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }else{
                    player.sendSystemMessage(Component.translatable("race.cooldown"));
                }
            }
        });*/
    }

    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(RaceDataHolder.LOCATION, new RaceDataHolder.Provider(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return; // Only copy data when player respawns after death

        // Revive the original player's capabilities to be able to read them
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(RaceDataHolder.INSTANCE).ifPresent(old ->
                event.getEntity().getCapability(RaceDataHolder.INSTANCE).ifPresent(holder ->
                        holder.copyFrom(old)));
    }

    private static final String TAG_RESURRECTED = "zetta_spells:no_drop";

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity dyingEntity = event.getEntity();

        if (dyingEntity instanceof Player) {
            return;
        }
        if (!(dyingEntity instanceof Mob)) return;

        // Optional: Filter specific mobs if you don't want this for everything
        // if (!(dyingEntity instanceof Monster)) return;

        Level level = dyingEntity.level();

        if (!level.isClientSide) {
            // Create the Soul Vessel
            DeathVesselEntity vessel = new DeathVesselEntity(ZSEntities.DEATH_VESSEL.get(), level);
            vessel.setPos(dyingEntity.position());

            // Store the data
            vessel.storeEntityData(dyingEntity);
            vessel.lifetime = 300;

            // Spawn the vessel
            level.addFreshEntity(vessel);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag persistentData = entity.getPersistentData();

        // 3. Check if drops should be suppressed
        if (persistentData.contains(TAG_RESURRECTED) && persistentData.getBoolean(TAG_RESURRECTED)) {
            ZettaSpellsMod.LOGGER.warn("Resurrected entity: {}", entity.getName().getString());
            event.setCanceled(true); // Prevent all drops
            persistentData.remove(TAG_RESURRECTED); // Cleanup tag
        }
    }

    // Custom events of EBWiz Redux

    public static void register() {
        WizardryEventBus bus = WizardryEventBus.getInstance();
        bus.register(SpellCastEvent.Pre.class, ZSEvents::onPreCast);
        bus.register(SpellCastEvent.Tick.class, ZSEvents::onTickCast);
        bus.register(SpellCastEvent.Post.class, ZSEvents::onAfterCast);
    }

    public static void onPreCast(SpellCastEvent.Pre event){
        if (event.getCaster() instanceof  Player player){
            if (event.getSource() != SpellCastEvent.Source.WAND && event.getSource() != SpellCastEvent.Source.SCROLL) return;
            if (player.isCreative()) return;
            Spell spell = event.getSpell();
            SpellManagerData spellData = Services.OBJECT_DATA.getSpellManagerData(player);
            WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);

            if (!spellData.hasSpellBeenDiscovered(spell)) return;

            Race.get(player).ifPresent((data) -> {
                int knowledge = data.getSpellKnowledge(spell);
                float chance = (float) EBConfig.FORFEIT_CHANCE.get();
                if (ArtifactChannel.isEquipped(player, EBItems.AMULET_WISDOM.get())) chance *= 0.5F;
                if (knowledge < 12){
                    Random random = wizardData.getRandom();
                    float roll = random.nextFloat();
                    Forfeit forfeit = ForfeitRegistry.getRandomForfeit(random, spell.getTier(), spell.getElement());
                    if (forfeit == null) return;
                    boolean shouldTrigger = roll < chance;
                    if (shouldTrigger){
                        forfeit.apply(event.getLevel(), player);

                        if (player instanceof ServerPlayer) EBAdvancementTriggers.SPELL_FAILURE.triggerFor(player);
                        EntityUtil.playSoundAtPlayer(player, forfeit.getSound(), 1, 1);
                        event.setCanceled(true);
                    }
                }

                // Adding to spell potency and more!
                if (knowledge > 0 && !event.getLevel().isClientSide){
                    SpellModifiers mods = event.getModifiers();
                    mods.set(SpellModifiers.POTENCY, Math.min(mods.get(SpellModifiers.POTENCY) + (knowledge * 0.0001F), 100.0F)); // Yeah, crazy stuff, I know.
                    mods.set(SpellModifiers.COST, Math.max(mods.get(SpellModifiers.POTENCY) - (knowledge * 0.0001F), 0.5F));
                    event.getModifiers().combine(mods);
                }
            });
        }
    }

    public static void onTickCast(SpellCastEvent.Tick event){

    }

    public static void onAfterCast(SpellCastEvent.Post event){
        if (event.getCaster() instanceof Player player){
            if (event.isCanceled()) return;
            Level level = event.getLevel();
            Spell spell = event.getSpell(); // For read-ability
            if (Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell) && !player.isCreative()) {
                Race.get(player).ifPresent((data) -> {
                    data.addSpellKnowledge(spell, 1);
                    int knowledge = data.getSpellKnowledge(spell);
                    if (knowledge < 12) {
                        if (!level.isClientSide) {
                            player.displayClientMessage(Component.translatable("spell_knowledge.learning",
                                    (int) ((knowledge / 12F) * 100F)).withStyle(ChatFormatting.YELLOW), true);
                        }
                        if (level.isClientSide){
                            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
                        }
                    }
                    if (knowledge == 12){
                        if (!level.isClientSide)
                            player.displayClientMessage(Component.translatable("spell_knowledge.learned").withStyle(ChatFormatting.GOLD), true);
                        if (level.isClientSide){
                            Alteria.spawnEnchantmentParticles(player, 20, 2.0D, 2.0D);
                            player.playSound(SoundEvents.PLAYER_LEVELUP);
                        }
                    }
                });

            }

            if (player.hasEffect(ZSEffects.UNDEAD.get()) && !level.isClientSide) {
                if (spell.getElement() == Elements.NECROMANCY && spell.getTier() == SpellTiers.MASTER) {
                    Race.get(player).ifPresent((m) -> m.setRace("vampire"));

                    // Visual effects to show Tenebria's Attention
                    CosmeticSigil sigil = new CosmeticSigil(level);
                    sigil.setLocation(ZettaSpellsMod.location("textures/sigils/old/circle_vampires.png"));
                    sigil.lifetime = 40;
                    sigil.setCaster(player);
                    sigil.setPos(player.getPosition(1.0F));
                    level.addFreshEntity(sigil);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(ZSItems.NECROMANCER_STAFF.get())) {
            event.setCanceled(true); // Stops vanilla armor swap

            // Run your custom usage here
            event.getItemStack().use(event.getLevel(), event.getEntity(), event.getHand());
        }
    }

    @SubscribeEvent
    public static void onBreath(LivingBreatheEvent event){
        if (event.getEntity() instanceof Player player){
            Race.get(player).ifPresent((data) -> {
                if (data.getRace().equals("fishman")){
                    if (player.isUnderWater()){
                        event.setCanBreathe(true);
                        event.setCanRefillAir(true);
                    }else{
                        event.setCanBreathe(false);
                        event.setCanRefillAir(false);
                    }
                }
            });
        }
    }

}
