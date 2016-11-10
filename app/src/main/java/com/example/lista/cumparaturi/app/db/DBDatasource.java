package com.example.lista.cumparaturi.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Produs;
import com.example.lista.cumparaturi.app.beans.Urgente;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class DBDatasource {
    private SQLiteDatabase db;
    private final DBHelper dbHelper;

    public DBDatasource(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void openConnection(){
        db = dbHelper.getWritableDatabase();
    }

    public void closeConnection(){
        db.close();
    }

    public Set<Produs> fetchProduse(){
        Set<Produs> produse = new LinkedHashSet<>();
        Cursor cursor = db.query(
                ContractDB.ContractProduse.NUME_TABELA,
                ContractDB.ContractProduse.getCols(),
                null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            int idx = 0;
            int prodId = cursor.getInt(idx++);
            String numeProd = cursor.getString(idx++);
            String desc = cursor.getString(idx);

            produse.add(new Produs(prodId, numeProd, desc));
            cursor.moveToNext();
        }
        cursor.close();
        return produse;
    }

    public Set<Preferinta> fetchPreferinte(List<Produs> produse){
        Set<Preferinta> preferinte = new LinkedHashSet<>();
        Cursor cursor = db.query(
                ContractDB.ContractPreferinte.NUME_TABELA,
                ContractDB.ContractPreferinte.getCols(),
                null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            int idx = 0;
            int prodId = cursor.getInt(idx++);
            int dist = cursor.getInt(idx++);
            int urgenta = cursor.getInt(idx++);

            for (Produs prod: produse) {
                if(prod.getId() == prodId){
                    preferinte.add(new Preferinta(prod, dist, Urgente.getByUrgencyValue(urgenta)));
                    break;
                }
            }

            cursor.moveToNext();
        }
        cursor.close();
        return preferinte;

    }

    public void adaugaProdus(Produs p){
        ContentValues values = new ContentValues();
        values.put(ContractDB.ContractProduse.COL_ID, p.getId());
        values.put(ContractDB.ContractProduse.COL_NUME_PROD, p.getName());
        values.put(ContractDB.ContractProduse.COL_DESC, p.getDesc());
        db.insertWithOnConflict(
                ContractDB.ContractProduse.NUME_TABELA,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void adaugaPreferinta(Preferinta p){
        ContentValues values = new ContentValues();
        values.put(ContractDB.ContractPreferinte.COL_ID_PROD, p.getProdus().getId());
        values.put(ContractDB.ContractPreferinte.COL_DIST, p.getMaxDistanta());
        values.put(ContractDB.ContractPreferinte.COL_URGENTA, p.getUrgente().getMidValue());
        db.insertWithOnConflict(
                ContractDB.ContractPreferinte.NUME_TABELA,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean deleteProd(Produs p){
        return db.delete(ContractDB.ContractProduse.NUME_TABELA,
                ContractDB.ContractProduse.COL_ID + "=\"?\"",
                new String[]{p.getId() + ""}
                ) != 0;
    }

    public boolean deletePref(Preferinta p){
        return db.delete(ContractDB.ContractPreferinte.NUME_TABELA,
                ContractDB.ContractPreferinte.COL_ID_PROD + "=\"?\"",
                new String[]{p.getProdus().getId() + ""}
                ) != 0;
    }
}
