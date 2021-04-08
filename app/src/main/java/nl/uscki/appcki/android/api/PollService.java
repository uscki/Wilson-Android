package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.poll.PollItem;
import retrofit2.Call;
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
    Call<Pageable<PollItem>> overview(@Query("page") Integer page, @Query("size") Integer size);

    @POST("polls/active/options/{id}/vote")
    Call<ActionResponse<PollItem>> vote(@Path("id") Integer id);
}
