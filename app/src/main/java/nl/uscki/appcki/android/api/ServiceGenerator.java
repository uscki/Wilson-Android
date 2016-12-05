package nl.uscki.appcki.android.api;

import java.io.IOException;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.UserHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by peter on 7/12/16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = App.getContext().getString(R.string.apiurl);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);// TODO uncomment voor debug output

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("X-AUTH-TOKEN", UserHelper.getInstance().TOKEN)
                        .method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
