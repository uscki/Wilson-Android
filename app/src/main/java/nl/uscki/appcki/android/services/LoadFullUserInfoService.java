package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.organisation.CurrentUser;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

public class LoadFullUserInfoService extends IntentService {

    public static final String ACTION_LOAD_USER = "nl.uscki.appcki.android.services.action.LOAD_FULL_USER_INFO";

    public LoadFullUserInfoService() {
        super("LoadFullUserInfoService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(ACTION_LOAD_USER.equals(action)) {
                Services.getInstance().userService.currentUser().enqueue(callback);
            }
        }
    }

    Callback<CurrentUser> callback = new Callback<CurrentUser>() {
        @Override
        public void onSucces(Response response) {
            CurrentUser user = (CurrentUser) response.body();
            UserHelper.getInstance().setCurrentUser(user);
        }
    };
}
