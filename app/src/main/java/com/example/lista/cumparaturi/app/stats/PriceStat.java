package com.example.lista.cumparaturi.app.stats;

import java.util.Date;

/**
 * Created by macbookproritena on 11/21/16.
 */

public class PriceStat {
    private final Date data;
    private final float price;
    private final int stock;

    public PriceStat(float price, int stock, Date data) {
        this.price = price;
        this.stock = stock;
        this.data = data;
    }

    public float getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Date getData() {
        return data;
    }
}
