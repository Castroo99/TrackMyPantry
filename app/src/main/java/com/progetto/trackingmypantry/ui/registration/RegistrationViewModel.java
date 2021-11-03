package com.progetto.trackingmypantry.ui.registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.progetto.trackingmypantry.retrofit.Repository;


public class RegistrationViewModel extends ViewModel {

    private final Repository repository;
    private MutableLiveData<Integer> code;

    public RegistrationViewModel() {
        repository = new Repository();
        code = repository.getRegisterCode();
    }

    public void register(String username, String email, String password) {
        repository.onRegisterClick(username, email, password);
    }

    public LiveData<Integer> getRegisterCodeValue() {
        return code;
    }
}