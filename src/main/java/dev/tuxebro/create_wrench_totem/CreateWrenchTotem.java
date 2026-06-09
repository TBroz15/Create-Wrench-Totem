package dev.tuxebro.create_wrench_totem;

import com.mojang.logging.LogUtils;
import dev.tuxebro.create_wrench_totem.networking.CustomTotemAnimationPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CreateWrenchTotem.MOD_ID)
public class CreateWrenchTotem {
    public static final String MOD_ID = "create_wrench_totem";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateWrenchTotem(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("hello i am initializing");

        modEventBus.addListener(CustomTotemAnimationPayload::register);
    }
}
