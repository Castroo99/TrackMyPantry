package com.progetto.trackingmypantry.ui.localpantry;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.progetto.trackingmypantry.room.LocalProducts;
import com.progetto.trackingmypantry.room.LocalRepository;
import java.util.List;


public class LocalPantryViewModel extends AndroidViewModel {

    private final LocalRepository localRepository;

    public LocalPantryViewModel(@NonNull Application application) {
        super(application);
        localRepository = new LocalRepository(application);
    }

    public List<LocalProducts> getAllProducts() {
        return  localRepository.getAllProducts();
    }

    public List<LocalProducts> getFilteredProducts(String filter) {
        return  localRepository.getFilteredProducts(filter);
    }

    public void insertProduct(LocalProducts localProducts) {
        localRepository.insertProduct(localProducts);
    }

    public void deleteProduct(LocalProducts localProducts) {
        localRepository.deleteProduct(localProducts);
    }

    public List<LocalProducts> searchProduct(String prodName) {
        return localRepository.searchProduct(prodName);
    }

}