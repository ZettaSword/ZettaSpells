package zettasword.zetta_spells.items.spellbook;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import zettasword.zetta_spells.network.ClientPacketHandlers;

import java.util.function.Supplier;

public class OpenWriterScreenPacket {

    public OpenWriterScreenPacket() {}

    public static OpenWriterScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenWriterScreenPacket();
    }

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                // ✅ Delegate to client-only handler - NO direct client class references here
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandlers::openWriterScreen)
        );
        ctx.get().setPacketHandled(true);
    }
}