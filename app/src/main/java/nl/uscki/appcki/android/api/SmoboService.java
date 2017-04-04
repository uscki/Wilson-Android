package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
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

    @GET("smobo/get/{id}/photos")
    Call<Pageable<Integer>> photos(@Path("id") Integer id, @Query("page") Integer page, @Query("size") Integer size);

    @GET("smobo/overview")
    Call<SmoboSearchResult> overview (@Query("query") String query, @Query("page") Integer page, @Query("size") Integer size, @Query("sort") String sort);
}
