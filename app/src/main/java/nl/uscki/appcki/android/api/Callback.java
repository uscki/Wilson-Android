package nl.uscki.appcki.android.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.generated.ServerError;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by peter on 1/1/17.
 */

public abstract class Callback<T> implements retrofit2.Callback<T> {
    protected String requestUrl = "";
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        requestUrl = call.request().url().toString();
        if(response.isSuccessful()) {
            onSucces(response);
        } else {
            handleError(response);
            onError(response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        new ConnectionError(t);
        t.printStackTrace();
    }

    private void handleError(Response<T> response) {
        try {
            Gson gson = new Gson();
            ServerError error = gson.fromJson(response.errorBody().string(), ServerError.class);
            EventBus.getDefault().post(new ServerErrorEvent(error));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            // NOT A CORRECTLY FORMATTED SERVER ERROR
            // gebeurt bijvoorbeeld bij het ophalen van de news icons
            // ook als de api down is, maar tomcat nog niet
            new ConnectionError(null);
        }
    }

    public abstract void onSucces(Response<T> response);

    /**
     * Optional class for additional error handling on top of the default error handling
     * @param response  Original response
     */
    public void onError(Response<T> response) { }
}
