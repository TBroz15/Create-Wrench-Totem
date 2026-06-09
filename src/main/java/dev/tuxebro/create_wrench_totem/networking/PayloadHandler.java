package dev.tuxebro.create_wrench_totem.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PayloadHandler {
    public static void handleClient(final CustomTotemAniamtionPayload data, final IPayloadContext context) {
        context.enqueueWork(()->Minecraft.getInstance().gameRenderer.displayItemActivation(data.stack()))
        .exceptionally(e -> {
            context.disconnect(Component.literal("Error when popping a Wrench Totem: " + e.getMessage()));
            return null;
        });
    }

    public static void handleServer(final CustomTotemAniamtionPayload data, final IPayloadContext context) {
        // do absolutely nothing i guess
    }
}
