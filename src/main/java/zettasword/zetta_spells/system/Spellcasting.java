package zettasword.zetta_spells.system;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.data.ImbuementEnchantData;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.capability.RaceDataHolder;
import zettasword.zetta_spells.enchantments.ZSEnchantments;
import zettasword.zetta_spells.entity.construct.*;
import zettasword.zetta_spells.mob_effects.ZSEffects;
import zettasword.zetta_spells.system.particles.Alteria;

import javax.annotation.Nullable;
import java.util.List;

public class Spellcasting {

    public static void spellCast(Level world, Player caster, InteractionHand hand, String spell){
        List<String> words = TextProcessingUtil.extractWords(spell);

        String previous = "";
        SpellTarget target = new SpellTarget();
        target.setTarget(caster);
        boolean ignoreLivingEntities = false;
        // Duration in seconds.
        int duration = 10;
        // Power of the potion effect;
        int amplification = 1;
        // General power of the spell actions, like 'toras' word.
        int power = 1;
        HitResult lastRay = null;
        boolean particlesDefault = true;
        boolean sigilDefault = true;
        Direction hit_direction = Direction.UP;
        SpellModifiers modifiers = CastItemUtils.calculateModifiers(caster.getItemInHand(hand), caster, Spells.NONE);
        for (int i = 0; i < words.size(); ++i){
            String current = words.get(i);
            String next = getNext(words, i, 1);

            int number = getInt(next);

            if (current.equals("duratio") && number > 0)    duration = number;
            if (current.equals("amplify") && number > 0)    amplification = number; // Always keeping it above 0
            if (current.equals("power") && number > 0)      power = number;

            if (current.equals("ekta")) ignoreLivingEntities = true;
            if (current.equals("ikta")) ignoreLivingEntities = false;

            // Returns target to caster.
            if (current.equals("lita")){
                target.setTarget(caster);
            }

            // Raycast one entity and it's position, or one block.
            if (current.equals("rute")){
                Vec3 look = caster.getLookAngle();
                Vec3 origin = new Vec3(caster.getX(), caster.getY() + (double)caster.getEyeHeight() - (double)0.25F, caster.getZ());
                if (world.isClientSide && ClientUtils.isFirstPerson(caster)) {
                    origin = origin.add(look.scale(1.2));
                }
                double range = 14 * modifiers.get(SpellModifiers.RANGE);
                Vec3 endpoint = origin.add(look.scale(range));
                HitResult rayTrace = RayTracer.rayTrace(world, caster, origin, endpoint, 0.0F, false, Entity.class, ignoreLivingEntities ? EntityUtil::isLiving : RayTracer.ignoreEntityFilter(caster));
                if (rayTrace != null) {
                    if (rayTrace instanceof EntityHitResult entityHit) {
                        target.setTarget(entityHit.getEntity());
                        range = origin.distanceTo(rayTrace.getLocation());
                    } else if (rayTrace instanceof BlockHitResult blockHit) {
                        target.setTarget(blockHit.getBlockPos());
                        hit_direction = blockHit.getDirection();
                        range = origin.distanceTo(rayTrace.getLocation());
                    }

                    if (world.isClientSide && target.getTargetPos() != null){
                        BlockPos targetPos = target.getTargetPos();

                        double x = targetPos.getX();
                        double y = targetPos.getY();
                        double z = targetPos.getZ();

                        ParticleBuilder.create(EBParticles.BEAM).entity(caster).pos(origin.subtract(caster.position())).length(range).color(0xFF9800)
                                .scale(2).time(20).spawn(world);

                        ParticleBuilder.create(EBParticles.SPHERE).pos(x + 0.5, y + 0.5, z + 0.5).color(0xFF9800).time(20).scale(1.0F)
                                .spawn(world);
                    }
                    lastRay = rayTrace;
                }
            }

            // Only for Caster!
            if (target.getTargetEntity() instanceof Player && target.getTargetEntity() == caster){
                InteractionHand opposite_hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                ItemStack opposite = caster.getItemInHand(opposite_hand);
                if (!opposite.isEnchanted() && current.equals("encant")){ // Enchanting!
                    if (opposite.isEmpty()) continue;

                    if (next.equals("fiery")){
                        if (!world.isClientSide) {
                            opposite.enchant(Enchantments.FIRE_ASPECT, 2);
                            opposite.enchant(Enchantments.VANISHING_CURSE, 1);
                            opposite.setRepairCost(40);
                            caster.inventoryMenu.broadcastChanges();
                        }
                        spawnEnchantmentParticles(world, caster);
                        particlesDefault = false;
                    }

                    if (next.equals("protecto")){
                        if (!world.isClientSide) {
                            opposite.enchant(Enchantments.UNBREAKING, 1);
                            opposite.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 2);
                            opposite.enchant(Enchantments.VANISHING_CURSE, 1);
                            opposite.setRepairCost(40);
                            caster.inventoryMenu.broadcastChanges();
                        }
                        spawnEnchantmentParticles(world, caster);
                        particlesDefault = false;
                    }
                }

                if (current.equals("imbue")){
                    ImbuementEnchantData data = Services.OBJECT_DATA.getImbuementData(opposite);
                    if (data == null) continue;
                    long duration_time = world.getGameTime() + (duration * 20L);
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(getNext(words, i, 2)));
                    if (enchantment != null && !world.isClientSide){
                        opposite.enchant(enchantment, power);
                        data.addImbuement(enchantment, duration_time);
                        caster.inventoryMenu.broadcastChanges();
                    }
                    spawnEnchantmentParticles(world, caster);
                    particlesDefault = false;
                }

                // Easter egg :D
                if (!opposite.isEnchanted() && previous.equals("system") && current.equals("call")){
                    if (!opposite.isDamageableItem()) continue;
                    if (next.equals("enhance") && getNext(words, i, 2).equals("armament")){
                        if (!opposite.isEmpty() && !world.isClientSide){
                            opposite.enchant(ZSEnchantments.ENHANCE_ARMAMENT.get(), 1);
                            setDurabilityToHalf(opposite);
                            opposite.setRepairCost(40);
                            caster.inventoryMenu.broadcastChanges();
                        }
                    }

                    if (next.equals("release") && getNext(words, i, 2).equals("recollection")){
                        if (!opposite.isEmpty() && !world.isClientSide){
                            opposite.enchant(ZSEnchantments.RELEASE_RECOLLECTION.get(), 1);

                            if (world.getBlockState(caster.getOnPos()).getBlock() instanceof IceBlock){
                                opposite.enchant(ZSEnchantments.FROST_ASPECT.get(), 1);
                            }

                            if (world.getBlockState(caster.getOnPos()).getBlock() instanceof SandBlock){
                                opposite.enchant(Enchantments.FIRE_ASPECT, 1);
                            }

                            if (caster.isUnderWater()){
                                opposite.enchant(Enchantments.CHANNELING, 1);
                                opposite.enchant(Enchantments.AQUA_AFFINITY, 1);
                            }

                            if (world.dimension() == Level.NETHER){
                                opposite.enchant(Enchantments.FIRE_PROTECTION, 2);
                                opposite.enchant(Enchantments.FIRE_ASPECT, 2);
                            }
                            //TODO: Add End Aspect enchantment

                            if (opposite.getItem().canPerformAction(opposite, ToolActions.PICKAXE_DIG)
                                    || opposite.getItem().canPerformAction(opposite, ToolActions.SHOVEL_DIG)
                                    || opposite.getItem().canPerformAction(opposite, ToolActions.AXE_DIG)){
                                opposite.enchant(Enchantments.BLOCK_EFFICIENCY, 3);
                            }

                            if (opposite.getItem() instanceof CrossbowItem || opposite.getItem() instanceof BowItem){
                                opposite.enchant(Enchantments.INFINITY_ARROWS, 1);
                                opposite.enchant(Enchantments.FLAMING_ARROWS, 1);
                            }

                            if (opposite.getItem() instanceof TridentItem){
                                opposite.enchant(Enchantments.LOYALTY, 5);
                                opposite.enchant(Enchantments.CHANNELING, 1);
                            }

                            opposite.enchant(Enchantments.VANISHING_CURSE, 1);
                            opposite.setRepairCost(40);

                            setRemainingDurability(opposite, 10);
                            caster.inventoryMenu.broadcastChanges();
                        }
                    }

                    if (!world.isClientSide){
                        SystemCall sigil = new SystemCall(world);
                        sigil.lifetime = 20;
                        sigil.setCaster(caster);
                        sigil.setPos(caster.getPosition(1.0F));
                        world.addFreshEntity(sigil);
                    }
                    sigilDefault = false;
                }
            }


