package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.helpers.UserHelper;

/**
 * Created by peter on 2/6/16.
 */
public class VolleyAPI {
    String url = App.getContext().getResources().getString(R.string.apiurl);

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-AUTH-TOKEN", UserHelper.getInstance().getUser().TOKEN);
        return headers;
    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("VolleyAPI", "" +error.networkResponse.statusCode);
        }
    };

    protected void apiCall(Call call) {
        GsonRequest gsonRequest = new GsonRequest(url + call.getRequestUrl(), call.type, getHeaders(), call.responseListener, errorListener);
        APISingleton.getInstance(App.getContext()).addToRequestQueue(gsonRequest);
    }

    public class Call<T> {
        String url;
        HashMap<String, Object> arguments;
        Class<T> type;
        Response.Listener<T> responseListener;

        public String getRequestUrl() {
            String ret = url + "?";
            for(String key : arguments.keySet()) {
                ret += key+"="+arguments.get(key)+"&";
            }
            return ret;
        }
    }
}
