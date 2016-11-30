package com.example.lista.cumparaturi.app.activities;

import android.app.NotificationManager;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
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

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(preferinta.getProdus().getName(), 0);
        }

        chart = (LineChart) findViewById(R.id.line_chart);
        locatii = new ArrayList<>(locatieSets.size());
        locatii.addAll(locatieSets.keySet());
        spinner.setItems(locatii);
        spinner.setSelectAll(true);
        addToChart(locatii);

        chart.setNoDataText("No data available");
        chart.getDescription().setText("Evolutia preturilor in timp");

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getLegend().setTextSize(14f);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chart.getLegend().setXEntrySpace(1f);
        chart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        chart.setHighlightPerDragEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        //chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

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
        ValueFormater valueFormater = new ValueFormater();

        chart.getAxisLeft().setValueFormatter(valueFormater);
        data.setValueTextSize(11);
        data.setValueFormatter(valueFormater);

        chart.setData(data);
        chart.animateX(1000);
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
            List<PriceStat> stats = new ArrayList<>(l.getSortedStats()).subList(0, min);
            Collections.reverse(stats);

            for(PriceStat price : stats){
                long convert = TimeUnit.DAYS.convert(now.getTime() - price.getData().getTime(), TimeUnit.MILLISECONDS);
                entries.add(new Entry(++x, price.getPrice()));
            }
            LineDataSet dataSet = new LineDataSet(entries, l.getCompName());
            dataSet.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
            dataSet.setCircleColor(dataSet.getColor());
            dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSet.setCircleRadius(6);

            locatieSets.put(l.getAdresa(), dataSet);
        }
    }

    private static class ValueFormater extends LargeValueFormatter{
        private final String suffix = " Lei";

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return super.getFormattedValue(value, entry, dataSetIndex, viewPortHandler) + suffix;
        }

        // IAxisValueFormatter
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return super.getFormattedValue(value, axis) + suffix;
        }
    }

}
