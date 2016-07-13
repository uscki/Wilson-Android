package me.blackwolf12333.appcki.api;

import android.util.Log;

import java.io.IOException;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.helpers.UserHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Log.d("ServiceGenerator", original.toString());

                Request.Builder requestBuilder = original.newBuilder()
                        .header("X-AUTH-TOKEN", UserHelper.getInstance().getUser().TOKEN)
                        //.header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                Response response = chain.proceed(request);

                //Log.d("ServiceGenerator", respon);
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
