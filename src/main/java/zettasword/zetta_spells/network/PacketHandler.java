package zettasword.zetta_spells.network;
// PacketHandler.java

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.items.spellbook.OpenWriterScreenPacket;
import zettasword.zetta_spells.items.spellbook.SubmitWriterTextPacket;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel INSTANCE; // ← NOT initialized here!

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        int id = 0;
        INSTANCE.messageBuilder(OpenWriterScreenPacket.class, id++, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenWriterScreenPacket::decode)
                .encoder(OpenWriterScreenPacket::encode)
                .consumerMainThread(OpenWriterScreenPacket::handle)
                .add();

        INSTANCE.messageBuilder(SubmitWriterTextPacket.class, id++, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                .decoder(SubmitWriterTextPacket::decode)
                .encoder(SubmitWriterTextPacket::encode)
                .consumerMainThread(SubmitWriterTextPacket::handle)
                .add();

        // In PacketHandler.register():
        INSTANCE.messageBuilder(RaceCapabilitySyncPacketS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RaceCapabilitySyncPacketS2C::encode)
                .decoder(RaceCapabilitySyncPacketS2C::decode)  // ✅ Must be static method reference
                .consumerMainThread(RaceCapabilitySyncPacketS2C::handle)
                .add();
    }
}