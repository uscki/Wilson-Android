package me.blackwolf12333.appcki.api;

import me.blackwolf12333.appcki.generated.meeting.Meeting;
import me.blackwolf12333.appcki.generated.meeting.MeetingOverview;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface MeetingService {
    @GET("meetings/")
    Call<MeetingOverview> overview();

    @GET("meetings/{id}")
    Call<Meeting> get(@Path("id") Integer id);

    @GET("meetings/slots/{id}?canAttend=true")
    Call<Void> setSlot(@Path("id") Integer id, @Query("notes") String notes);
}
