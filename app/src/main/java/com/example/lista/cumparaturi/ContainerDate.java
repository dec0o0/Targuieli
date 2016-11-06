package com.example.lista.cumparaturi;

import android.content.Context;

import com.example.lista.cumparaturi.beans.Preferinta;
import com.example.lista.cumparaturi.beans.Produs;
import com.example.lista.cumparaturi.beans.Urgente;
import com.example.lista.cumparaturi.db.DBDatasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by macbookproritena on 11/5/16.
 */

public final class ContainerDate {
    private boolean readFromDb;
    private List<Produs> produse;
    private List<Preferinta> preferinte;
    private DBDatasource datasource;

    private static ContainerDate instance;
    private final int INIT_SIZE = 10;

    private ContainerDate(){
        this.produse = new ArrayList<>(INIT_SIZE);
        this.preferinte = new ArrayList<>(INIT_SIZE);
        this.readFromDb = false;
    }

    public static ContainerDate instance(){
        if(instance == null){
            instance = new ContainerDate();
        }
        return instance;
    }

    public List<Produs> getProduse() {
        return produse;
    }

    public List<Preferinta> getPreferinte() {
        return preferinte;
    }

    public void addProdus(Produs s){
        if(!produse.contains(s)) {
            produse.add(s);
        }
    }

    public void addPreferinta(Preferinta p){
        if(!preferinte.contains(p)) {
            preferinte.add(p);
            PreferinteEventManger.manager().triggerAddedEvent(p);
        }
    }

    public void loadAllFromDb(Context context){
        if(readFromDb) return;
        datasource = new DBDatasource(context);
        datasource.openConnection();
        produse = new ArrayList<>(datasource.fetchProduse());
        preferinte = new ArrayList<>(datasource.fetchPreferinte(produse));
        readFromDb = true;
    }

    public void saveAllToDB(){
        for(Produs p : produse){
            datasource.adaugaProdus(p);
        }
        for(Preferinta p : preferinte){
            datasource.adaugaPreferinta(p);
        }
    }

    public void closeDBConnection(boolean save){
        if(save){
            saveAllToDB();
        }
        datasource.closeConnection();
    }

    public void loadDummyData(){
        Produs a, b, c;
        a = new Produs(123, "Philips 332W", "Masina de spalat speciala");
        b = new Produs(132, "Vw Golf 1.9TDI", "Masina speciala Volkwagen.");
        c = new Produs(312, "Microsoft E332", "Mouse pt laptopul personal.");
        Preferinta aa, bb, cc;
        aa = new Preferinta(a, 300, Urgente.FOARTE);
        bb = new Preferinta(b, 100, Urgente.PUTIN);
        cc = new Preferinta(c, 332, Urgente.DELOC);

        produse.add(a);
        produse.add(b);
        produse.add(c);
        preferinte.add(aa);
        preferinte.add(bb);
        preferinte.add(cc);
    }
}
