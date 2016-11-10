package com.example.lista.cumparaturi.app;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Pair;

import com.example.lista.cumparaturi.app.beans.Produs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class Utils {
    private Utils(){;}

    public static final String preventNullString = "";
    public static final int defaultProdusId = -1;
    public static final String PREF_EXTRA = "preferinta-input";

    private static final String REMOTE_API_URL = "http://proiectsoftwareinechipa.16mb.com/api/index.php";
    private static final String ACTION_TAG = "action";
    private static final String ACTION_VAL = "GetProductListByName";
    private static final String PRODUCT_NAME_TAG = "ProductName";
    private static final String PROD_DESC_TAG = "ProductDescription";
    private static final String PROD_ID_TAG = "ProductId";

    public static List<Produs> getRemoteProducts(String prodName){

        List<Produs> produses = new LinkedList<>();

        JsonReader reader = null;
        try {
            reader = getFromRemote(
                    new Pair<String, String>(ACTION_TAG, ACTION_VAL),
                    new Pair<String, String>(PRODUCT_NAME_TAG, prodName));

            //reader = new FetchFromRemote().execute().get();

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
