package com.mad.sharpdesign.events;

/**
 * EventBus event that will post a boolean message to our subscribed activities.
 */
public class ApplyEvent {
    public final boolean applied;

    public ApplyEvent(boolean applied) {
        this.applied = applied;
    }

    public boolean isApplied() {
        return applied;
    }
}
