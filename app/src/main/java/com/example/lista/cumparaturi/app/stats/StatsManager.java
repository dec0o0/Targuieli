package com.example.lista.cumparaturi.app.stats;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.activities.AdaugaProdusNou;
import com.example.lista.cumparaturi.app.activities.MainActivity;
import com.example.lista.cumparaturi.app.activities.VizualizarePreturiActivity;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Produs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macbookproritena on 11/21/16.
 */

public class StatsManager {
    private static StatsManager manager = null;
    private static final Map<Produs, ProdInfo> produse = new HashMap<>();
    private static final Map<Produs, Double> lowestPrices = new HashMap<>();
    //private static final Map<Produs, Integer> notifIds = new HashMap<>();
    //private final AtomicInteger count = new AtomicInteger(0);
    private final String PREFS_NAME = "pricesPrefs";

    // TODO : fix data for chart

    private StatsManager(){}

    public static StatsManager instance(){
        if(manager == null) manager = new StatsManager();
        return manager;
    }

    public double getGeneralProdSlope(Produs p){
        if(!produse.containsKey(p)) return Double.MIN_VALUE;
        List<Locatie> locatieList = produse.get(p).getLocatieList();
        double min = Double.MAX_VALUE;
        for(Locatie l : locatieList){
            double slope = l.getSlope();
            min = slope < min ? slope : min;
        }
        return min;
    }

    public ProdInfo getProdInfo(Produs p){
        if(!produse.containsKey(p)) return null;
        return produse.get(p);
    }

    public void addProdInfo(Produs pp, ProdInfo p){
        if(produse.containsKey(pp))
            produse.remove(pp);
        produse.put(pp, p);
    }

    public double lowestPrice(Produs p, @Nullable Context context){
        if(!produse.containsKey(p)){
            if(context != null){
                SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, 0);
                float price = pref.getFloat(p.getName(), Float.MAX_VALUE);
                if(price != Float.MAX_VALUE)
                    return price;
            }
            return 0;
        }

        double min = Double.MAX_VALUE;
        for(Locatie l : produse.get(p).getLocatieList()){
            double curr = l.getSortedStats().get(0).getPrice();
            min = curr < min ? curr : min;
        }
        lowestPrices.put(p, min);
        return min;
    }

    public void checkForOffers(@NonNull Context context){

        for(ProdInfo p : produse.values()){
            Locatie best = null;
            float bestDisc = Float.MAX_VALUE, bestPrice = Float.MAX_VALUE;
            Preferinta pref = ContainerDate.instance().getPrefByProd(p.getProd());
            if(pref == null) continue;

            // Populate map lowest price
            lowestPrice(p.getProd(), context);

            for(Locatie l : p.getLocatieList()){
                List<PriceStat> sortedStats = l.getSortedStats();
                if(lowestPrices.get(p.getProd()).floatValue() < sortedStats.get(0).getPrice() ||
                        sortedStats.size() < 2 || sortedStats.get(0).getStock() < 5) continue;

                float disc = calcDiscount(sortedStats.get(1).getPrice(), sortedStats.get(0).getPrice());
                double predict = l.getRegression().predict(sortedStats.size() - 1);

                if(disc > pref.getUrgente().getPragDiscount() ||
                        calcDiscount((float)predict, sortedStats.get(0).getPrice()) >
                                pref.getUrgente().getPragDiscount()){
                    if(best != null){
                        best = l;
                        bestPrice = sortedStats.get(0).getPrice();
                    }
                    else if(bestPrice > sortedStats.get(0).getPrice()){
                        best = l;
                        bestPrice = sortedStats.get(0).getPrice();
                    }
                }
            }

            if(best != null){
                createNotif(context, p.getProd(), bestPrice, best.getCompName());
            }
        }
    }

    private float calcDiscount(float before, float after){
        if(before >= after) return Float.MAX_VALUE;
        float delta = after - before;
        return (100 * delta) / before;
    }

    public void createNotif(Context context, Produs p, float pret, String locatie){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager == null)
            return;

        Intent intent = new Intent(context, VizualizarePreturiActivity.class);
        intent.putExtra(AdaugaProdusNou.PREFERINTA_EXTRA_TAG, 0);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class).addNextIntent(intent);

        manager.notify(p.getName(), 0,
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_attach_money_white_24dp)
                        .addAction(R.drawable.ic_info_black_24dp,
                                "Vezi detalii",
                                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setGroup("Lista cumparaturi")
                        .setContentTitle("Oferta pentru " + p.getName())
                        .setContentText("Pret: " + pret)
                        .build());
    }

    public void savePricesToCache(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        for(Preferinta p : ContainerDate.instance().getPreferinte()){
            if(lowestPrices.containsKey(p.getProdus())){
                editor.putFloat(p.getProdus().getName(), lowestPrices.get(p.getProdus()).floatValue());
            }
        }
        editor.apply();
    }
}
