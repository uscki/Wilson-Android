package nl.uscki.appcki.android.api.media;

import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaCollection;
import nl.uscki.appcki.android.generated.media.MediaFile;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface MediaService {
    @GET("media/get")
    @Deprecated
    Call<MediaFile> get(@Query("id") Integer id);

    @GET("media/file/{id}/{size}")
    Call<ResponseBody> file(@Path("id") Integer id, @Path("size") String size);

    @GET("media/collections/{collection}/photos/")
    Call<Pageable<MediaFileMetaData>> photos(@Path("collection") int collectionId, @Query("page") int page, @Query("size") int size);

    @GET("media/collections/")
    Call<Pageable<MediaCollection>> getToplevelCollection(@Query("page") int page, @Query("size") int size);

    @GET("media/collections/")
    Call<Pageable<MediaCollection>> getCollection(@Query("id") int parentCollectionId, @Query("page") int page, @Query("size") int size);

    @GET("media/collections/{collection}")
    Call<MediaCollection> getCollectionDetails(@Path("collection") Integer subCollectionId, @Query("page") int page, @Query("size") int size);

}
