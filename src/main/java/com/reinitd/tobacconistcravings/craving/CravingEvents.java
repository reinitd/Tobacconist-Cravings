package com.reinitd.tobacconistcravings.craving;

import com.reinitd.tobacconistcravings.TobacconistCravings;
import com.reinitd.tobacconistcravings.attachment.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Locale;

// https://docs.neoforged.net/docs/1.21.1/concepts/events#eventbussubscriber
@EventBusSubscriber(modid = TobacconistCravings.MODID)
public class CravingEvents {
    private static final ResourceLocation NICOTINE_ID = ResourceLocation.fromNamespaceAndPath("tobacconistmod", "nicotine");

    // 20 ticks = 1 sec
    private static final int CHECK_INTERVAL = 20;

    private static final int EFFECT_DURATION = 60;

    private static final float NAUSEA_PULSE_CHANCE = 1.0F / 45; // about every 45 seconds
    private static final int NAUSEA_PULSE_DURATION = 160;       // 8 seconds
    private static final float BLINDNESS_PULSE_CHANCE = 1.0F / 60; // about once a minute
    private static final int BLINDNESS_PULSE_DURATION = 40;        // 2 seconds

    // https://docs.neoforged.net/docs/1.21.1/datastorage/attachments#using-the-attachment-type
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (isNicotine(event.getEffectInstance())) {
                serverPlayer.setData(ModAttachments.TICKS_SINCE_SMOKE, 0);
            }
        }
    }

    // After the player's tick logic ran.
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.isCreative() || serverPlayer.isSpectator()) { return; }

            int tss = serverPlayer.getData(ModAttachments.TICKS_SINCE_SMOKE);
            if (tss == ModAttachments.NEVER_SMOKED) { return; }

            if (tss < CravingStage.SEVERE.startTicks()) {
                tss++;
                serverPlayer.setData(ModAttachments.TICKS_SINCE_SMOKE, tss);

                CravingStage stage = CravingStage.fromTicks(tss);
                if (stage != CravingStage.NONE && tss == stage.startTicks()) {
                    String path = String.format("message.%s.craving.%s", TobacconistCravings.MODID, stage.name().toLowerCase(Locale.ROOT));
                    ChatFormatting color = ChatFormatting.GRAY;

                    switch (stage) {
                        case CravingStage.MODERATE -> color = ChatFormatting.GOLD;
                        case CravingStage.SEVERE -> color = ChatFormatting.RED;
                    }

                    Component chat = Component.translatable(path).withStyle(color);
                    serverPlayer.displayClientMessage(chat, false);
                }
            }

            if (serverPlayer.tickCount % CHECK_INTERVAL == 0) {
                CravingStage stage = CravingStage.fromTicks(tss);
                applyDebuffs(serverPlayer, stage);
            }

        }
    }

    private static boolean isNicotine(MobEffectInstance instance) {
        return instance.getEffect().is(NICOTINE_ID);
    }

    //  MILD:     hunger I, bad luck
    //  MODERATE: + mining fatigue I, weakness I, nausea pulses
    //  SEVERE:   + darkness, slowness I, blindness pulses
    private static void applyDebuffs(ServerPlayer player, CravingStage stage) {
        if (stage == CravingStage.NONE) { return; }

        // Mild and up
        refreshEffect(player, MobEffects.HUNGER);
        refreshEffect(player, MobEffects.UNLUCK);

        if (stage.compareTo(CravingStage.MODERATE) >= 0) {
            refreshEffect(player, MobEffects.DIG_SLOWDOWN);
            refreshEffect(player, MobEffects.WEAKNESS);
            pulseEffect(player, MobEffects.CONFUSION, NAUSEA_PULSE_CHANCE, NAUSEA_PULSE_DURATION);
        }

        if (stage == CravingStage.SEVERE) {
            refreshEffect(player, MobEffects.DARKNESS);
            refreshEffect(player, MobEffects.MOVEMENT_SLOWDOWN);
            pulseEffect(player, MobEffects.BLINDNESS, BLINDNESS_PULSE_CHANCE, BLINDNESS_PULSE_DURATION);
        }

    }

    // https://docs.neoforged.net/docs/1.21.1/items/mobeffects#mobeffectinstances
    private static void refreshEffect(ServerPlayer player, Holder<MobEffect> effect) {
        MobEffectInstance effectInstance = new MobEffectInstance(
                effect,
                EFFECT_DURATION,
                0,
                true,
                false,
                true
        );
        player.addEffect(effectInstance);
    }

    private static void pulseEffect(ServerPlayer player, Holder<MobEffect> effect, float chance, int duration) {
        if (player.hasEffect(effect)) { return; }

        float f = player.getRandom().nextFloat();

        if (f < chance) {
            MobEffectInstance effectInstance = new MobEffectInstance(
                    effect,
                    duration,
                    0,
                    true,
                    false,
                    true
            );

            player.addEffect(effectInstance);
        }
    }
}
