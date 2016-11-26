package com.example.lista.cumparaturi.app.beans;

import com.example.lista.cumparaturi.R;

/**
 * Created by macbookproritena on 11/9/16.
 */

public enum Trend {
    FLAT(R.drawable.ic_trending_flat_black_24dp),
    DOWNWARDS(R.drawable.ic_trending_down_black_24dp),
    UPPWARDS(R.drawable.ic_trending_up_black_24dp);

    private final int resource;

    Trend(int resource) {
        this.resource = resource;
    }

    public static Trend getByVal(double val){
        if(val < 0) return DOWNWARDS;
        else if(val > 0) return UPPWARDS;
        return FLAT;
    }

    public int getResource() {
        return resource;
    }
}
