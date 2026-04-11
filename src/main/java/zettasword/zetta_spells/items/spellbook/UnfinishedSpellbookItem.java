package zettasword.zetta_spells.items.spellbook;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import zettasword.zetta_spells.network.PacketHandler;

public class UnfinishedSpellbookItem extends Item {
    public UnfinishedSpellbookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // Send packet to open screen ONLY on client
            PacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player),
                new OpenWriterScreenPacket()
            );
        }
        
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}