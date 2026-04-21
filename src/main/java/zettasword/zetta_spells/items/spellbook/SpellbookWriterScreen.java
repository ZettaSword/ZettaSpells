package zettasword.zetta_spells.items.spellbook;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zettasword.zetta_spells.network.PacketHandler;

@OnlyIn(Dist.CLIENT)
public class SpellbookWriterScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen.zetta_spells.writer");
    private static final Component SPELL_NAME = Component.translatable("gui.zetta_spells.textbox1");
    private static final Component SPELL = Component.translatable("gui.zetta_spells.textbox2");
    private static ServerPlayer author = null;
    //private static final ResourceLocation BACKGROUND_TEXTURE =
    //    ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
    
    private final Minecraft minecraft;
    private EditBox textBox;
    private EditBox textName;
    private Button confirmButton;
    private int backgroundWidth = 176;
    private int backgroundHeight = 166;

    public SpellbookWriterScreen() {
        super(TITLE);
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        
        // Center elements on screen
        int guiLeft = (width - backgroundWidth) / 2;
        int guiTop = (height - backgroundHeight) / 2;



        // Text input box for Name
        textName = new EditBox(
                minecraft.font,
                guiLeft + 15, guiTop,
                backgroundWidth - 50, 20,
                Component.translatable("gui.zetta_spells.textbox_name")
        );
        textName.setMaxLength(100);
        addRenderableWidget(textName);

        // Text input box
        textBox = new EditBox(
            minecraft.font, 
            guiLeft - 80, guiTop + 35,
            backgroundWidth + 200, 20,
            Component.translatable("gui.zetta_spells.textbox")
        );
        textBox.setMaxLength(1000);
        textBox.setValue("");
        addRenderableWidget(textBox);

        // Confirm button
        confirmButton = Button.builder(
            Component.translatable("gui.zetta_spells.confirm"),
            button -> {
                if (!textBox.getValue().isEmpty()) {
                    // Send text to server and close screen
                    PacketHandler.INSTANCE.sendToServer(
                        new SubmitWriterTextPacket(textBox.getValue(), textName.getValue())
                    );
                    onClose();
                }
            }
        ).pos(guiLeft + (backgroundWidth - 60) / 2, guiTop + 65)
         .size(60, 20)
         .build();
        addRenderableWidget(confirmButton);
        
        // Cancel on ESC key
        //setInitialFocus(textBox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        
        // Draw background texture
        int guiLeft = (width - backgroundWidth) / 2;
        int guiTop = (height - backgroundHeight) / 2;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //guiGraphics.blit(BACKGROUND_TEXTURE, guiLeft, guiTop, 0, 0, backgroundWidth, backgroundHeight);
        
        // Draw title
        /*guiGraphics.drawCenteredString(
            minecraft.font,
            TITLE, 
            width / 2, 
            guiTop + 15, 
            0xd1d1d1
        );*/
        guiGraphics.drawCenteredString(
                minecraft.font,
                SPELL_NAME,
                guiLeft, guiTop + 5,
                0xd1d1d1
        );

        guiGraphics.drawCenteredString(
                minecraft.font,
                SPELL,
                guiLeft - 100, guiTop + 45,
                0xd1d1d1
        );
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Submit on Enter key
        if (keyCode == 257 || keyCode == 335) { // ENTER keys
            if (modifiers == 0 && !textBox.getValue().isEmpty()) {
                confirmButton.onPress();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause game while screen is open
    }
}