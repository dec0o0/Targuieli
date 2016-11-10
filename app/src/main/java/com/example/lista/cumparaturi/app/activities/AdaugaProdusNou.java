package com.example.lista.cumparaturi.app.activities;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.Utils;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Produs;
import com.example.lista.cumparaturi.app.beans.Urgente;
import com.github.channguyen.rsv.RangeSliderView;

import java.util.ArrayList;
import java.util.List;

public class AdaugaProdusNou extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    AutoCompleteTextView autoCompleteTextView;
    RangeSliderView distantaSlider, urgentaSlider;
    TextView distantaTextView, urgentaTextView;
    int distanta, urgentaValInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.distanta = 0;
        this.urgentaValInt = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga);

        ActionBar ab=getSupportActionBar();
        Resources r=getResources();
        Drawable d=r.getDrawable(R.color.royalBlue);
        ab.setBackgroundDrawable(d);

        distantaSlider = (RangeSliderView)findViewById(R.id.distanta_input_val);
        urgentaSlider = (RangeSliderView) findViewById(R.id.urgency_input);
        distantaTextView = (TextView) findViewById(R.id.distanta_textView);
        urgentaTextView = (TextView) findViewById(R.id.urgenta_textview);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        adapter = new ArrayAdapter<String>(this, R.layout.single_autocomplete_item, R.id.single_item_text_view_autocomplete);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);


        autoCompleteTextView.addTextChangedListener(getTextWatcher());

        distantaSlider.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                distanta = index * 5;
                distantaTextView.setText(
                        createNiceTextViewTag(
                                getResources().getString(R.string.prag_distanta_tag),
                                distanta + " km"));
            }
        });

        urgentaSlider.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                urgentaValInt = index * 10;
                Urgente urgenta = Urgente.getByUrgencyValue(urgentaValInt);
                urgentaTextView.setText(
                        createNiceTextViewTag(
                                getResources().getString(R.string.urgenta_tag),
                                urgenta.getTextValue()));
                urgentaSlider.setFilledColor(getResources().getColor(urgenta.getColor()));
            }
        });

        autoCompleteTextView.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence charSequence) {
                return charSequence != null && charSequence.length() > 2;
            }

            @Override
            public CharSequence fixText(CharSequence charSequence) {
                return null;
            }
        });

        findViewById(R.id.arata_rezultatele_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }


    private void save(){
        // VALIDATE PROD

        autoCompleteTextView.performValidation();

        if(autoCompleteTextView.getText().length() < 2){
            Toast.makeText(this, "Formularul este incomplet ...", Toast.LENGTH_SHORT);
            return;
        }

        Preferinta p = new Preferinta(new Produs(autoCompleteTextView.getText().toString()), distanta, Urgente.getByUrgencyValue(urgentaValInt));
        ContainerDate.instance().addPreferinta(p);
        Toast.makeText(this, "Ai adaugat un produs nou", Toast.LENGTH_SHORT).show();
        this.onBackPressed();
    }

    private String createNiceTextViewTag(String textViewText, String newText){
        return textViewText + " - " + newText;
    }


    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( charSequence.length() < 2) return;
                if(previous != null &&
                        previous.getStatus() == AsyncTask.Status.RUNNING)
                    previous.cancel(true);
                previous = new SuggestionsAsync();
                previous.execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    private final Object lockInstance = new Object();
    private void notifyAdapter(final List<String> list){
        synchronized (lockInstance){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private SuggestionsAsync previous = null;
    private class SuggestionsAsync extends AsyncTask<String, Void, Void>{
        private volatile boolean running = true;

        @Override
        protected Void doInBackground(String... arrayAdapters) {
            if(running && arrayAdapters.length > 0){
                List<String> prodNames = new ArrayList<String>();
                List<Produs> produses = Utils.getRemoteProducts(arrayAdapters[0]);
                for(Produs p : produses) prodNames.add(p.getName());
                if(running)
                    notifyAdapter(prodNames);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            running = false;
        }
    }
}
