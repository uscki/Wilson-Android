package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.roephoek.Roephoek;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface ShoutboxService {
    @GET("shouts/")
    Call<Roephoek> getShoutsCollection(@Query("page") Integer page, @Query("size") Integer size);

    @GET("shouts/")
    Call<Roephoek> getShoutsCollection(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @FormUrlEncoded
    @POST("shoutbox/shout")
    Call<RoephoekItem> shout(@Field("nickname") String name, @Field("message") String message);
}