            // Affect Living entity
            if (target.getTargetEntity() instanceof LivingEntity living) {

                // Gods Blessings!
                if (previous.equals("tenebria")){
                    int darkness_alignment = 0;
                    for (ItemStack stack : caster.getArmorSlots()) {
                        if (stack.getItem() instanceof WizardArmorItem wizardry) {
                            if (wizardry.getElement() == Elements.NECROMANCY) {
                                darkness_alignment++;
                            }
                        }
                    }
                    // Nope, she's not going to help you if you don't wear anything Necromancy related lol
                    if (darkness_alignment <= 0) continue;

                    // Spawning Necromancy particles.
                    if (world.isClientSide && Elements.NECROMANCY.getColor().getColor() != null) {

                        for (int j = 0; (float) j < (10 + (darkness_alignment * 5)); ++j) {
                            double x = caster.xo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            double y = caster.yo + (double) caster.getEyeHeight() - (double) 0.5F + world.random.nextDouble();
                            double z = caster.zo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z)
                                    .velocity((double) 0.0F, 0.1, (double) 0.0F)
                                    .color(Elements.NECROMANCY.getColor().getColor())
                                    .spawn(world);
                        }
                    }

                    // Since we already replace default particles.
                    particlesDefault = false;

                    if (current.equals("protect") && next.equals("us")){
                        if (!world.isClientSide) {
                            Alchemy.apply(living, MobEffects.DARKNESS, 5, 4);
                            Alchemy.apply(living, MobEffects.DAMAGE_RESISTANCE, 60, 1);

                            TenebriaProtectionSigil sigil = new TenebriaProtectionSigil(world);
                            sigil.lifetime = Math.max(10, duration * 20);
                            sigil.setCaster(caster);
                            sigil.setPos(caster.getPosition(1.0F));
                            world.addFreshEntity(sigil);
                        }
                        sigilDefault = false;
                    }

                    if (current.equals("help") && next.equals("us") && getNext(words, i, 2).equals("fight")){
                        if (!world.isClientSide) { // TODO: Change this effect to something useful
                            Alchemy.apply(living, MobEffects.DARKNESS, 5, 4);
                            Alchemy.apply(living, MobEffects.DAMAGE_BOOST, 120, 1);
                            // Visual effects to show Tenebria's Attention
                            TenebriaWillSigil sigil = new TenebriaWillSigil(world);
                            sigil.lifetime = 40;
                            sigil.setCaster(caster);
                            sigil.setPos(caster.getPosition(1.0F));
                            world.addFreshEntity(sigil);
                        }
                        sigilDefault = false;
                    }

                    if (current.equals("bless") && next.equals("us")) {
                        if (!world.isClientSide) {
                            Alchemy.apply(living, MobEffects.DARKNESS, 5 + 15 * world.random.nextDouble(), 4);
                            Alchemy.apply(living, MobEffects.UNLUCK, 600, 0);
                            Alchemy.apply(living, 300, 0, MobEffects.NIGHT_VISION, ZSEffects.UNDEAD.get());
                            // Visual effects to show Tenebria's Attention
                            TenebriaWillSigil sigil = new TenebriaWillSigil(world);
                            sigil.lifetime = 40;
                            sigil.setCaster(caster);
                            sigil.setPos(caster.getPosition(1.0F));
                            world.addFreshEntity(sigil);
                        }

                        sigilDefault = false;
                    }
                }

