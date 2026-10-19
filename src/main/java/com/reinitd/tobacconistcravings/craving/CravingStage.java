package com.reinitd.tobacconistcravings.craving;

import com.reinitd.tobacconistcravings.attachment.ModAttachments;

public enum CravingStage {
    // Smoked within the last half day or never smoked
    NONE(0),

    // Half a day without smoking: 12,000 ticks, 10 IRL minutes
    MILD(12000),

    // TESTING
//    MILD(200), // 10s

    // Three quarters of a day: 18,000 ticks, 15 IRL minutes
    MODERATE(18000),

    // TESTING
//    MODERATE(400), // 20s

    // A full day: 24,000 ticks, 20 IRL minutes
    SEVERE(24000);

    // TESTING
//    SEVERE(600); // 30s

    private final int startTicks;

    CravingStage(int startTicks) {
        this.startTicks = startTicks;
    }

    public int startTicks() {
        return startTicks;
    }

    public static CravingStage fromTicks(int ticksSinceSmoke) {
        if (ticksSinceSmoke == ModAttachments.NEVER_SMOKED) { return NONE; }

        if (ticksSinceSmoke >= SEVERE.startTicks()) { return SEVERE; }
        else if (ticksSinceSmoke >= MODERATE.startTicks()) { return MODERATE; }
        else if (ticksSinceSmoke >= MILD.startTicks()) { return MILD; }

        return NONE;
    }
}
