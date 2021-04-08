package nl.uscki.appcki.android.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by peter on 4/5/17.
 */

public interface PermissionsService {
    @GET("permissions/{node}/{level}")
    Call<Boolean> hasPermission(@Path("node") String node, @Path("level") String level);
}
