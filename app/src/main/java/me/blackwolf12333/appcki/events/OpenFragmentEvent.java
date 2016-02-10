package me.blackwolf12333.appcki.events;

import android.os.Bundle;

import me.blackwolf12333.appcki.MainActivity;

/**
 * Created by peter on 2/1/16.
 */
public class OpenFragmentEvent {
    public MainActivity.Screen screen;
    public Bundle arguments;

    public OpenFragmentEvent(MainActivity.Screen screen) {
        this.screen = screen;
        this.arguments = null;
    }

    public OpenFragmentEvent(MainActivity.Screen screen, Bundle arguments) {
        this.screen = screen;
        this.arguments = arguments;
    }
}
