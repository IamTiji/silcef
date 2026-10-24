package cc.tijiisreal.silcef.internals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class DownloadPrompt extends Screen {
    private static final int MARGIN = 10;
    private final Runnable afterConsent;

    public DownloadPrompt(Runnable afterConsent) {
        super(Component.translatable("silcef.native_download.prompt.title"));
        this.afterConsent = afterConsent;
    }

    @Override
    protected void init() {
        super.init();

        StringWidget title = new StringWidget(Component.translatable("silcef.native_download.prompt.title"), font);
        title.setY(MARGIN);
        title.setX(MARGIN);
        addRenderableWidget(title);

        MultiLineTextWidget paragraph = new MultiLineTextWidget(
            MARGIN,
            MARGIN,
            Component.translatable("silcef.native_download.prompt.desc", JcefLoader.NATIVE_PATH),
            font
        );
        paragraph.setMaxWidth(width - MARGIN * 2);
        paragraph.setY(MARGIN*2 + font.lineHeight);
        paragraph.setX(MARGIN);

        addRenderableWidget(paragraph);

        addRenderableWidget(
            Button.builder(
                Component.translatable("silcef.native_download.prompt.allow"),
                (_) -> {
                    allowDownload();
                    Minecraft.getInstance().stop();
                }
            )
                .pos(MARGIN, height - MARGIN - Button.DEFAULT_HEIGHT)
                .width(width - MARGIN*2)
                .build()
        );

        addRenderableWidget(
            Button.builder(
                Component.translatable("silcef.native_download.prompt.later"),
                (_) -> {
                    allowDownload();
                    afterConsent.run();
                }
            )
                .pos(MARGIN, height - MARGIN - Button.DEFAULT_HEIGHT*2)
                .width(width - MARGIN*2)
                .build()
        );

        addRenderableWidget(
            Button.builder(
                Component.translatable("silcef.native_download.prompt.disallow"),
                (_) -> afterConsent.run()
            )
                .pos(MARGIN, height - MARGIN - Button.DEFAULT_HEIGHT*3)
                .width(width - MARGIN*2)
                .build()
        );
    }

    private void allowDownload() {
        try {
            //noinspection ResultOfMethodCallIgnored
            JcefLoader.DOWNLOAD_CHECK.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
