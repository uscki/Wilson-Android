package nl.uscki.appcki.android.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 4/5/17.
 */

public interface PermissionsService {
    @GET("permissions/")
    Call<Boolean> hasPermission(@Query("node") String node, @Query("level") String level);
}
