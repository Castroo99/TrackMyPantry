package com.progetto.trackingmypantry.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocalProductsDao {

    @Query("SELECT * FROM localproducts")
    List<LocalProducts> getAllProducts();

    @Insert
    void insertProduct(LocalProducts... products);

    @Delete
    void delete(LocalProducts localProducts);

    @Query("SELECT * FROM localproducts WHERE prod_name = :prodName")
    List<LocalProducts> getSelectedProducts(String prodName);

    @Query("SELECT * FROM localproducts WHERE type = :filter")
    List<LocalProducts> getFilteredProducts(String filter);
}
