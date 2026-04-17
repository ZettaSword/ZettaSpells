package zettasword.zetta_spells.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RaceCapabilitySyncPacketS2C {

    private final CompoundTag data;

    public RaceCapabilitySyncPacketS2C(CompoundTag data) {
        this.data = data;
    }

    // ✅ Decoder must be a static method that returns a new instance
    public static RaceCapabilitySyncPacketS2C decode(FriendlyByteBuf buf) {
        return new RaceCapabilitySyncPacketS2C(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                // ✅ Delegate ALL client logic to a separate client-only class
                // This class (RaceCapabilitySyncPacketS2C) now has ZERO client-only references
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        ClientPacketHandlers.applyRaceSync(data)
                )
        );
        ctx.get().setPacketHandled(true);
    }

    // ✅ Keep this if other classes need to read the data (optional)
    public CompoundTag getData() {
        return data;
    }
}