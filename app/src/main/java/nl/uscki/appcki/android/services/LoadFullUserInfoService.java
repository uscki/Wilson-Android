package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.organisation.Person;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
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
                Services.getInstance().smoboService.get(UserHelper.getInstance().getPerson().getId()).enqueue(callback);
            }
        }
    }

    Callback<SmoboItem> callback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response response) {
            SmoboItem mySmobo = (SmoboItem) response.body();
            Person person = mySmobo.getPerson();
            UserHelper.getInstance().setFullPerson(person);
        }
    };
}
