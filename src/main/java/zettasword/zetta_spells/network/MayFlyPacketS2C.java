package zettasword.zetta_spells.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MayFlyPacketS2C {

    private final boolean mayFly;

    public MayFlyPacketS2C(boolean mayFly) {
        this.mayFly = mayFly;
    }

    public static MayFlyPacketS2C decode(FriendlyByteBuf buf) {
        return new MayFlyPacketS2C(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(mayFly);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        ClientPacketHandlers.applyMayFly(mayFly)
                )
        );
        ctx.get().setPacketHandled(true);
    }
}