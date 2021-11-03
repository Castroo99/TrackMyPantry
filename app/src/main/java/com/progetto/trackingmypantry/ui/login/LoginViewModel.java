package com.progetto.trackingmypantry.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.progetto.trackingmypantry.retrofit.Repository;
import com.progetto.trackingmypantry.retrofit.AccessToken;


public class LoginViewModel extends ViewModel {

    private final Repository repository;
    MutableLiveData<AccessToken> accessTokenMutableLiveData;

    public LoginViewModel() {
        repository = new Repository();
        accessTokenMutableLiveData = repository.getAccessToken();
    }

    public void login(String email, String password) {
        repository.onLoginClick(email, password);
    }

    public LiveData<AccessToken> getAccessTokenMutableLiveData() {
        return accessTokenMutableLiveData;
    }
}