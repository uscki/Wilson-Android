package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by peter on 12/30/16.
 */

public interface UserService {
    @GET("user/current")
    Call<PersonSimple> currentUser();

    @FormUrlEncoded
    @POST("notifications/android/register")
    Call<Boolean> registerDeviceId(@Field("token") String token);
}
