package com.example.lista.cumparaturi.layouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.example.lista.cumparaturi.ContainerDate;
import com.example.lista.cumparaturi.IPreferintaEventHandler;
import com.example.lista.cumparaturi.PreferinteEventManger;
import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.beans.Preferinta;
import com.example.lista.cumparaturi.beans.Produs;
import com.example.lista.cumparaturi.beans.Urgente;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class MainActivity extends ActionBarActivity implements IPreferintaEventHandler {

    FloatingActionsMenu menu;
    MaterialListView listaPreferinte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaPreferinte = (MaterialListView)findViewById(R.id.listaProduse);
        listaPreferinte.getLayoutManager().offsetChildrenVertical(30);
        menu = (FloatingActionsMenu)findViewById(R.id.floatingMenu);
        /*
        To set the ActionBar color for API<21
        For API 21, see values-v21/styles.xml
         */
        ActionBar ab=getSupportActionBar();
        Resources r=getResources();
        Drawable d=r.getDrawable(R.color.royalBlue);
        ab.setBackgroundDrawable(d);

        FloatingActionButton adaugaBtn = (FloatingActionButton)findViewById(R.id.floating_add_btn);
        FloatingActionButton time = (FloatingActionButton)findViewById(R.id.sortTime);
        FloatingActionButton time2 = (FloatingActionButton)findViewById(R.id.sortTime2);

        adaugaBtn.setOnClickListener(newListener(menu, AdaugaProdusNou.class));

        listaPreferinte.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int i) {
                //Toast.makeText(MainActivity.this,)
            }
        });

        PreferinteEventManger.manager().addListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new AsyncDataLoad().execute();
    }

    private <T> View.OnClickListener newListener(final FloatingActionsMenu fmenu,
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "To be implemented ...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void preferintaNouaAdaugata(Preferinta p) {
        listaPreferinte.add(newCard(p));
    }

    private OnButtonPressListener blankListener(){
        return new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {;}
        };
    }

    private Card newCard(Preferinta pref){
        BigImageButtonsCard card = new BigImageButtonsCard(MainActivity.this);
        card.setOnRightButtonPressedListener(blankListener());
        card.setOnLeftButtonPressedListener(blankListener());
        card.setTitle(pref.getProdus().getName());
        card.setDescription(pref.getProdus().getDesc());
        card.setRightButtonText("Vezi oferte");
        card.setLeftButtonText("Max " + pref.getMaxDistanta() + "km");
        card.setDividerVisible(true);

        card.setBackgroundColorRes(pref.getUrgente().getColor());
        card.setTitleColorRes(R.color.white);
        return card;
    }

    private class AsyncDataLoad extends AsyncTask<Void, String, Void>{
        private ProgressDialog pDialog;

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
        protected Void doInBackground(Void... voids) {
            ContainerDate.instance().loadAllFromDb(MainActivity.this);
            ContainerDate.instance().loadDummyData();
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            for (Preferinta pref : ContainerDate.instance().getPreferinte()){
                listaPreferinte.add(newCard(pref));
            }
            pDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ContainerDate.instance().saveAllToDB();
    }

    @Override
    protected void onDestroy() {
        ContainerDate.instance().closeDBConnection(true);
        super.onDestroy();
    }
}
