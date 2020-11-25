package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.generated.organisation.PersonName;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 3/4/17.
 */

public interface SmoboService {
    @GET("people/{id}")
    Call<SmoboItem> get(@Path("id") Integer id);

    @GET("people/")
    Call<Pageable<PersonName>> getPeopleCollection(@Query("page") Integer page, @Query("size") Integer size, @Query("sort") String sort);

    @GET("people/{id}/photos/")
    Call<Pageable<MediaFileMetaData>> photos(@Path("id") Integer id, @Query("page") Integer page, @Query("size") Integer size);

    @GET("people/search")
    Call<Pageable<PersonName>> search (@Query("query") String query, @Query("page") Integer page, @Query("size") Integer size);

    @GET("people/search")
    Call<Pageable<PersonName>> search (@Query("query") String query, @Query("page") Integer page, @Query("size") Integer size, @Query("sort") String sort);
}
