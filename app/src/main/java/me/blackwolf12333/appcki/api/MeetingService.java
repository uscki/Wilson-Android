package me.blackwolf12333.appcki.api;

import me.blackwolf12333.appcki.generated.meeting.MeetingItem;
import me.blackwolf12333.appcki.generated.meeting.MeetingOverview;
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
    Call<MeetingOverview> overview();

    @GET("meetings/{id}")
    Call<MeetingItem> get(@Path("id") Integer id);

    @POST("meetings/slots/{id}?canAttend=true")
    Call<Boolean> setSlot(@Path("id") Integer id, @Query("notes") String notes);
}
