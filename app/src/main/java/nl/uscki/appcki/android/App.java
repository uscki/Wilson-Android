package nl.uscki.appcki.android;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

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

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        ServiceGenerator.init(); // initialise our OkHttp3 client for Fresco

        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, ServiceGenerator.client)
                .build();
        Fresco.initialize(this, config);
    }

    public static Context getContext() {
        return mContext;
    }
}