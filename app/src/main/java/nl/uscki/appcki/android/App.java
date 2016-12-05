package nl.uscki.appcki.android;

import android.app.Application;
import android.content.Context;

/**
 * Created by peter on 12/9/15.
 */
public class App extends Application {

    //TODO: LINT: Do not place Android context classes in static fields; this is a memory leak (and also breaks Instant Run)
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}