                if (previous.equals("nox")){
                    if (current.equals("bless") && next.equals("me")) {
                        if (!world.isClientSide) {
                            // Turning player vampire
                            // TODO: Make Vampire not just glow lol :D
                            caster.getCapability(RaceDataHolder.INSTANCE).ifPresent((m)-> m.setRace("vampire"));

                            Alchemy.apply(living, MobEffects.DARKNESS, 5 + 15 * world.random.nextDouble(), 4);
                            Alchemy.apply(living, MobEffects.POISON, 600, 0);
                            // Visual effects to show Tenebria's Attention
                            CosmeticSigil sigil = new CosmeticSigil(world);
                            sigil.setLocation(ZettaSpells.location("textures/sigils/old/circle_vampires.png"));
                            sigil.lifetime = 40;
                            sigil.setCaster(caster);
                            sigil.setPos(caster.getPosition(1.0F));
                            world.addFreshEntity(sigil);
                        }
                        sigilDefault = false;
                    }
                }

                // Apply any effect.
                try {
                    if (previous.equals("apote") && ForgeRegistries.MOB_EFFECTS.containsKey(ResourceLocation.parse(current))) {
                        MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(current));
                        if (mobEffect != null) {
                            if (!world.isClientSide) {
                                living.addEffect(new MobEffectInstance(mobEffect, duration * 20, amplification - 1, false, false));
                            }
                            // Client side
                            if (world.isClientSide) {
                                ParticleBuilder.create(EBParticles.BUFF).entity(living).color(mobEffect.getColor()).spawn(world);
                            }
                        }
                    }
                }catch (Exception ignore){}

                if (current.equals("heato")){
                    if (!world.isClientSide) {
                        living.addEffect(new MobEffectInstance(ZSEffects.HEAT.get(), duration * 20, amplification - 1, false, false));
                    }
                    // Client side
                    if (world.isClientSide) {
                        ParticleBuilder.create(EBParticles.BUFF).entity(living).color(ZSEffects.HEAT.get().getColor()).spawn(world);
                    }
                }

                // Ignition
                if (current.equals("solas")){
                    // Server side
                    if (!world.isClientSide) {
                        living.setSecondsOnFire(duration);
                    }
                }
                if (current.equals("mundo") && !world.isClientSide){
                    MagicChains entity = new MagicChains(world);
                    entity.setPos(target.getTargetPos().getCenter());
                    entity.lifetime = Math.max(1200, duration * 20);
                    entity.setCaster(caster);
                    world.addFreshEntity(entity);
                }
            }

            // Affect Block
            if (target.getTargetPos() != null){
                BlockPos pos = target.getTargetPos();
                BlockPos relativePos = pos.relative(hit_direction);
                if (current.equals("ignitio")){
                    if (world.isEmptyBlock(relativePos)){
                        if (!world.isClientSide && BlockUtil.canPlaceBlock(caster, world, relativePos)) {
                            world.setBlockAndUpdate(relativePos, Blocks.FIRE.defaultBlockState());
                        }
                    }
                }

                if (current.equals("uter")){
                    if (!world.isEmptyBlock(pos)){
                            if (!world.isClientSide && BlockUtil.canBreak(caster, world, pos, false)) {
                                world.destroyBlock(pos, true, caster);
                            }
                            if (world.isClientSide){
                                Alteria.spawnBlockOutlineParticles(world, pos, ParticleTypes.HAPPY_VILLAGER, 5);
                            }
                    }
                }

                if (current.equals("modifi")){
                    // Lit lamp:
                    // Rute modifi lit!
                    if (!world.isEmptyBlock(pos)){
                        if (next.isEmpty()) next = "nothing";
                        if (!world.isClientSide) {
                            if (!BlockStateUtils.toggleBooleanProperty(world, pos, next)) {
                                // This thing does not work. Why? IDK
                                // TODO:Make integer properties change!
                                BlockStateUtils.setIntProperty(world, pos, next, getInt(getNext(words, i, 2)));
                            }
                        }
                        if (world.isClientSide){
                            Alteria.spawnBlockOutlineParticles(world, pos, ParticleTypes.HAPPY_VILLAGER, 5);
                        }
                    }
                }

                if (previous.equals("pos")){
                    if (number <= 0) continue;
                    if (current.equals("ba")){ // Back
                        target.setTargetPos(pos.relative(caster.getDirection().getOpposite(), number));
                    }
                    if (current.equals("fo")){ // Forth
                        target.setTargetPos(pos.relative(caster.getDirection(), number));
                    }
                    if (current.equals("ri")){ // Right
                        target.setTargetPos(pos.relative(caster.getDirection().getClockWise(), number));
                    }
                    if (current.equals("li")){ // Left
                        target.setTargetPos(pos.relative(caster.getDirection().getCounterClockWise(), number));
                    }
                    if (current.equals("di")){ //Down
                        target.setTargetPos(pos.relative(Direction.DOWN, number));
                    }
                    if (current.equals("up")){ //Up
                        target.setTargetPos(pos.relative(Direction.UP, number));
                    }
                    if (current.equals("casto")){ // Following caster look
                        Vec3 lookVec = caster.getLookAngle().scale(number);
                        target.setTargetPos(pos.offset((int) Math.round(lookVec.x), (int) Math.round(lookVec.y), (int) Math.round(lookVec.z)));
                    }
                    pos = target.getTargetPos();
                }

                // Allows placing of blocks at selected position
                if (current.equals("place")){
                    InteractionHand opposite_hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    ItemStack opposite = caster.getItemInHand(opposite_hand);
                    if (!opposite.isEmpty() && opposite.getItem() instanceof BlockItem blockItem) {
                        if (BlockUtil.canBlockBeReplaced(world, pos) && BlockUtil.canPlaceBlock(caster, world, pos)) {
                            if (!world.isClientSide) {
                                world.setBlock(pos, blockItem.getBlock().defaultBlockState(), 3);
                                opposite.shrink(1);
                            }
                            if (world.isClientSide){
                                Alteria.spawnBlockOutlineParticles(world, pos, EBParticles.SPARKLE, 0xFF9800, 5);
                            }
                        }
                    }
                }

                if (current.equals("transpero")){
                    if (!world.isClientSide){
                        int x=getInt(getNext(words,i,2)), y=getInt(getNext(words,i,3)), z=getInt(getNext(words,i,4));
                        if (x == 0 && y == 0 && z == 0) continue; // Skip if all are 0.
                        BlockState state = world.getBlockState(pos); // Original position
                        BlockPos pos2 = pos.offset(new BlockPos(x,y,z));
                        BlockPos pos2neg = pos.offset(new BlockPos(-x,-y,-z));

                        //BlockState state2 = world.getBlockState(pos2); // New position
                        //BlockState state2neg = world.getBlockState(pos2neg); // New position
                        // Earth Wall spell:
                        // Rute transpero apet 0 3 0 pos di 1 transpero apet 0 3 0 pos di 1 transpero apet 0 3 0

                        if (next.equals("apet")) {
                            //world.setBlock(pos2, state, 3); // Replace old block with new one
                            //world.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                            swapBlocks(world, pos, pos2);
                        }

                        if (next.equals("deas")) {
                            //world.setBlock(pos2neg, state,3); // Replace old block with new one
                            //world.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                            swapBlocks(world, pos, pos2neg);
                        }
                    }
                }
            }

            previous = words.get(i);
        }

        // After ALL the spell work.
        if (!world.isClientSide && sigilDefault){
            // Hehe i made cooldown hehe.... hehe.
            caster.getCooldowns().addCooldown(caster.getItemInHand(hand).getItem(), 5 + duration + (amplification) * (amplification));
            // Visual effects, but they're actually entities so we spawn them.
            CosmeticSigil sigil = new CosmeticSigil(world);
            sigil.setLocation(WizardryMainMod.location("textures/entity/healing_aura.png"));
            sigil.lifetime = 40;
            sigil.setCaster(caster);
            sigil.setPos(caster.getPosition(1.0F));
            world.addFreshEntity(sigil);
        }

        if (world.isClientSide) {
            if (particlesDefault) {
                for (int j = 0; (float) j < 20; ++j) {
                    double x = caster.xo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                    double y = caster.yo + (double) caster.getEyeHeight() - (double) 0.5F + world.random.nextDouble();
                    double z = caster.zo + world.random.nextDouble() * (double) 2.0F - (double) 1.0F;
                    ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z)
                            .velocity((double) 0.0F, 0.1, (double) 0.0F)
                            .color(0xFF9800)
                            .spawn(world);
                }
            }

            caster.playSound(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.7F, 1.0F);
        }
    }

    private static void spawnEnchantmentParticles(Level world, Player caster) {
        if (world.isClientSide) {
            // 1. Define the target position (Caster's current position + eye height)
            double targetX = caster.getX();
            double targetY = caster.getY() + caster.getEyeHeight();
            double targetZ = caster.getZ();

            for (int j = 0; j < 20; ++j) {
                // 2. Calculate spawn position
                double x = caster.xo + world.random.nextDouble() * 2.0D - 1.0D;
                double y = caster.yo + (double) caster.getEyeHeight() - 0.5D + world.random.nextDouble();
                double z = caster.zo + world.random.nextDouble() * 2.0D - 1.0D;

                // 3. Calculate velocity vector (Target - Spawn)
                double motionX = targetX - x;
                double motionY = targetY - y;
                double motionZ = targetZ - z;

                // 4. Normalize and scale speed
                // This ensures particles travel at the same speed regardless of where they spawn
                double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
                if (length > 0) {
                    double speed = 0.5;
                    motionX = (motionX / length) * speed;
                    motionY = (motionY / length) * speed;
                    motionZ = (motionZ / length) * speed;
                }

                // 5. Spawn particle with calculated velocity
                world.addParticle(ParticleTypes.ENCHANT, x, y, z, motionX, motionY, motionZ);
            }
        }
    }

    /**
     * Sets the item's durability to 50% of its maximum.
     * (e.g., if Max is 100, sets damage to 50, leaving 50 uses)
     */
    public static void setDurabilityToHalf(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageableItem()) {
            return;
        }

        int maxDamage = stack.getDamageValue();

        // Setting damage to half means half durability remains
        int halfDamage = Math.max(0, maxDamage / 2);

        stack.setDamageValue(halfDamage);
    }

    /**
     * Damages the item so that only 10 durability points remain.
     * (e.g., if Max is 100, sets damage to 90, leaving 10 uses)
     */
    public static void setRemainingDurability(ItemStack stack, int durabilityLeft) {
        if (stack.isEmpty() || !stack.isDamageableItem()) {
            return;
        }

        int maxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamageValue();

        // Calculate how much durability is currently left
        int currentRemaining = maxDamage - currentDamage;

        // If remaining durability is already 10 or less, do nothing
        if (currentRemaining <= 10) {
            return;
        }

        int targetDamage = maxDamage - durabilityLeft;

        // Ensure damage doesn't go below 0 (if max durability is less than durabilityLeft)
        targetDamage = Math.max(0, targetDamage);
        stack.setDamageValue(targetDamage);
    }

    public static int getInt(String next){
        try {
            return Integer.parseInt(next);
        }catch (Exception ignore){
            return 0;
        }
    }

    public static int getInt(String next, int back){
        try {
            return Integer.parseInt(next);
        }catch (Exception ignore){
            return back;
        }
    }

    public static String getNext(List<String> words, int i, int next){
        return i+next < words.size() ? words.get(i+next) : "";
    }

    /**
     * Swaps two blocks in the world, including their BlockEntity data (inventories, etc.).
     * MUST be called on the Logical Server.
     */
    public static void swapBlocks(Level level, BlockPos pos1, BlockPos pos2) {
        // 1. Safety Checks
        if (level.isClientSide){
            Alteria.spawnBlockOutlineParticles(level, pos1, ParticleTypes.HAPPY_VILLAGER, 5);
            Alteria.spawnBlockOutlineParticles(level, pos2, ParticleTypes.HAPPY_VILLAGER, 5);
            return;
        }
        if (pos1.equals(pos2)) return;  // Cannot swap same position

        // 2. Capture Current States
        BlockState state1 = level.getBlockState(pos1);
        BlockState state2 = level.getBlockState(pos2);

        // Optional: Prevent swapping Bedrock or unbreakable blocks
        if (state1.getBlock() == Blocks.BEDROCK || state2.getBlock() == Blocks.BEDROCK) {
            return;
        }

        // 3. Capture BlockEntity Data (NBT)
        // We save the data to NBT so we can restore it after swapping the blocks
        CompoundTag nbt1 = getBlockEntityNBT(level, pos1);
        CompoundTag nbt2 = getBlockEntityNBT(level, pos2);

        // 4. Remove existing BlockEntities to prevent conflicts/crashes during swap
        // setBlock usually handles this, but explicit removal is safer for swaps
        level.removeBlockEntity(pos1);
        level.removeBlockEntity(pos2);

        // 5. Swap the BlockStates
        // Block.UPDATE_ALL (3) notifies neighbors and updates rendering
        level.setBlock(pos1, state2, Block.UPDATE_ALL);
        level.setBlock(pos2, state1, Block.UPDATE_ALL);

        // 6. Restore BlockEntity Data to the new locations
        // If Pos1 now has a block that supports BEs, load nbt2 (because state2 moved here)
        restoreBlockEntityNBT(level, pos1, nbt2);

        // If Pos2 now has a block that supports BEs, load nbt1 (because state1 moved here)
        restoreBlockEntityNBT(level, pos2, nbt1);
    }

    @Nullable
    private static CompoundTag getBlockEntityNBT(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            // saveWithFullMetadata includes the block entity ID
            return be.saveWithFullMetadata();
        }
        return null;
    }

    private static void restoreBlockEntityNBT(Level level, BlockPos pos, @Nullable CompoundTag nbt) {
        if (nbt != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                // Load the old data into the new block entity
                be.load(nbt);
                // Mark dirty so data saves to disk
                be.setChanged();
            }
        }
    }

    /* Example of particles for Flame Ray
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).collide(true).spawn(world());
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).collide(true).spawn(world());
    }*/
}
