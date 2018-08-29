package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.agenda.Agenda;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.generated.comments.CommentPage;
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
public interface AgendaService {
    @GET("agenda/{id}")
    Call<AgendaItem> get(@Path("id") Integer id);

    @GET("agenda/newer")
    Call<Agenda> newer(@Query("page") Integer page, @Query("size") Integer size);

    @GET("agenda/older")
    Call<Agenda> older(@Query("page") Integer page, @Query("size") Integer size);

    @GET("agenda/older")
    Call<Agenda> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @FormUrlEncoded
    @POST("agenda/subscribe")
    Call<AgendaParticipantLists> subscribe(@Field("id") Integer id, @Field("note") String note);

    @FormUrlEncoded
    @POST("agenda/unsubscribe")
    Call<AgendaParticipantLists> unsubscribe(@Field("id") Integer id);

    @GET("agenda/{id}/comments")
    Call<CommentPage> getComments(@Path("id") Integer agendaId);

    @POST("agenda/{id}/comments")
    Call<CommentPage> replyToComment(@Path("id") Integer agendaId, @Query("parentId") Integer parentId, @Query("comment") String comment);

    @GET("agenda/subscribed")
    Call<Agenda> subscribed();
}
