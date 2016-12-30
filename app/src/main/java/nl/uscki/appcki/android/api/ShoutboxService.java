package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.roephoek.Roephoek;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface ShoutboxService {
    @GET("shoutbox/newer")
    Call<Roephoek> newer(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer newer);

    @GET("shoutbox/older")
    Call<Roephoek> older(@Query("page") Integer page, @Query("size") Integer size);

    @GET("shoutbox/older")
    Call<Roephoek> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @GET("shoutbox/shout")
    Call<Boolean> shout(@Query("nickname") String name, @Query("message") String message);
}
