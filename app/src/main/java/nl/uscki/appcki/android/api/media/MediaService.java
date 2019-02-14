package nl.uscki.appcki.android.api.media;

import nl.uscki.appcki.android.generated.media.MediaFile;
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
    Call<MediaFile> get(@Query("id") Integer id);

    @GET("media/file/{id}/{size}")
    Call<ResponseBody> file(@Path("id") Integer id, @Path("size") String size);
}
