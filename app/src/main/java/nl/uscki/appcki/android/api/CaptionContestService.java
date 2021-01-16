package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.captioncontest.Caption;
import nl.uscki.appcki.android.generated.captioncontest.CaptionContest;
import nl.uscki.appcki.android.generated.common.Pageable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface CaptionContestService {

    @GET("media/captioncontest/")
    Call<Pageable<CaptionContest>> getCaptionContests(@Query("page") Integer page, @Query("size") Integer size, @Query("sort") String... sort);

    @GET("media/captioncontest/current")
    Call<CaptionContest> getCurrentCaptionContest();

    @GET("media/captioncontest/{captionContestId}")
    Call<CaptionContest> getCaptionContest(@Path("captionContestId") int captionContestId);

    @POST("media/captioncontest/current/captions/add")
    Call<ActionResponse<Caption>> addCaption(@Query("caption") String caption, @Query("hideName") boolean hideName);

    @POST("media/captioncontest/current/captions/{captionId}/vote")
    Call<ActionResponse<CaptionContest>> vote(@Path("captionId") int captionId);
}
