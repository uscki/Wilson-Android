package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.generated.agenda.SimpleAgendaItem;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.generated.common.Pageable;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface AgendaService {
    @GET("agenda/{event}")
    Call<AgendaItem> get(@Path("event") Integer id);

    @GET("agenda/")
    Call<Pageable<SimpleAgendaItem>> agenda(@Query("page") Integer page, @Query("size") Integer size);

    @GET("agenda/")
    Call<Pageable<SimpleAgendaItem>> agenda(@Query("page") Integer page, @Query("size") Integer size, @Query("sort") Object sort);

    @GET("agenda/archive/")
    Call<Pageable<SimpleAgendaItem>> archive(@Query("page") Integer page, @Query("size") Integer size);

    @GET("agenda/archive/")
    Call<Pageable<SimpleAgendaItem>> archive(@Query("page") Integer page, @Query("size") Integer size, @Query("sort") Object sort);

    @GET("agenda/categories/")
    Call<Object> categories();

    @FormUrlEncoded
    @POST("agenda/{event}/subscribe")
    Call<ActionResponse<AgendaParticipantLists>> subscribe(@Path("event") Integer id, @Field("note") String note);

    @FormUrlEncoded
    @POST("agenda/{event}/subscribe")
    Call<ActionResponse<AgendaParticipantLists>> subscribe(@Path("event") Integer id, @Field("note") String note, @Field("answer") String answer);

    @POST("agenda/{event}/unsubscribe")
    Call<ActionResponse<AgendaParticipantLists>> unsubscribe(@Path("event") Integer id);

    @GET("agenda/{event}/comments/")
    Call<Pageable<Comment>> getComments(@Path("event") Integer agendaId, @Query("page") Integer page, @Query("size") Integer size);

    @POST("agenda/{event}/comments/add")
    Call<ActionResponse<Comment>> replyToComment(@Path("event") Integer agendaId, @Query("parentId") Integer parentId, @Query("comment") String comment);

    @DELETE("agenda/{event}/comments/{comment}")
    Call<Boolean> deleteComment(@Path("event") Integer agendaId, @Path("comment") Integer commentId);
}
