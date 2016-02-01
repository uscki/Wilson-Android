package me.blackwolf12333.appcki.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;

/**
 * Created by peter on 12/9/15.
 */
public class APICall extends AsyncTask<String, Void, Void> {

    String apiCall;
    User user;

    public APICall(User user, String apiCall) {
        this.apiCall = apiCall;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        URL api = null;
        HttpURLConnection connection = null;
        JsonElement elem = null;

        try {
            String callUrl = App.getContext().getResources().getString(R.string.apiurl) + apiCall;

            if(params.length != 0) {
                callUrl += "?";
                for(String p : params) {
                    callUrl += p + "&";
                }

            }

            api = new URL(callUrl);
            connection = (HttpURLConnection) api.openConnection();
            connection.setRequestProperty("X-AUTH-TOKEN", user.TOKEN);
            connection.setRequestMethod("GET");

            if(connection.getResponseCode() == 200) {
                String response = readStream(connection.getInputStream());
                Log.d("APICall", "got response: " + response);
                elem = new JsonParser().parse(response);
            } else {
                elem = new JsonParser().parse(IOUtils.toString(connection.getErrorStream()));
                int code = (int)elem.getAsJsonObject().get("status").getAsInt();
                if(code == 403) {
                    // TODO: 12/18/15 handle errors properly
                }

                System.out.println(IOUtils.toString(connection.getErrorStream()));
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        EventBus.getDefault().post(new JSONReadyEvent(elem, apiCall));
        return null;
    }

    private String readStream(InputStream stream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[512];
        try {
            while (-1 != (len = stream.read(buffer))) {
                bos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toString();
    }
}
