package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.generated.quotes.QuotesPage;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 3/1/17.
 */

public interface QuoteService {
    @GET("quotes/{id}")
    Call<Quote> get(@Path("id") Integer id);

    @GET("quotes/")
    Call<QuotesPage> getQuotesCollection(@Query("page") Integer page, @Query("size") Integer size);

    @GET("quotes/")
    Call<QuotesPage> getQuotesCollection(@Query("page") Integer page, @Query("size") Integer size, @Query("sort") String... sort);

    @FormUrlEncoded
    @POST("quotes/{id}/vote")
    Call<ActionResponse<Quote>> vote(@Path("id") Integer id, @Field("positive") boolean positive);

    @FormUrlEncoded
    @POST("quotes/new")
    Call<ActionResponse<Quote>> newQuote(@Field("quote") String name);
}
