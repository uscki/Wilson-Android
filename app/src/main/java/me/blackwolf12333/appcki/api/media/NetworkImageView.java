package me.blackwolf12333.appcki.api.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.Services;
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

    public void setImageIdAndType(Integer id, String type) {
        final String url = String.format(MediaAPI.URL, type, id);

        if (MediaAPI.cacheContains(url)) {
            setImageBitmap(MediaAPI.getFromCache(url));
        } else {
            Call<ResponseBody> call = Services.getInstance().imageService.getAgendaPoster(url);
            call.enqueue(new Callback<ResponseBody>() {
                             @Override
                             public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                 if (response.isSuccessful()) {
                                     Log.d("NetworkImageView", "server contacted and has file");
                                     Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                     setImageBitmap(bitmap);

                                     Log.d("NetworkImageView", "file download was a success? ");
                                 } else {
                                     Log.d("NetworkImageView", "server contact failed");
                                 }
                                 response.body().close();
                             }
                             @Override
                             public void onFailure(Call<ResponseBody> call, Throwable t) {
                                 return;
                             }
                         }
            );
        }
    }

    public void setDefaultImageResId(int defaultImage) {
        this.setImageResource(defaultImage);
    }
}
