package zettasword.zetta_spells.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import zettasword.zetta_spells.capability.RaceDataHolder;

import java.util.function.Supplier;

public class RaceCapabilitySyncPacketS2C {

    private final CompoundTag data;

    public RaceCapabilitySyncPacketS2C(CompoundTag data) {
        this.data = data;
    }

    public RaceCapabilitySyncPacketS2C(FriendlyByteBuf buf) {
        this.data = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null) return;
            player.getCapability(RaceDataHolder.INSTANCE).ifPresent(d -> d.deserializeNBT(this.getData()));
        });
        ctx.get().setPacketHandled(true);
    }

    public CompoundTag getData() {
        return data;
    }
}
