package me.blackwolf12333.appcki.api;

import me.blackwolf12333.appcki.generated.agenda.Agenda;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.generated.agenda.Subscribers;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface AgendaService {
    @GET("agenda/get")
    Call<AgendaItem> get(@Query("id") Integer id);

    @GET("agenda/newer")
    Call<Agenda> newer();

    @GET("agenda/older")
    Call<Agenda> older(@Query("id") Integer older);

    @GET("agenda/subscribe")
    Call<Subscribers> subscribe(@Query("id") Integer id, @Query("note") String note);

    @GET("agenda/unsubscribe")
    Call<Subscribers> unsubscribe(@Query("id") Integer id);

    @GET("agenda/subscribed")
    Call<Agenda> subscribed();
}
