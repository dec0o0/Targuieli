package com.example.lista.cumparaturi.db;

import android.provider.BaseColumns;

/**
 * Created by macbookproritena on 11/5/16.
 */

public final class ContractDB {
    private ContractDB(){;}

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_PRODUSE =
            "CREATE TABLE " + ContractProduse.NUME_TABELA + " (" +
                    ContractProduse._ID + " INTEGER PRIMARY KEY," +
                    ContractProduse.COL_ID + INTEGER_TYPE + COMMA_SEP +
                    ContractProduse.COL_NUME_PROD + TEXT_TYPE + COMMA_SEP +
                    ContractProduse.COL_DESC + TEXT_TYPE +
                    " )";

    public static final String SQL_CREATE_PREFERINTE =
            "CREATE TABLE " + ContractPreferinte.NUME_TABELA + " (" +
                    ContractPreferinte._ID + " INTEGER PRIMARY KEY," +
                    ContractPreferinte.COL_ID_PROD + INTEGER_TYPE + COMMA_SEP +
                    ContractPreferinte.COL_DIST + INTEGER_TYPE + COMMA_SEP +
                    ContractPreferinte.COL_URGENTA + INTEGER_TYPE +
                    " )";

    public static final String SQL_DELETE_PRODUSE =
            "DROP TABLE IF EXISTS " + ContractProduse.NUME_TABELA;

    public static final String SQL_DELETE_PREFERINTE =
            "DROP TABLE IF EXISTS " + ContractPreferinte.NUME_TABELA;

    public static class ContractProduse implements BaseColumns{
        public static final String NUME_TABELA = "produse";
        public static final String COL_ID = "id";
        public static final String COL_NUME_PROD = "nume";
        public static final String COL_DESC = "desc";

        public static String[] getCols(){
            return new String[]{COL_ID, COL_NUME_PROD, COL_DESC};
        }
    }

    public static class ContractPreferinte implements BaseColumns{
        public static final String NUME_TABELA = "preferinte";
        public static final String COL_ID_PROD = "idProdus";
        public static final String COL_DIST = "distantaMaxima";
        public static final String COL_URGENTA = "urgenta";

        public static String[] getCols(){
            return new String[]{COL_ID_PROD, COL_DIST, COL_URGENTA};
        }
    }
}
