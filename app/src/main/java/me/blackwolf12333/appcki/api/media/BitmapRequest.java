package me.blackwolf12333.appcki.api.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

/**
 * Created by peter on 2/15/16.
 */
public class BitmapRequest extends Request<Bitmap> {
    private final Response.Listener<Bitmap> listener;
    private final Response.ErrorListener errorlistener;

    public BitmapRequest(String url, Map<String, String> headers, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BitmapRequest", new String(error.networkResponse.data));
            }
        });
        this.listener = listener;
        this.errorlistener = errorListener;
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        if(response.statusCode == 200) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
            return Response.success(bitmap, HttpHeaderParser.parseCacheHeaders(response));
        }
        return null;
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        errorlistener.onErrorResponse(volleyError);
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected void deliverResponse(Bitmap response) {
        listener.onResponse(response);
    }
}
