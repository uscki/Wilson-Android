package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by peter on 12/30/16.
 */

public interface UserService {
    @GET("user/current")
    Call<PersonSimple> currentUser();
}
