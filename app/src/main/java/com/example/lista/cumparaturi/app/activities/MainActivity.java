package com.example.lista.cumparaturi.app.activities;

import android.app.ProgressDialog;
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

import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.IPreferintaEventHandler;
import com.example.lista.cumparaturi.app.PreferinteEventManger;
import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.RecyclerAdapter;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Comparator;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class MainActivity extends ActionBarActivity implements IPreferintaEventHandler {

    FloatingActionsMenu menu;
    RecyclerView recyclerView;
    RecyclerView.Adapter recycleAdapter;
    RecyclerView.LayoutManager recycleManager;

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

        PreferinteEventManger.manager().addListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.lista_preferinte);
        recycleManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recycleManager);
        recycleAdapter = new RecyclerAdapter(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(recycleAdapter);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncDataLoad(recycleAdapter).execute(new Comparator<Preferinta>() {
                    @Override
                    public int compare(Preferinta preferinta, Preferinta t1) {
                        return preferinta.getProdus().getName().compareTo(t1.getProdus().getName());
                    }
                });
            }
        });

        time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncDataLoad(recycleAdapter).execute(new Comparator<Preferinta>() {
                    @Override
                    public int compare(Preferinta preferinta, Preferinta t1) {
                        return preferinta.getUrgente().compareTo(t1.getUrgente());
                    }
                });
            }
        });
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void preferintaNouaAdaugata(Preferinta p) {
        recycleAdapter.notifyItemInserted(ContainerDate.instance().getPreferinte().size() - 1);
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
            if(adapter!=null)
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ContainerDate.instance().saveAllToDB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
