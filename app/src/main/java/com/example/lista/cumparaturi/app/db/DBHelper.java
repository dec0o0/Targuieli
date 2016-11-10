package com.example.lista.cumparaturi.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "cumparaturi.db";
    private final static int DB_V = 1;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_V);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ContractDB.SQL_CREATE_PRODUSE);
        sqLiteDatabase.execSQL(ContractDB.SQL_CREATE_PREFERINTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ContractDB.SQL_DELETE_PREFERINTE);
        sqLiteDatabase.execSQL(ContractDB.SQL_DELETE_PRODUSE);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
