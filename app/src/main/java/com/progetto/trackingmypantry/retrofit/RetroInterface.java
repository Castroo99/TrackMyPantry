package com.progetto.trackingmypantry.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetroInterface {
    String BASE_URL = "https://lam21.iot-prism-lab.cs.unibo.it/";

    @POST("users")
    Call<Utente> register(@Body UtenteRegister utenteRegister);

    @POST("auth/login")
    Call<AccessToken> login(@Body UtenteLogin utenteLogin);

    @GET
    Call<ProductsToken> getProducts(@Header("Authorization") String accessToken, @Url String url);

    @POST("products")
    Call<Products> postProducts(@Header("Authorization") String accessToken,
                                @Body ProductRequest productRequest);

    @POST("/votes")
    Call<Votes> postVote(@Header("Authorization")String accessToken,
                         @Body VotesRequest vote);
}
