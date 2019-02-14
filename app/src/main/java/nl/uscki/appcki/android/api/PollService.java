package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.poll.PollItem;
import nl.uscki.appcki.android.generated.poll.PollPage;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 3/7/17.
 */

public interface PollService {
    @GET("polls/active")
    Call<PollItem> active();

    @GET("polls/{id}")
    Call<PollItem> get(@Path("id") Integer id);

    @GET("polls/")
    Call<PollPage> overview(@Query("page") Integer page, @Query("size") Integer size);

    @FormUrlEncoded
    @POST("poll/active/options/{id}/vote")
    Call<PollItem> vote(@Path("id") Integer id);
}
