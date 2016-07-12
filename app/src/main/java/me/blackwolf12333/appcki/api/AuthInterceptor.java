package me.blackwolf12333.appcki.api;

import java.io.IOException;

import me.blackwolf12333.appcki.helpers.UserHelper;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by peter on 7/12/16.
 */
public class AuthInterceptor implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("X-AUTH-TOKEN", UserHelper.getInstance().getUser().TOKEN).build();

        Response response = chain.proceed(authenticatedRequest);

        return response;
    }
}
