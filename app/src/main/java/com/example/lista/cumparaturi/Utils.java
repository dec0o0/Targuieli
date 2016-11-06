package com.example.lista.cumparaturi;

import android.util.JsonReader;
import android.util.Log;

import com.example.lista.cumparaturi.beans.Produs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class Utils {
    private Utils(){;}

    public static final String preventNullString = "";
    public static final int defaultProdusId = -1;

    private static final String REMOTE_API_URL = "http://proiectsoftwareinechipa.16mb.com/api/index.php";
    private static final String ACTION_TAG = "action";
    private static final String ACTION_VAL = "GetProductListByName";
    private static final String PRODUCT_NAME_TAG = "ProductName";
    private static final String PROD_DESC_TAG = "ProductDescription";
    private static final String PROD_ID_TAG = "ProductId";

    public static List<Produs> getRemoteProducts(String prodName) throws IOException {
        List<NameValuePair> header = new LinkedList<>();
        header.add(new BasicNameValuePair(ACTION_TAG, ACTION_VAL));
        header.add(new BasicNameValuePair(PRODUCT_NAME_TAG, prodName));
        JsonReader reader = getRemoteJson(header);

        List<Produs> produses = new LinkedList<>();
        reader.beginArray();
        while(reader.hasNext()){
            produses.add(readProdus(reader));
        }
        reader.endArray();
        reader.close();

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

    private static JsonReader getRemoteJson(List<NameValuePair> header){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(REMOTE_API_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(header));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            return new JsonReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
