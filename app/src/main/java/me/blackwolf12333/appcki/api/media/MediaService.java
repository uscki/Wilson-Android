package me.blackwolf12333.appcki.api.media;

import me.blackwolf12333.appcki.generated.media.MediaFile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 7/12/16.
 */
public interface MediaService {
    @GET("media/get")
    Call<MediaFile> get(@Query("id") Integer id);
}
