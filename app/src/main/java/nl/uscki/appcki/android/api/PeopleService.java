package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.generated.organisation.Person;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peter on 2/23/17.
 */

public interface PeopleService {
    @GET("person/get")
    Call<Person> get(@Query("id") Integer id);
}
