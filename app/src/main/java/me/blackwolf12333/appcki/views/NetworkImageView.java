package me.blackwolf12333.appcki.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.net.ConnectException;

import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.error.ConnectionError;
import me.blackwolf12333.appcki.generated.media.MediaFile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by peter on 7/12/16.
 */
public class NetworkImageView extends ImageView {
    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(final String url) {
        if (MediaAPI.cacheContains(url)) {
            setImageBitmap(MediaAPI.getFromCache(url));
        } else {
            Call<ResponseBody> call = Services.getInstance().imageService.getImage(url);
            call.enqueue(new Callback<ResponseBody>() {
                             @Override
                             public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                 if (response.isSuccessful()) {
                                     Log.v("NetworkImageView", "server contacted and has file");
                                     Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                     if (bitmap != null) {
                                         setImageBitmap(bitmap);
                                         MediaAPI.putInCache(bitmap, url);
                                         Log.v("NetworkImageView", "file download was a success!");
                                     } else {
                                         Log.v("NetworkImageView", "file download was a failure!");
                                     }
                                     response.body().close();
                                 } else {
                                     Log.v("NetworkImageView", "server contact failed");
                                 }
                             }
                             @Override
                             public void onFailure(Call<ResponseBody> call, Throwable t) {
                                 if (t instanceof ConnectException) {
                                     new ConnectionError(t); // handle connection error in MainActivity
                                 } else {
                                     throw new RuntimeException(t);
                                 }
                             }
                         }
            );
        }
    }

    public void setImageMediaId(Integer id) {
        final String url = MediaAPI.API_URL + id + "/medium";
        if (MediaAPI.cacheContains(url)) {
            setImageBitmap(MediaAPI.getFromCache(url));
        } else {
            Call<ResponseBody> call = Services.getInstance().mediaService.file(id, "medium");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.v("NetworkImageView", "api contacted and has file");
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        if (bitmap != null) {
                            setImageBitmap(bitmap);
                            MediaAPI.putInCache(bitmap, url);
                            Log.v("NetworkImageView", "file download was a success!");
                        } else {
                            Log.v("NetworkImageView", "file download was a failure!");
                        }
                        response.body().close();
                    } else {
                        Log.v("NetworkImageView", "api contact failed");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof ConnectException) {
                        new ConnectionError(t); // handle connection error in MainActivity
                    } else {
                        throw new RuntimeException(t);
                    }
                }
            });
        }
    }

    public void setImageMediaFile(MediaFile file) {
        this.setImageIdAndType(file.getId(), MediaAPI.getFiletypeFromMime(file.getMimetype()));
    }

    public void setImageIdAndType(Integer id, String type) {
        final String url = String.format(MediaAPI.URL, type, id);
        this.setImageUrl(url);
    }

    public void setDefaultImageResId(int defaultImage) {
        this.setImageResource(defaultImage);
    }
}
