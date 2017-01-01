package nl.uscki.appcki.android.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;

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
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            onSucces(response);
        } else {
            handleError(response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof ConnectException) {
            new ConnectionError(t); // handle connection error in MainActivity
        } else {
            throw new RuntimeException(t);
        }
    }

    private void handleError(Response<T> response) {
        try {
            Gson gson = new Gson();
            ServerError error = gson.fromJson(response.raw().body().string(), ServerError.class);
            EventBus.getDefault().post(new ServerErrorEvent(error));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onSucces(Response<T> response);
}
