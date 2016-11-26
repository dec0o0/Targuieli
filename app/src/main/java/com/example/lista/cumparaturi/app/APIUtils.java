package com.example.lista.cumparaturi.app;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Pair;

import com.example.lista.cumparaturi.app.beans.Produs;
import com.example.lista.cumparaturi.app.stats.Locatie;
import com.example.lista.cumparaturi.app.stats.PriceStat;
import com.example.lista.cumparaturi.app.stats.ProdInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class APIUtils {
    private APIUtils(){;}

    public static final String preventNullString = "";
    public static final int defaultProdusId = -1;
    public static final String PREF_EXTRA = "preferinta-input";

    private static final String REMOTE_API_URL = "http://proiectsoftwareinechipa.16mb.com/api/index.php";
    private static final String ACTION_TAG = "action";
    private static final String ACTION_VAL = "GetProductListByName";
    private static final String PRODUCT_NAME_TAG = "ProductName";
    private static final String PROD_DESC_TAG = "ProductDescription";
    private static final String PROD_ID_TAG = "ProductId";

    @NonNull public static List<Produs> getRemoteProducts(String prodName){

        List<Produs> produses = new LinkedList<>();

        JsonReader reader = null;
        try {
            reader = getFromRemote(
                    new Pair<String, String>(ACTION_TAG, ACTION_VAL),
                    new Pair<String, String>(PRODUCT_NAME_TAG, prodName));

            //reader = new FetchFromRemote().execute().get();

            if (reader == null) return null;
            reader.beginArray();
            while(reader.hasNext()){
                produses.add(readProdus(reader));
            }
            reader.endArray();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return produses;
    }

    public static ProdInfo getPreturiProdus(Produs p){
        JsonReader reader = null;
        ProdInfo prodInfo = new ProdInfo(p);

        try{
            reader = getFromRemote(
                    new Pair<String, String>(ACTION_TAG, "GetProductPricesGeneral"),
                    new Pair<String, String>(PROD_ID_TAG, "17"));

            if (reader == null) return null;
            reader.beginArray();
            while(reader.hasNext()){
                prodInfo.addLocatie(readLocatie(reader));
            }
            reader.endArray();
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return prodInfo;
    }

    private static Locatie readLocatie(JsonReader reader) throws IOException, ParseException {
        String name = null, adresa = null;
        List<PriceStat> stats = new ArrayList<>();

        reader.beginObject();
        while(reader.hasNext()){
            String nextName = reader.nextName();
            if(nextName.equalsIgnoreCase("LocationName")){
                name = reader.nextString();
            }
            else if (nextName.equalsIgnoreCase("LocationAdress")){
                adresa = reader.nextString();
            }
            else if (nextName.equalsIgnoreCase("PriceAndStock")){
                reader.beginArray();
                while(reader.hasNext()){
                    stats.add(readStat(reader));
                }
                reader.endArray();
            }
            else
                reader.skipValue();
        }
        reader.endObject();

        Locatie l = new Locatie(adresa, name);
        for(PriceStat p : stats)
            l.addPriceStat(p);
        return l;
    }

    private static PriceStat readStat(JsonReader reader) throws IOException, ParseException {
        float pret = 0;
        int stock = 100;
        Date date = new Date();

        reader.beginObject();
        while(reader.hasNext()){
            String next = reader.nextName();
            if(next.equalsIgnoreCase("Price")){
                pret = (float) reader.nextDouble();
            }
            else if(next.equalsIgnoreCase("Stock")){
                stock = reader.nextInt();
            }
            else if(next.equalsIgnoreCase("PriceStockEnterDate")){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = format.parse(reader.nextString());
            }
            else
                reader.skipValue();
        }
        reader.endObject();

        return new PriceStat(pret, stock, date);
    }

    private static Produs readProdus(JsonReader reader) throws IOException {
        int prodId = defaultProdusId;
        String prodName = null;
        String desc = null;

        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();
            if(name.equalsIgnoreCase(PRODUCT_NAME_TAG)){
                prodName = reader.nextString();
            }
            else if(name.equalsIgnoreCase(PROD_DESC_TAG)){
                desc = reader.nextString();
            }
            else if(name.equalsIgnoreCase(PROD_ID_TAG)){
                prodId = reader.nextInt();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Produs(prodId, prodName, desc);
    }

    private static JsonReader getFromRemote(Pair<String, String> ...pairs){
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        StringBuilder params = new StringBuilder();
        for (Pair<String, String> p : pairs) {
            if(params.length() > 0)
                params.append("&");
            params.append(p.first).append("=").append(p.second);
        }

        try
        {
            url = new URL(REMOTE_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(params.toString());
            request.flush();
            request.close();
            return new JsonReader(new InputStreamReader(connection.getInputStream()));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static class FetchFromRemote extends AsyncTask<Pair<String, String>, Void, JsonReader>{

        @Override
        @SafeVarargs
        protected final JsonReader doInBackground(Pair<String, String>... nameValuePairs) {
            return getFromRemote(nameValuePairs);
        }
    }
}
