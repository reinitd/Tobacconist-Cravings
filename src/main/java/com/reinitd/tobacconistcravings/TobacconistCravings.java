package com.reinitd.tobacconistcravings;

import com.reinitd.tobacconistcravings.attachment.ModAttachments;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(TobacconistCravings.MODID)
public class TobacconistCravings {
    public static final String MODID = "tobacconistcravings";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TobacconistCravings(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
    }
}
