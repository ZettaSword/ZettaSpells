package zettasword.zetta_spells.items.spellbook;

import com.binaris.wizardry.api.content.util.RegistryUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.items.ZSItems;
import zettasword.zetta_spells.spells.ZSSpells;

import java.util.function.Supplier;

public class SubmitWriterTextPacket {

    // ✅ Constants for validation (prevent magic numbers)
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_TEXT_LENGTH = 1000;

    private final String name;
    private final String text;

    public SubmitWriterTextPacket(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public static SubmitWriterTextPacket decode(FriendlyByteBuf buf) {
        // ✅ Use bounded readUtf to prevent malicious long strings
        return new SubmitWriterTextPacket(
                buf.readUtf(MAX_TEXT_LENGTH),
                buf.readUtf(MAX_NAME_LENGTH)
        );
    }

    public void encode(FriendlyByteBuf buf) {
        // ✅ Explicit length limits for safety
        buf.writeUtf(text, MAX_TEXT_LENGTH);
        buf.writeUtf(name, MAX_NAME_LENGTH);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                ZettaSpells.LOGGER.warn("Received SubmitWriterTextPacket from null player");
                return;
            }

            // ✅ Server-side validation (never trust client input)
            if (text.length() > MAX_TEXT_LENGTH || name.length() > MAX_NAME_LENGTH) {
                ZettaSpells.LOGGER.warn("Player {} sent oversized text/name", player.getName().getString());
                return;
            }

            if (name.isBlank() || text.isBlank()) {
                ZettaSpells.LOGGER.debug("Player {} sent empty writer content", player.getName().getString());
                return;
            }

            boolean converted = false;

            // ✅ Check both hands for writer item
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.is(ZSItems.UNFINISHED_SPELLBOOK.get())) {
                    // ✅ Create new written item with sanitized NBT data
                    ItemStack writtenStack = new ItemStack(ZSItems.FINISHED_SPELLBOOK.get());
                    var tag = writtenStack.getOrCreateTag();
                    tag.putString("written_text", text.trim());      // ✅ Trim whitespace
                    tag.putString("author", player.getName().getString()); // ✅ Store author
                    tag.putLong("written_time", player.level().getGameTime()); // ✅ Timestamp
                    writtenStack.setHoverName(Component.literal(name.trim()));
                    RegistryUtils.setSpell(writtenStack, ZSSpells.CUSTOM_PLAYER_SPELL.get());

                    // ✅ Replace item in hand
                    player.setItemInHand(hand, writtenStack);

                    // ✅ Optional: Send confirmation to client
                    // PacketHandler.INSTANCE.send(
                    //     new WriterConversionSuccessPacket(),
                    //     player.connection.connection
                    // );

                    //ZettaSpellsMod.LOGGER.debug("Player {} converted writer item: '{}'",
                    //        player.getName().getString(), name.trim());

                    converted = true;
                    break;
                }
            }

            if (!converted) {
                ZettaSpells.LOGGER.warn("Player {} tried to submit writer text but had no writer item",
                        player.getName().getString());
                // ✅ Optional: Send error feedback to player
                // player.sendSystemMessage(Component.translatable("error.zetta_spells.no_writer")
                //     .withStyle(ChatFormatting.RED));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}