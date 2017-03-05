package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.generated.smobo.SmoboSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 3/4/17.
 */

public interface SmoboService {
    @GET("smobo/get/{id}")
    Call<SmoboItem> get(@Path("id") Integer id);

    @GET("smobo/search")
    Call<SmoboSearchResult> search(@Query("query") String query, @Query("page") Integer page, @Query("size") Integer size, @Query("sort") String sort);
}
