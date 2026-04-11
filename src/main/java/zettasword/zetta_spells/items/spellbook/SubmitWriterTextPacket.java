package zettasword.zetta_spells.items.spellbook;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import zettasword.zetta_spells.items.ZSItems;

import java.util.function.Supplier;

public class SubmitWriterTextPacket {
    private final String name;
    private final String text;

    public SubmitWriterTextPacket(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public static SubmitWriterTextPacket decode(FriendlyByteBuf buf) {
        return new SubmitWriterTextPacket(buf.readUtf(1000), buf.readUtf(100)); // Max 100 chars
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(text, 1000);
        buf.writeUtf(name, 100);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || text.length() > 1000) return;

            // Check both hands for writer item
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.is(ZSItems.WRITER_ITEM.get())) {
                    // Create new written item with NBT data
                    ItemStack writtenStack = new ItemStack(ZSItems.WRITTEN_ITEM.get());
                    writtenStack.getOrCreateTag().putString("written_text", text);
                    writtenStack.setHoverName(Component.literal(name));
                    
                    // Replace item in hand
                    player.setItemInHand(hand, writtenStack);
                    break;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}