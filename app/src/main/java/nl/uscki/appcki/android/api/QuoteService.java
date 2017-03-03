package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.generated.quotes.QuotesPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by peter on 3/1/17.
 */

public interface QuoteService {
    @GET("quote/get")
    Call<Quote> get(@Query("id") Integer id);

    @GET("quote/get")
    Call<Quote> get(@Query("id") Integer id, @Query("html") boolean html);

    @GET("quote/newer")
    Call<QuotesPage> newer(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer newer);

    @GET("quote/older")
    Call<QuotesPage> older(@Query("page") Integer page, @Query("size") Integer size);

    @GET("quote/older")
    Call<QuotesPage> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @POST("quote/vote")
    Call<Quote> vote(@Query("id") Integer id, @Query("positive") boolean positive);

    @POST("quote/vote")
    Call<Quote> vote(@Query("id") Integer id, @Query("positive") boolean positive, @Query("html") boolean html);
}
