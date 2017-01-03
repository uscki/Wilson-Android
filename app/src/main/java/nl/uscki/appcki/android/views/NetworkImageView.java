package nl.uscki.appcki.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.media.MediaFile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by peter on 7/12/16.
 */
public class NetworkImageView extends ImageView {
    private Integer mediaId;
    private int defaultResourceId;

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
                public void onSucces(Response<ResponseBody> response) {
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
                }
            }
            );
        }
    }

    public void setImageMediaId(Integer id, MediaAPI.MediaSize size) {
        final String url = MediaAPI.API_URL + id + "/" + size;
        this.mediaId = id;
        if (MediaAPI.cacheContains(url)) {
            setImageBitmap(MediaAPI.getFromCache(url));
        } else {
            Call<ResponseBody> call = Services.getInstance().mediaService.file(id, MediaAPI.MediaSize.NORMAL.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onSucces(Response<ResponseBody> response) {
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
                }

                //TODO figure out if unsuccesful calls can happen and if so use the retrofit Callback
                //TODO instead of this one because we need to set de default ImageResource
                /*@Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                    } else {
                        Log.v("NetworkImageView", "error: " + response.message());
                        setImageResource(defaultResourceId); // set default resource
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof ConnectException) {
                        new ConnectionError(t); // handle connection error in MainActivity
                    } else {
                        throw new RuntimeException(t);
                    }
                }*/
            });
        }
    }

    public void setImageMediaId(Integer id) {
        setImageMediaId(id, MediaAPI.MediaSize.NORMAL);
    }

    public void setImageMediaFile(MediaFile file) {
        this.setImageIdAndType(file.getId(), MediaAPI.getFiletypeFromMime(file.getMimetype()));
    }

    public void setImageIdAndType(Integer id, String type) {
        final String url = String.format(MediaAPI.URL, type, id);
        this.setImageUrl(url);
    }

    public void setDefaultImageResId(int defaultImage) {
        this.defaultResourceId = defaultImage;
        this.setImageResource(defaultImage);
    }

    public Integer getMediaId() {
        return mediaId;
    }
}
