package com.example.mychat.services.Retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("completions")
    Call<ChatCompletionResponse> getData(
            @Body RequestBody body
    );
}