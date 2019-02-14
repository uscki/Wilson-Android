package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.generated.news.NewsOverview;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface NewsService {
    @GET("news/")
    Call<NewsOverview> getNewsCollection(@Query("page") Integer page, @Query("size") Integer size);

    @GET("news/{id}")
    Call<NewsItem> getNewsResource(@Path("id") Integer id);

    @GET("news/types/")
    Call<Object> getNewsTypesCollection();
}
