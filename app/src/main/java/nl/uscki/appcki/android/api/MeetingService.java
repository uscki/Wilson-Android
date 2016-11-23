package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.MeetingOverview;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface MeetingService {
    @GET("meetings/")
    Call<MeetingOverview> overview(@Query("page") Integer page, @Query("size") Integer size);

    @GET("meetings/{id}")
    Call<MeetingItem> get(@Path("id") Integer id);

    @POST("meetings/slots/{id}")
    Call<Boolean> setSlot(@Path("id") Integer id, @Query("notes") String notes, @Query("canAttend") Boolean canAttend);
}
