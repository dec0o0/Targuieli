package com.example.lista.cumparaturi.app.stats;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Produs;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macbookproritena on 11/21/16.
 */

public class StatsManager {
    private static StatsManager manager = null;
    private static final Map<Produs, ProdInfo> produse = new HashMap<>();

    // TODO : fix data for chart

    private StatsManager(){}

    public static StatsManager instance(){
        if(manager == null) manager = new StatsManager();
        return manager;
    }

    public double getGeneralProdSlope(Produs p){
        if(!produse.containsKey(p)) return -1;
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

    public double bestPrice(Produs p){
        if(!produse.containsKey(p)) return 0;
        double min = Double.MAX_VALUE;
        for(Locatie l : produse.get(p).getLocatieList()){
            double curr = Collections.max(l.getStats(), new Comparator<PriceStat>() {
                @Override
                public int compare(PriceStat priceStat, PriceStat t1) {
                    return priceStat.getData().compareTo(t1.getData());
                }
            }).getPrice();

            min = curr < min ? curr : min;
        }
        return min;
    }

    public void checkForOffers(Context context){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager == null)
            return;

        for(ProdInfo p : produse.values()){
            Locatie best = null;
            float bestDisc = Float.MAX_VALUE, bestPrice = Float.MAX_VALUE;
            Preferinta pref = ContainerDate.instance().getPrefByProd(p.getProd());
            if(pref == null) continue;

            for(Locatie l : p.getLocatieList()){
                List<PriceStat> sortedStats = l.getSortedStats();
                if(sortedStats.size() < 2 || sortedStats.get(0).getStock() < 5) continue;

                float disc = calcDiscount(sortedStats.get(1).getPrice(), sortedStats.get(0).getPrice());
                double predict = l.getRegression().predict(sortedStats.size());

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
                createNotif(manager, context, p.getProd(), bestPrice, best.getCompName());
            }
        }
    }

    private float calcDiscount(float before, float after){
        if(before >= after) return Float.MAX_VALUE;
        float delta = after - before;
        return (100 * delta) / before;
    }

    private void createNotif(NotificationManager manager, Context c, Produs p, float pret, String locatie){
        manager.notify(1,
                new NotificationCompat.Builder(c)
                        .setGroup("Lista cumparaturi")
                        .setContentTitle("Oferta pentru " + p.getName())
                        .setContentText("Pret: " + pret)
                        .setContentInfo("Locatie: " + locatie).build());
    }
}
