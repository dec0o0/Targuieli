package com.example.lista.cumparaturi.app.stats;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by macbookproritena on 11/21/16.
 */

public class Locatie {
    private final List<PriceStat> stats;
    private final String adresa, compName;
    public final int MAX_DATA = 20;

    public Locatie(String adresa, String compName) {
        this.stats = new ArrayList<>();
        this.adresa = adresa;
        this.compName = compName;
    }

    public List<PriceStat> getStats() {
        return stats;
    }

    public List<PriceStat> getSortedStats(){
        Collections.sort(stats, new Comparator<PriceStat>() {
            @Override
            public int compare(PriceStat priceStat, PriceStat t1) {
                return t1.getData().compareTo(priceStat.getData());
            }
        });
        return stats.size() > MAX_DATA ? stats.subList(0, MAX_DATA) : stats;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getCompName() {
        return compName;
    }

    public double getSlope(){
        return getRegression().getSlope();
    }

    public SimpleRegression getRegression(){
        List<PriceStat> stats = getSortedStats();

        SimpleRegression regression = new SimpleRegression(true);
        ListIterator<PriceStat> iterator = stats.listIterator(stats.size());
        for(int i = 1; iterator.hasPrevious(); ++i){
            regression.addData(i, iterator.previous().getPrice());
        }

        return regression;
    }

    public void addPriceStat(PriceStat p){
        this.stats.add(p);
    }
}
