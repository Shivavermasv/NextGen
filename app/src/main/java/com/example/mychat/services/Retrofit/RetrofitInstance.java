package com.example.mychat.services.Retrofit;

import androidx.annotation.NonNull;

import com.example.mychat.constants.AppKeys;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class RetrofitInstance {
    public static RetrofitInstance instance;
    public APIInterface apiInterface;
    static String BASE_URL = "https://api.openai.com/v1/chat/";
    RetrofitInstance(){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout ( 40, TimeUnit.SECONDS ).writeTimeout ( 20,TimeUnit.SECONDS )
                .addInterceptor(new Interceptor () {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer " + AppKeys.GPT_TOKEN )
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();


        apiInterface = new Retrofit.Builder ()
                .client ( client )
                .baseUrl ( BASE_URL )
                .addConverterFactory ( GsonConverterFactory.create () )
                .build ().create ( APIInterface.class );
    }
    public static RetrofitInstance getInstance(){
        if(instance == null){
            instance = new RetrofitInstance ();
        }
        return instance;
    }
}
