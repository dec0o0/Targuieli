package com.example.lista.cumparaturi.app;

import android.content.Context;

import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Produs;
import com.example.lista.cumparaturi.app.beans.Urgente;
import com.example.lista.cumparaturi.app.db.DBDatasource;
import com.example.lista.cumparaturi.app.internals.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookproritena on 11/5/16.
 */

public final class ContainerDate {
    private static ContainerDate instance;

    private final int INIT_SIZE = 10;
    private final List<Produs> produse = new ArrayList<>(INIT_SIZE);
    private final List<Preferinta> preferinte = new ArrayList<>(INIT_SIZE);

    private boolean readFromDb =false, dummydataloaded = false;
    private DBDatasource datasource = null;

    private ContainerDate(){}

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

    public Preferinta getPrefByProd(Produs p){
        for(Preferinta preferinta : getPreferinte()){
            if (preferinta.getProdus().getName().equals(p.getName())){
                return preferinta;
            }
        }
        return null;
    }

    public void addProdus(Produs s){
        if(!produse.contains(s)) {
            produse.add(s);
        }
    }

    public void addPreferinta(Preferinta p){
        if(!preferinte.contains(p)) {
            preferinte.add(p);
            EventManager.manager().triggerAddedEvent(p);
        }
    }

    public void loadAllFromDb(Context context){
        if(readFromDb) return;
        if(datasource == null) datasource = new DBDatasource(context);
        datasource.openConnection();
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
        if(dummydataloaded) return;;
        Produs a, b, c;
        a = new Produs(17, "Philips 332W", "Masina de spalat speciala");
        b = new Produs(15, "Vw Golf 1.9TDI", "Masina speciala Volkwagen.");
        c = new Produs(17, "Microsoft E332", "Mouse pt laptopul personal.");
        Preferinta aa, bb, cc;
        aa = new Preferinta(a, 30, Urgente.FOARTE);
        bb = new Preferinta(b, 14, Urgente.PUTIN);
        cc = new Preferinta(c, 44, Urgente.DELOC);

        produse.add(a);
        produse.add(b);
        produse.add(c);
        preferinte.add(aa);
        preferinte.add(bb);
        preferinte.add(cc);
        dummydataloaded = true;
    }
}
