package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.ParsedBBCode;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BBService {

    @POST("bb/parse/")
    Call<ActionResponse<ParsedBBCode>> parse(@Body RequestBody encodedText, @Query("tags") String... tags);
}
