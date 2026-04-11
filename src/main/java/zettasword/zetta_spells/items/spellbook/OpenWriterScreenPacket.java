package zettasword.zetta_spells.items.spellbook;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenWriterScreenPacket {
    public OpenWriterScreenPacket() {}

    public static OpenWriterScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenWriterScreenPacket();
    }

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> 
            Minecraft.getInstance().setScreen(new SpellbookWriterScreen())
        );
        ctx.get().setPacketHandled(true);
    }
}