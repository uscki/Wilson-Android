package me.blackwolf12333.appcki.api;

import me.blackwolf12333.appcki.generated.news.NewsItem;
import me.blackwolf12333.appcki.generated.news.NewsOverview;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface NewsService {
    @GET("news/overview?sort=posteddate,desc")
    Call<NewsOverview> overview();

    @GET("news/get")
    Call<NewsItem> get(@Query("id") Integer id);

    @GET("news/newer")
    Call<NewsOverview> newer(@Query("id") Integer newer);

    @GET("news/older")
    Call<NewsOverview> older(@Query("id") Integer older);
}
