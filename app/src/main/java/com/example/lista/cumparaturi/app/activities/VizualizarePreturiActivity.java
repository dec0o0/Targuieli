package com.example.lista.cumparaturi.app.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.stats.Locatie;
import com.example.lista.cumparaturi.app.stats.PriceStat;
import com.example.lista.cumparaturi.app.stats.ProdInfo;
import com.example.lista.cumparaturi.app.stats.StatsManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

public class VizualizarePreturiActivity extends AppCompatActivity {

    Preferinta preferinta;
    Map<String, LineDataSet> locatieSets = new HashMap<>();
    List<String> locatii;
    LineChart chart;
    MultiSelectSpinner spinner;
    TextView prodName, descView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezutate);

        ActionBar ab=getSupportActionBar();
        Resources r=getResources();
        Drawable d=r.getDrawable(R.color.royalBlue);
        ab.setBackgroundDrawable(d);

        prodName = (TextView) findViewById(R.id.rez_produs_text);
        descView = (TextView) findViewById(R.id.rez_desc_text);
        spinner = (MultiSelectSpinner) findViewById(R.id.graph_spinner);

        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.containsKey(AdaugaProdusNou.PREFERINTA_EXTRA_TAG)){
            int idx = extras.getInt(AdaugaProdusNou.PREFERINTA_EXTRA_TAG);
            preferinta = ContainerDate.instance().getPreferinte().get(idx);
            populate(StatsManager.instance().getProdInfo(preferinta.getProdus()));

            prodName.setText(preferinta.getProdus().getName());
            descView.setText(preferinta.getProdus().getDesc());
        }

        chart = (LineChart) findViewById(R.id.line_chart);
        locatii = new ArrayList<>(locatieSets.size());
        locatii.addAll(locatieSets.keySet());
        spinner.setItems(locatii);
        spinner.setSelectAll(true);
        addToChart(locatii);
        chart.setNoDataText("No data available");
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getLegend().setTextSize(14f);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chart.getLegend().setXEntrySpace(5f);

        spinner.setAllCheckedText("Toate");
        spinner.setAllUncheckedText("Nici una");

        // CLICK LISTENERS

        spinner.setMinSelectedItems(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addToChart(spinner.getSelected());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void addToChart(List<String> locatii){
        LineData data = new LineData();
        for(String l : locatii){
            if(locatieSets.containsKey(l)) {
                data.addDataSet(locatieSets.get(l));
            }
        }
        data.setValueFormatter(new DateValueFormatter());
        chart.setData(data);
        chart.invalidate();
    }

    private void addToChart(boolean[] locatiiBools){
        List<String> ok = new ArrayList<>(locatiiBools.length);

        for(int i = 0; i < locatiiBools.length; ++i){
            if(locatiiBools[i]) ok.add(locatii.get(i));
        }

        addToChart(ok);
    }

    private void populate(ProdInfo info){
        if(info == null) return;
        Date now
                = new Date();

        int min = Integer.MAX_VALUE;
        for(Locatie l : info.getLocatieList()){
            int s = l.getStats().size();
            min = s < min ? s : min;
        }

        Random rnd = new Random();
        for(Locatie l : info.getLocatieList()){
            List<Entry> entries = new LinkedList<>();
            float x = 0;
            List<PriceStat> stats = l.getSortedStats().subList(0, min);
            for(PriceStat price : stats){
                long convert = TimeUnit.DAYS.convert(now.getTime() - price.getData().getTime(), TimeUnit.MILLISECONDS);
                entries.add(new Entry(++x, price.getPrice()));
            }
            LineDataSet dataSet = new LineDataSet(entries, l.getCompName());
            dataSet.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
            dataSet.setCircleColor(dataSet.getColor());
            dataSet.setMode(LineDataSet.Mode.STEPPED);
            locatieSets.put(l.getAdresa(), dataSet);
        }
    }

    private static class DateValueFormatter implements IValueFormatter{
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            Calendar nowc = Calendar.getInstance();
            nowc.add(Calendar.DATE, (-1) * Math.round(value));
            return (nowc.get(Calendar.MONTH) + 1) + "/"
                    + nowc.get(Calendar.DATE);
        }
    }

}
