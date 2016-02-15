package me.blackwolf12333.appcki.api.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.MediaAPI;

/**
 * Created by peter on 2/6/16.
 */
public class ImageRequest extends Request<Bitmap> {
    public static final String URL = "https://www.uscki.nl/?pagina=Media/MediaObject/%s&mediaFile=%d";
    private final Response.Listener<Bitmap> listener;
    private final Map<String, String> headers;

    private final String mediafileKey = App.getContext().getResources().getString(R.string.mediafile_key);

    public ImageRequest(String url, Map<String, String> headers, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.listener = listener;
        this.headers = headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        String postData = "APPCKISepischeBACKDOOR=" + mediafileKey;
        return postData.getBytes();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return MediaAPI.getBitmapHeaders();
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        if(response.statusCode == 200) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
            return Response.success(bitmap, HttpHeaderParser.parseCacheHeaders(response));
        } else {
            Log.d("ImageRequest", new String(response.data));
        }
        return null;
    }

    @Override
    protected void deliverResponse(Bitmap response) {
        listener.onResponse(response);
    }
}
