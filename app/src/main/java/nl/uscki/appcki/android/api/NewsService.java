package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.generated.news.NewsOverview;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface NewsService {
    @GET("news/overview")
    Call<NewsOverview> overview(@Query("page") Integer page, @Query("size") Integer size);

    @GET("news/get")
    Call<NewsItem> get(@Query("id") Integer id);

    @GET("news/newer")
    Call<NewsOverview> newer(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer newer);

    @GET("news/older")
    Call<NewsOverview> older(@Query("page") Integer page, @Query("size") Integer size);

    @GET("news/older")
    Call<NewsOverview> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older);

    @GET("news/older")
    Call<NewsOverview> older(@Query("page") Integer page, @Query("size") Integer size, @Query("id") Integer older, @Query("newsTypeId") Integer type);
}
