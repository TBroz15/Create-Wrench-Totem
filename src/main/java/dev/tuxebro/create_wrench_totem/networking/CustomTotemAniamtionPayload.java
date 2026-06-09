package dev.tuxebro.create_wrench_totem.networking;

import dev.tuxebro.create_wrench_totem.CreateWrenchTotem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record CustomTotemAniamtionPayload(ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CustomTotemAniamtionPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CreateWrenchTotem.MOD_ID, "custom_totem_aniamtion"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomTotemAniamtionPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            CustomTotemAniamtionPayload::stack,
            CustomTotemAniamtionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.NETWORK);

        registrar.playBidirectional(
                CustomTotemAniamtionPayload.TYPE,
                CustomTotemAniamtionPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        PayloadHandler::handleClient,
                        PayloadHandler::handleServer
                )
        );
    }
}
