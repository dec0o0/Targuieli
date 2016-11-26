package com.example.lista.cumparaturi.app.stats;

import com.example.lista.cumparaturi.app.beans.Produs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookproritena on 11/21/16.
 */

public class ProdInfo {
    private final Produs prod;
    private final List<Locatie> locatieList;

    public ProdInfo(Produs prod) {
        this.prod = prod;
        this.locatieList = new ArrayList<>();
    }

    public Produs getProd() {
        return prod;
    }

    public List<Locatie> getLocatieList() {
        return locatieList;
    }

    public void addLocatie(Locatie l){this.locatieList.add(l);}
}
