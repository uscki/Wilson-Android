package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.Slot;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface MeetingService {
    @GET("meetings/")
    Call<Pageable<MeetingItem>> getMeetingCollection(@Query("page") Integer page, @Query("size") Integer size);

    @GET("meetings/")
    Call<Pageable<MeetingItem>> getMeetingCollection(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer id);

    @GET("meetings/{id}")
    Call<MeetingItem> get(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("meetings/{meeting}/slots/{id}")
    Call<Slot> setSlot(@Path("meeting") Integer meetingId, @Path("id") Integer id, @Field("notes") String notes, @Field("canAttend") Boolean canAttend);
}
