package com.progetto.trackingmypantry.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private RetroInterface myRetroInterface;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //Permette di loggare i dettagli delle operazioni HTTP
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetroInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        myRetroInterface = retrofit.create(RetroInterface.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public RetroInterface getMyApi() {
        return myRetroInterface;
    }

}
