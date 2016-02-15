package me.blackwolf12333.appcki.api.common;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.events.ServerErrorEvent;
import me.blackwolf12333.appcki.generated.ServerError;
import me.blackwolf12333.appcki.helpers.UserHelper;

/**
 * Created by peter on 2/6/16.
 */
public class VolleyAPI {
    protected String url = App.getContext().getResources().getString(R.string.apiurl);

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-AUTH-TOKEN", UserHelper.getInstance().getUser().TOKEN);
        return headers;
    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error.networkResponse != null) {
                Gson gson = new Gson();
                ServerError serverError = gson.fromJson(new String(error.networkResponse.data), ServerError.class);
                EventBus.getDefault().post(new ServerErrorEvent(serverError));
            }
        }
    };

    protected void apiCall(Call call) {
        GsonRequest gsonRequest = new GsonRequest(url + call.getRequestUrl(), call.type, getHeaders(), call.responseListener, errorListener);
        APISingleton.getInstance(App.getContext()).addToRequestQueue(gsonRequest);
    }

    public class Call<T> {
        protected String url;
        protected HashMap<String, Object> arguments;
        protected Class<T> type;
        protected Response.Listener<T> responseListener;

        public String getRequestUrl() {
            String ret = url + "?";
            for(String key : arguments.keySet()) {
                ret += key+"="+arguments.get(key)+"&";
            }
            return ret;
        }
    }
}
