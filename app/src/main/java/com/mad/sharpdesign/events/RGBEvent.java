package com.mad.sharpdesign.events;

/**
 * EventBus event that will send a RGB int value to our subscribed activities.
 */
public class RGBEvent {
    public final int red,green,blue;

    public RGBEvent(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
