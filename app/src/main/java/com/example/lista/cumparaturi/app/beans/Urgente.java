package com.example.lista.cumparaturi.app.beans;

import com.example.lista.cumparaturi.R;

/**
 * Created by macbookproritena on 11/5/16.
 */

public enum Urgente {
    DELOC("deloc urgent", 0, 9, R.color.white),
    PUTIN("putin urgent", 10, 19, R.color.yellow_low),
    URGENT("urgent", 20, 29, R.color.yellow_high),
    FOARTE("foarte urgent", 30, 39, R.color.accentColor),
    DEOSEBIT("deosebit de urgent", 40, 40, R.color.accentColorPressed);

    private String textValue;
    private int min, max, color;

    Urgente(String value, int min, int max, int color){
        this.textValue = value;
        this.min = min;
        this.max = max;
        this.color = color;
    }

    public int getMidValue(){
        return (min + max) / 2;
    }

    public String getTextValue() {
        return textValue;
    }

    public int getColor() {
        return color;
    }

    public static Urgente getByUrgencyValue(int value){
        for(Urgente urgente : Urgente.values()){
            if(value >= urgente.min &&
                    value <= urgente.max){
                return urgente;
            }
        }
        return DELOC;
    }
}
