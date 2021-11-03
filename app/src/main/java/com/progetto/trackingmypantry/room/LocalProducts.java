package com.progetto.trackingmypantry.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class LocalProducts {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "barcode")
    public String barcode;

    @ColumnInfo(name = "prod_name")
    public String prodName;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "img")
    public String img;

    @ColumnInfo(name = "expiration_date")
    public String expirationDate;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "alarmId")
    public Integer alarmId;

    public String getLocalBarcode() {
        return barcode;
    }

    public String getProdName() {
        return prodName;
    }

    public String getLocalDescription() {
        return description;
    }

    public String getLocalImg() {
        return img;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
