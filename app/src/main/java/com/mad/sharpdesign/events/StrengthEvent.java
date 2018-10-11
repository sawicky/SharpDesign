package com.mad.sharpdesign.events;

/**
 * EventBus event that will send an int strength value to our subscribed activities.
 */
public class StrengthEvent {
    public final int mStrength;

    public StrengthEvent(int mStrength) {
        this.mStrength = mStrength;
    }

    public int getmStrength() {
        return mStrength;
    }
}
