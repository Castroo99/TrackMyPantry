package com.progetto.trackingmypantry.retrofit;


import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    public static MutableLiveData<Integer> codeRequest;
    public static MutableLiveData<AccessToken> accessToken;
    public static MutableLiveData<ProductsToken> productsToken;
    public static MutableLiveData<Integer> registerCode;
    private ExecutorService threadPool;

    public Repository() {
        accessToken = new MutableLiveData<>();
        codeRequest = new MutableLiveData<>();
        productsToken = new MutableLiveData<>();
        registerCode = new MutableLiveData<>();
        threadPool = Executors.newFixedThreadPool(8);
    }

    public void onRegisterClick(String username, String email, String password) {
        threadPool.execute(() -> {

            UtenteRegister utenteRegister = new UtenteRegister(email, username, password);
            Call<Utente> call = RetrofitClient.getInstance().getMyApi().register(utenteRegister);

            call.enqueue(new Callback<Utente>() {
                @Override
                public void onResponse(Call<Utente> call, Response<Utente> response) {
                    if (response.code() == 201) {
                        registerCode.postValue(response.code());
                        Utente myUser = response.body();
                        Log.println(Log.INFO, "Register", myUser.getUsername() +" "+ myUser.getEmail()+" "+myUser.getId()+
                                " "+ myUser.getPassword()+ " " +myUser.getDateCreation()+" "+myUser.getDateUpdate());
                    }
                    else {
                        registerCode.postValue(response.code());
                    }
                }

                @Override
                public void onFailure(Call<Utente> call, Throwable t) {
                    Log.println(Log.INFO, "Register", "Registrazione fallita");
                }
            });
        });
    }

    public void onLoginClick(String email, String password) {
        threadPool.execute(() -> {
            accessToken.postValue(null);
            UtenteLogin utenteLogin = new UtenteLogin(email, password);
            Call<AccessToken> call = RetrofitClient.getInstance().getMyApi().login(utenteLogin);

            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    if (response.code() == 201) {
                        if (response.body() != null) {
                            accessToken.postValue(response.body());
                        }
                    }
                    else {
                        accessToken.postValue(new AccessToken("Errore"));
                    }
                }
                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.println(Log.INFO, "Login", "Login fallito");
                }
            });
        });
    }

    public MutableLiveData<ProductsToken> getProductsOnClick(String accessToken, String barcode) {

        String url = "https://lam21.iot-prism-lab.cs.unibo.it/products?barcode=" + barcode;
        String tokenAuth = "Bearer " + accessToken;
        threadPool.execute(() -> {
            Call<ProductsToken> call = RetrofitClient.getInstance().getMyApi().getProducts(tokenAuth, url);
            call.enqueue(new Callback<ProductsToken>() {
                @Override
                public void onResponse(Call<ProductsToken> call, Response<ProductsToken> response) {
                    Log.println(Log.INFO, "Get Products", String.valueOf(response.code()));
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            productsToken.postValue(response.body());
                        }
                    }
                }
                @Override
                public void onFailure(Call<ProductsToken> call, Throwable t) {
                    Log.println(Log.INFO, "Get Products", "Get Products fallito " + t.getMessage());
                    t.printStackTrace();
                }
            });
        });
        return productsToken;
    }

    public void addProductsOnClick(String accessToken, String tokenProd, String name, String description, String barcode, String imageEncoded) {
        String tokenAuth = "Bearer " + accessToken;
        threadPool.execute(() -> {

            ProductRequest productRequest= new ProductRequest(tokenProd, name, description, barcode, imageEncoded, false);
            Call<Products> call = RetrofitClient.getInstance().getMyApi().postProducts(tokenAuth, productRequest);

            call.enqueue(new Callback<Products>() {
                @Override
                public void onResponse(Call<Products> call, Response<Products> response) {
                    if (response.body() != null) {
                        Products products = response.body();
                        Log.println(Log.INFO, "Post Products", String.valueOf(products.getName()));
                    }
                }
                @Override
                public void onFailure(Call<Products> call, Throwable t) {
                    Log.println(Log.INFO, "Post Products", "Post Products fallito " + t.getMessage());
                    t.printStackTrace();
                }
            });
        });
    }

    public void postPreference(String accessToken, String token, Integer rating, String productId) {
        codeRequest.setValue(0);
        String tokenAuth = "Bearer " + accessToken;
        VotesRequest votesRequest = new VotesRequest(token, rating, productId);
        threadPool.execute(() -> {
            Call<Votes> call = RetrofitClient.getInstance().getMyApi().postVote(tokenAuth, votesRequest);
            call.enqueue(new Callback<Votes>() {
                @Override
                public void onResponse(Call<Votes> call, Response<Votes> response) {
                    if (response.code() == 201) {
                        codeRequest.postValue(response.code());

                        if (response.body() != null) {
                            Votes votes = response.body();
                            Log.println(Log.INFO, "Rating", String.valueOf(votes.getRating()));
                        }
                    }
                    else if (response.code() == 500) {

                        codeRequest.postValue(response.code());
                    }
                }
                @Override
                public void onFailure(Call<Votes> call, Throwable t) {
                    Log.println(Log.INFO, "Post Votes", "Post Products fallito " + t.getMessage());
                    t.printStackTrace();
                }
            });
        });
    }

    public static MutableLiveData<Integer> getCodeRequest() {
        return codeRequest;
    }

    public static MutableLiveData<AccessToken> getAccessToken() {
        return accessToken;
    }

    public static MutableLiveData<Integer> getRegisterCode() {
        return registerCode;
    }
}
