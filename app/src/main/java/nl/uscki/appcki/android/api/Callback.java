package nl.uscki.appcki.android.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Date;

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
        } else if(t instanceof SocketTimeoutException) {
            new ConnectionError(t); // handle connection error in MainActivity
        } else {
            throw new RuntimeException(t);
        }
    }

    private void handleError(Response<T> response) {
        try {
            Gson gson = new Gson();
            ServerError error = gson.fromJson(response.raw().body().string(), ServerError.class);
           // ServerError error1 = new ServerError();
            //error1.setError("I don't know man");
            //error1.setException("This shit is weird");
            //error1.setMessage("Tried rebooting?");
            //error1.setPath("/api/unsolvableURL");
            //error1.setStatus(500);
            //error1.setTimestamp(new Date().getTime());
            EventBus.getDefault().post(new ServerErrorEvent(error));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e)
        {
            ServerError error = new ServerError();
            error.setError("Internal Server Error");
            error.setException("java.lang.IllegalStateException");
            error.setMessage("Error in parsing error message from server. So meta.");
            error.setPath("");
            error.setStatus(500);
            error.setTimestamp(new Date().getTime());
            EventBus.getDefault().post(new ServerErrorEvent(error));
        }
    }

    public abstract void onSucces(Response<T> response);
}
