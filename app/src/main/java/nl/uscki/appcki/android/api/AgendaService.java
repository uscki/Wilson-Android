package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.agenda.Agenda;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface AgendaService {
    @GET("agenda/get")
    Call<AgendaItem> get(@Query("id") Integer id);

    @GET("agenda/newer")
    Call<Agenda> newer(@Query("page") Integer page, @Query("size") Integer size);

    @GET("agenda/older")
    Call<Agenda> older(@Query("page") Integer page, @Query("size") Integer size);

    @GET("agenda/older")
    Call<Agenda> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @POST("agenda/subscribe")
    Call<AgendaParticipantLists> subscribe(@Query("id") Integer id, @Query("note") String note);

    @POST("agenda/unsubscribe")
    Call<AgendaParticipantLists> unsubscribe(@Query("id") Integer id);

    @GET("agenda/subscribed")
    Call<Agenda> subscribed();
}
