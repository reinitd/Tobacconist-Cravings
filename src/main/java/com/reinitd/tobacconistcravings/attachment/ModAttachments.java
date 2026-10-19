package com.reinitd.tobacconistcravings.attachment;

import com.mojang.serialization.Codec;
import com.reinitd.tobacconistcravings.TobacconistCravings;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

// https://docs.neoforged.net/docs/1.21.1/datastorage/attachments
public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TobacconistCravings.MODID);

    public static final int NEVER_SMOKED = -1;
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> TICKS_SINCE_SMOKE = ATTACHMENT_TYPES.register(
            "ticks_since_smoke", () -> AttachmentType.<Integer>builder(() -> NEVER_SMOKED)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build()
    );
}
