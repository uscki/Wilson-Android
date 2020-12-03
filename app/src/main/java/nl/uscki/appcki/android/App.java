package nl.uscki.appcki.android;

import android.app.Application;
import android.content.Context;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import nl.uscki.appcki.android.api.ServiceGenerator;

/**
 * Created by peter on 12/9/15.
 */
public class App extends Application {

    //TODO: LINT: Do not place Android context classes in static fields; this is a memory leak (and also breaks Instant Run)
    private static Context mContext;

    public static final String USCKI_CKI_CHARACTER = "\u01de";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Regular-with-CKI.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        // TODO not sure if this now can be deleted, or if a similar way is available for Glide
        ServiceGenerator.init(this); // initialise our OkHttp3 client for Fresco
    }

    public static Context getContext() {
        return mContext;
    }
}