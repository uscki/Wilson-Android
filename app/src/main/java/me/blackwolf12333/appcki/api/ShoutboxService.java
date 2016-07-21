package me.blackwolf12333.appcki.api;

import me.blackwolf12333.appcki.generated.roephoek.Roephoek;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface ShoutboxService {
    @GET("shoutbox/newer")
    Call<Roephoek> newer(@Query("id") Integer newer);

    @GET("shoutbox/older")
    Call<Roephoek> older(@Query("id") Integer older);

    @GET("shoutbox/shout")
    Call<Roephoek> shout(@Query("nickname") String name, @Query("message") String message);
}
