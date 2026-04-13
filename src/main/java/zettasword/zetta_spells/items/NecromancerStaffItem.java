package zettasword.zetta_spells.items;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.spells.ReplaceArmor;
import zettasword.zetta_spells.system.ArcaneColor;

import java.util.List;

public class NecromancerStaffItem extends Item {
    private static final String TAG_MODE = "StaffMode";
    private static final int MODE_COMMAND = 0;
    private static final int MODE_EQUIP = 1;

    public NecromancerStaffItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> tooltip, net.minecraft.world.item.@NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.zetta_spells.necromancer_staff.desc"));

        // Show current mode with color coding
        int mode = getStaffMode(stack);
        if (mode == MODE_COMMAND) {
            tooltip.add(Component.translatable("item.zetta_spells.necromancer_staff.mode.command")
                    .withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(Component.translatable("item.zetta_spells.necromancer_staff.mode.equip")
                    .withStyle(ChatFormatting.GOLD));
        }

        tooltip.add(Component.translatable("item.zetta_spells.necromancer_staff.mode.toggle")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Toggle mode when shift + right-clicking (air or entity)
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                toggleStaffMode(stack);
                int newMode = getStaffMode(stack);
                Component message = newMode == MODE_COMMAND
                        ? Component.translatable("item.zetta_spells.necromancer_staff.mode.command.active")
                        : Component.translatable("item.zetta_spells.necromancer_staff.mode.equip.active");
                player.displayClientMessage(message, true);
                player.playSound(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.7F, 1.2F);
            }
            return InteractionResultHolder.success(stack);
        }

        // Normal right-click: interact with minion based on current mode
        double range = 14;
        Vec3 look = player.getLookAngle();
        Vec3 origin = new Vec3(player.getX(), player.getY() + (double)player.getEyeHeight() - (double)0.25F, player.getZ());
        if (level.isClientSide && ClientUtils.isFirstPerson(player)) {
            origin = origin.add(look.scale(1.2));
        }
        Vec3 endpoint = origin.add(look.scale(range));
        HitResult rayTrace = RayTracer.rayTrace(level, player, origin, endpoint, 0.0F, false, Entity.class, RayTracer.ignoreEntityFilter(player));

        if (rayTrace instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity instanceof Mob mob && Services.OBJECT_DATA.isMinion(mob)) {
                MinionData data = Services.OBJECT_DATA.getMinionData(mob);
                if (data != null && data.getOwner() == player) {
                    int mode = getStaffMode(stack);

                    if (mode == MODE_COMMAND) {
                        // === COMMAND MODE: Toggle follow/sit ===
                        boolean isSitting = mob.getPersistentData().contains("sitting") && mob.getPersistentData().getBoolean("sitting");
                        if (!level.isClientSide) {
                            mob.getPersistentData().putBoolean("sitting", !isSitting);
                            Component msg = !isSitting
                                    ? Component.translatable("item.zetta_spells.necromancer_staff.use1")  // Now following
                                    : Component.translatable("item.zetta_spells.necromancer_staff.use2"); // Now sitting
                            player.displayClientMessage(msg, true);
                        }
                    } else {
                        // === EQUIP MODE: Equip item from player's other hand ===
                        InteractionHand otherHand = player.getMainHandItem().getItem() instanceof NecromancerStaffItem
                                ? InteractionHand.OFF_HAND
                                : InteractionHand.MAIN_HAND;
                        ItemStack itemToEquip = player.getItemInHand(otherHand).copy();

                        if (!mob.isAlive()) return InteractionResultHolder.pass(stack);
                        if (!itemToEquip.isEmpty()) {
                            applyItemToMinion(level, player, mob, itemToEquip, otherHand);
                        } else if (!level.isClientSide) {
                            player.displayClientMessage(Component.translatable("item.zetta_spells.necromancer_staff.equip.empty"), true);
                        }
                    }

                    // Visual/audio feedback for successful interaction
                    if (level.isClientSide) {
                        for (int j = 0; j < 20; ++j) {
                            double x = mob.xo + level.random.nextDouble() * 2.0D - 1.0D;
                            double y = mob.yo + mob.getEyeHeight() - 0.5D + level.random.nextDouble();
                            double z = mob.zo + level.random.nextDouble() * 2.0D - 1.0D;
                            ParticleBuilder.create(EBParticles.SPARKLE)
                                    .pos(x, y, z)
                                    .velocity(0.0D, 0.1D, 0.0D)
                                    .color(0xFF9800)
                                    .spawn(level);
                        }
                        ParticleBuilder.create(EBParticles.BUFF)
                                .entity(mob)
                                .color(ArcaneColor.ARCANE)
                                .spawn(level);
                        player.playSound(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.7F, 1.0F);
                    }
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    public static void applyItemToMinion(@NotNull Level level, @NotNull Player player, Mob mob, ItemStack itemToEquip, InteractionHand otherHand) {
        if (itemToEquip.getItem() == Items.NAME_TAG){
            mob.setCustomName(itemToEquip.getHoverName());
            mob.setPersistenceRequired();
            player.setItemInHand(otherHand, ItemStack.EMPTY);
            player.inventoryMenu.broadcastChanges();
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("item.zetta_spells.necromancer_staff.equip.change_name"), true);
            }
            return;
        }
        EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(itemToEquip);
        ItemStack currentEquipped = mob.getItemBySlot(slot);

        // Drop current item if present
        ReplaceArmor.dropCurrentSlotItem(level, mob, currentEquipped);
        mob.setItemSlot(slot, ItemStack.EMPTY);

        // Equip new item and consume from player
        mob.setItemSlot(slot, itemToEquip);
        player.setItemInHand(otherHand, ItemStack.EMPTY);
        player.inventoryMenu.broadcastChanges();

        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("item.zetta_spells.necromancer_staff.equip.success"), true);
        }
    }

    // ===== MODE MANAGEMENT =====
    private int getStaffMode(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.contains(TAG_MODE) ? nbt.getInt(TAG_MODE) : MODE_COMMAND;
    }

    private void setStaffMode(ItemStack stack, int mode) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(TAG_MODE, mode);
    }

    private void toggleStaffMode(ItemStack stack) {
        int current = getStaffMode(stack);
        setStaffMode(stack, current == MODE_COMMAND ? MODE_EQUIP : MODE_COMMAND);
    }
}