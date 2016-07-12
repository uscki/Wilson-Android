package me.blackwolf12333.appcki.api.media;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by peter on 7/12/16.
 */
public interface ImageService {
    //@Streaming
    @GET
    Call<ResponseBody> getAgendaPoster(@Url String url);
}
