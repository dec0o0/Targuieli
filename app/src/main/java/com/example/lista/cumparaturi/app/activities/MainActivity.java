package com.example.lista.cumparaturi.app.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.APIUtils;
import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.OffersJob;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.internals.EventManager;
import com.example.lista.cumparaturi.app.internals.IPreferintaEventHandler;
import com.example.lista.cumparaturi.app.internals.IPreturiRefreshedEventHandler;
import com.example.lista.cumparaturi.app.internals.ListaPreferintaRecyclerAdapter;
import com.example.lista.cumparaturi.app.stats.ProdInfo;
import com.example.lista.cumparaturi.app.stats.StatsManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class MainActivity extends ActionBarActivity implements IPreferintaEventHandler, IPreturiRefreshedEventHandler {

    FloatingActionsMenu menu;
    RecyclerView recyclerView;
    RecyclerView.Adapter recycleAdapter;
    RecyclerView.LayoutManager recycleManager;
    int jobId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        To set the ActionBar color for API<21
        For API 21, see values-v21/styles.xml
         */
        ActionBar ab=getSupportActionBar();
        Resources r=getResources();
        Drawable d=r.getDrawable(R.color.royalBlue);
        ab.setBackgroundDrawable(d);

        menu = (FloatingActionsMenu)findViewById(R.id.floatingMenu);
        FloatingActionButton adaugaBtn = (FloatingActionButton)findViewById(R.id.floating_add_btn);
        FloatingActionButton time = (FloatingActionButton)findViewById(R.id.sortTime);
        FloatingActionButton time2 = (FloatingActionButton)findViewById(R.id.sortTime2);

        adaugaBtn.setOnClickListener(deployActivity(menu, AdaugaProdusNou.class));

        EventManager.manager().addListener(this);
        EventManager.manager().addRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.lista_preferinte);
        recycleManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recycleManager);
        recycleAdapter = new ListaPreferintaRecyclerAdapter(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(recycleAdapter);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(ContainerDate.instance().getPreferinte(), new Comparator<Preferinta>() {
                    @Override
                    public int compare(Preferinta preferinta, Preferinta t1) {
                        return preferinta.getProdus().getName().compareTo(t1.getProdus().getName());
                    }
                });
                recycleAdapter.notifyDataSetChanged();
            }
        });

        time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(ContainerDate.instance().getPreferinte(), new Comparator<Preferinta>() {
                    @Override
                    public int compare(Preferinta preferinta, Preferinta t1) {
                        return Integer.compare(preferinta.getUrgente().getMidValue(), t1.getUrgente().getMidValue());
                    }
                });
                recycleAdapter.notifyDataSetChanged();
            }
        });

        startOffersJob();
    }

    private void startOffersJob(){
        ComponentName componentName = new ComponentName(this, OffersJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName);
        builder
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresDeviceIdle(false)
                .setPeriodic(10000)
                .setRequiresCharging(false);

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(builder.build());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new AsyncDataLoad(null).execute();
    }

    private <T> View.OnClickListener deployActivity(final FloatingActionsMenu fmenu,
                                                    final Class<T> tClass){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fmenu.collapse();
                startActivity(new Intent(MainActivity.this, tClass));
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Menu handling
        if(id == R.id.settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        else if(id == R.id.refresh){
            refreshAllTrends();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void preferintaNouaAdaugata(Preferinta p) {
        recycleAdapter.notifyItemInserted(ContainerDate.instance().getPreferinte().size() - 1);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ContainerDate.instance().saveAllToDB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(jobId != Integer.MAX_VALUE)
        ((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE)).cancel(jobId);
    }

    @Override
    public void onRefresh() {
        recycleAdapter.notifyDataSetChanged();
        StatsManager.instance().checkForOffers(this);
        StatsManager.instance().savePricesToCache(this);
    }

    private class AsyncDataLoad extends AsyncTask<Comparator<Preferinta>, String, Void>{
        private ProgressDialog pDialog;
        private RecyclerView.Adapter adapter;

        public AsyncDataLoad(@Nullable RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching data from local storage ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Comparator<Preferinta>... voids) {
            ContainerDate.instance().loadAllFromDb(MainActivity.this);
            ContainerDate.instance().loadDummyData();
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            pDialog.dismiss();
            if(adapter!=null) adapter.notifyDataSetChanged();
        }
    }

    private void refreshAllTrends(){
        for(Preferinta p :ContainerDate.instance().getPreferinte())
            new GetPriceAsync().execute(p);
    }

    private void notifyAdapter(final RecyclerView.Adapter adapter, final int index){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(index);
            }
        });
    }

    private class GetPriceAsync extends AsyncTask<Preferinta, Void, Void>{

        List<Preferinta> prefs;
        List<ProdInfo> infos;

        @Override
        protected Void doInBackground(Preferinta... voids) {
            prefs = Arrays.asList(voids);
            infos = new LinkedList<>();

            for (Preferinta p : prefs)
                infos.add(APIUtils.getPreturiProdus(p.getProdus()));

            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            if(prefs.size() != infos.size()) return;

            for(int i = 0; i < prefs.size(); ++i){
                StatsManager.instance().addProdInfo(prefs.get(i).getProdus(), infos.get(i));

                if(ContainerDate.instance().getPreferinte().contains(prefs.get(i)))
                    notifyAdapter(recycleAdapter, ContainerDate.instance().getPreferinte().indexOf(prefs.get(i)));
            }

            StatsManager.instance().checkForOffers(MainActivity.this);
        }
    }
}
