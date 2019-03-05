package nl.uscki.appcki.android.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.UserHelper;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.joda.time.format.ISODateTimeFormat.*;

class DateTimeTypeAdapter implements JsonSerializer<DateTime>,
        JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        return DateTime.parse(json.getAsString());
    }

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(dateTimeNoMillis().print(src));
    }
}

/**
 * Created by peter on 7/12/16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = App.getContext().getString(R.string.apiurl);

    public static final String AUTH_HEADER = "Authorization";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    public static OkHttpClient client;

    public static void init() {
        LoggingInterceptor logging = new LoggingInterceptor();
        // set your desired log level
        logging.setLevel(LoggingInterceptor.Level.BODY);
        logging.addFilter("www.uscki.nl").addFilter("api/media/");
        httpClient.addInterceptor(logging);// TODO uncomment voor debug output

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                if(UserHelper.getInstance().getToken() == null) {
                    return chain.proceed(original);
                }

                if (!original.url().host().contains("uscki.nl"))
                    return chain.proceed(original);

                Request.Builder requestBuilder = original.newBuilder()
                        .header(AUTH_HEADER, UserHelper.getInstance().getToken())
                        .method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            }
        });

        client = httpClient
                .cache(new Cache(new File(App.getContext().getCacheDir(), "http-cache"), 10 * 1024 * 1024))
                .build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static Gson getGson() {
        return gson;
    }
}
