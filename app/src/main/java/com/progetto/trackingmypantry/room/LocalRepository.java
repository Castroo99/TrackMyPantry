package com.progetto.trackingmypantry.room;

import android.app.Application;
import java.util.List;

public class LocalRepository {
    private LocalProductsDao localProductsDao;

    public LocalRepository(Application application){
        Database database = Database.getDbInstance(application);
        localProductsDao = database.localProductsDao();
    }

    public List<LocalProducts> getAllProducts() {
        return localProductsDao.getAllProducts();
    }

    public List<LocalProducts> getFilteredProducts(String filter) {
        return localProductsDao.getFilteredProducts(filter);
    }

    public void insertProduct(LocalProducts localProducts) {
        localProductsDao.insertProduct(localProducts);
    }

    public void deleteProduct(LocalProducts localProducts) {
        localProductsDao.delete(localProducts);
    }

    public List<LocalProducts> searchProduct(String prodName) {
        return localProductsDao.getSelectedProducts(prodName);
    }
}
