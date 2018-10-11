package com.mad.sharpdesign.events;

public class ApplyEvent {
    public final boolean applied;

    public ApplyEvent(boolean applied) {
        this.applied = applied;
    }

    public boolean isApplied() {
        return applied;
    }
}
