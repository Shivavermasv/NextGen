package Retrofit;

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("completions")
    Call<ChatCompletionResponse> getData(
            @Body RequestBody body
    );
}
