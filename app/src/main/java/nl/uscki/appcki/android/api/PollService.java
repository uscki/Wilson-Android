package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.poll.PollItem;
import nl.uscki.appcki.android.generated.poll.PollPage;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by peter on 3/7/17.
 */

public interface PollService {
    @GET("poll/active")
    Call<PollItem> active();

    @GET("poll/get")
    Call<PollItem> get(@Query("id") Integer id);

    @GET("poll/overview")
    Call<PollPage> overview(@Query("page") Integer page, @Query("size") Integer size);

    @GET("poll/newer")
    Call<PollPage> newer(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer newer);

    @GET("poll/older")
    Call<PollPage> older(@Query("page") Integer page, @Query("size") Integer size);

    @GET("poll/older")
    Call<PollPage> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @FormUrlEncoded
    @POST("poll/vote")
    Call<PollItem> vote(@Field("id") Integer id);
}
