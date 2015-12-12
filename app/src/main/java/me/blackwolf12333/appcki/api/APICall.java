package me.blackwolf12333.appcki.api;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;

/**
 * Created by peter on 12/9/15.
 */
public class APICall extends AsyncTask<String, Void, JsonElement> {

    String apiCall;
    User user;

    public APICall(User user, String apiCall) {
        this.apiCall = apiCall;
        this.user = user;
    }

    @Override
    protected JsonElement doInBackground(String... params) {
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
                System.out.println(response);

                elem = new JsonParser().parse(response);
            } else {
                System.out.println(readStream(connection.getErrorStream()));
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return elem;
    }

    private String readStream(InputStream stream) {
        if(stream == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    @Override
    protected void onPostExecute(JsonElement obj) {
        super.onPostExecute(obj);
    }
}
