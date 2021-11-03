package com.progetto.trackingmypantry.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.progetto.trackingmypantry.retrofit.ProductsToken;
import com.progetto.trackingmypantry.retrofit.Repository;

public class HomeViewModel extends ViewModel {

    private final Repository repository;
    private MutableLiveData<Integer> codeRequest;
    private final MutableLiveData<String> stringScanned = new MutableLiveData<>();

    public HomeViewModel() {
        codeRequest = new MutableLiveData<>();
        repository = new Repository();
    }

    public MutableLiveData<ProductsToken> loadProducts(String accessToken, String barcode) {
        setCodeRequest(Repository.getCodeRequest());
        return repository.getProductsOnClick(accessToken, barcode);
    }

    public void addProducts(String accessToken, String tokenProd, String name, String description, String imageEncoded, String barcode) {
        repository.addProductsOnClick(accessToken, tokenProd, name, description , barcode, imageEncoded);
    }

    public void postVote(String accessToken, String sessionToken, Integer rating, String productId) {
        repository.postPreference(accessToken, sessionToken, rating, productId);
    }

    public MutableLiveData<Integer> getCodeRequest() {
        return codeRequest;
    }

    public void setCodeRequest(MutableLiveData<Integer> codeRequest) {
        this.codeRequest = codeRequest;
    }

    public MutableLiveData<String> getStringScanned() {
        return stringScanned;
    }

    public void setStringScanned(String stringScanned) {
        this.stringScanned.setValue(stringScanned);
    }
}