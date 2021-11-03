package com.progetto.trackingmypantry.room;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {LocalProducts.class}, version = 5, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract LocalProductsDao localProductsDao();

    private static Database INSTANCE;

    public static Database getDbInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, "PANTRY")
                       .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
        }
        return INSTANCE;
    }

}
