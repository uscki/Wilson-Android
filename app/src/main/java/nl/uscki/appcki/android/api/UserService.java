package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.fragments.LoginFragment;
import nl.uscki.appcki.android.generated.organisation.CurrentUser;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by peter on 12/30/16.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("login")
    Call<Void> login(@Field("username") String username, @Field("password") String password);

    @GET("users/current")
    Call<CurrentUser> currentUser();

    @GET("users/current")
    Call<CurrentUser> currentUser(@Header(LoginFragment.AUTH_HEADER) String token);

    @FormUrlEncoded
    @POST("notifications/android/register")
    Call<ActionResponse<Boolean>> registerDeviceId(@Field("token") String token);
}
