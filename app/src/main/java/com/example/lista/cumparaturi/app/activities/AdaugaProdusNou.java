package com.example.lista.cumparaturi.app.activities;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.APIUtils;
import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Produs;
import com.example.lista.cumparaturi.app.beans.Urgente;
import com.github.channguyen.rsv.RangeSliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdaugaProdusNou extends AppCompatActivity {
    public static final String PREFERINTA_EXTRA_TAG = "preferinta_ttag";
    private static final int DISTANTA_MULTIPLIER = 5;
    private static final int URGENCY_MULTIPLIER = 10;

    ArrayAdapter<String> adapter;
    AutoCompleteTextView autoCompleteTextView;
    RangeSliderView distantaSlider, urgentaSlider;
    TextView distantaTextView, urgentaTextView;
    int distanta, urgentaValInt;
    EditText descriere;
    Produs produs = null;

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

        descriere = (EditText) findViewById(R.id.descriere_input);
        distantaSlider = (RangeSliderView)findViewById(R.id.distanta_input_val);
        urgentaSlider = (RangeSliderView) findViewById(R.id.urgency_input);
        distantaTextView = (TextView) findViewById(R.id.distanta_textView);
        urgentaTextView = (TextView) findViewById(R.id.urgenta_textview);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        adapter = new ArrayAdapter<String>(this, R.layout.single_autocomplete_item, R.id.single_item_text_view_autocomplete);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                produs = suggestedProduses.get(i);
                descriere.setText(produs.getDesc());
            }
        });

        autoCompleteTextView.addTextChangedListener(getTextWatcher());

        distantaSlider.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                distanta = index * DISTANTA_MULTIPLIER;
                distantaTextView.setText(
                        createNiceTextViewTag(
                                getResources().getString(R.string.prag_distanta_tag),
                                distanta + " km"));
            }
        });

        urgentaSlider.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                urgentaValInt = index * URGENCY_MULTIPLIER;
                Urgente urgenta = Urgente.getByUrgencyValue(urgentaValInt);
                urgentaTextView.setText(
                        createNiceTextViewTag(
                                getResources().getString(R.string.urgenta_tag),
                                urgenta.getTextValue()));
                urgentaSlider.setFilledColor(getResources().getColor(urgenta.getColor()));
            }
        });

        findViewById(R.id.arata_rezultatele_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(PREFERINTA_EXTRA_TAG)){
            Preferinta p = ContainerDate.instance().getPreferinte().get(extras.getInt(PREFERINTA_EXTRA_TAG));
            autoCompleteTextView.setText(p.getProdus().getName());
            autoCompleteTextView.setEnabled(false);
            descriere.setText(p.getProdus().getDesc());
            descriere.setEnabled(false);
            distantaTextView.setText(createNiceTextViewTag(getResources().getString(R.string.prag_distanta_tag), p.getMaxDistanta() + " km"));
            distantaSlider.setInitialIndex(p.getMaxDistanta()/ DISTANTA_MULTIPLIER);
            urgentaTextView.setText(createNiceTextViewTag(getResources().getString(R.string.urgenta_tag), p.getUrgente().getTextValue()));
            urgentaSlider.setInitialIndex(p.getUrgente().getMin() / URGENCY_MULTIPLIER);
        }
    }

    private void save(){
        ProgressDialog dialog = new ProgressDialog(AdaugaProdusNou.this);
        dialog.setMessage("Validating...");
        dialog.show();

        autoCompleteTextView.performValidation();
        if(autoCompleteTextView.getText().length() < 2){
            autoCompleteTextView.setError("Numarul minim de caractere este 2");
            dialog.dismiss();
            Toast.makeText(this, "Formularul este incomplet ...", Toast.LENGTH_SHORT);
            return;
        }

        Produs result = null;
        final String text = autoCompleteTextView.getText().toString();
        try {
            result = new AsyncTask<Void, Void, Produs>() {
                @Override
                protected Produs doInBackground(Void... voids) {
                    return APIUtils.getRemoteProducts(text).get(0);
                }
            }.get();
        } catch (InterruptedException | ExecutionException e) {
            dialog.dismiss();
            Toast.makeText(this, "Validarea nu a putut fi realizata cu succes, va rugam reincercati.", Toast.LENGTH_SHORT);
            return;
        }

        if(result == null){
            dialog.dismiss();
            Toast.makeText(this, "Produsul indicat nu a fost gasit in baza de date.", Toast.LENGTH_SHORT);
            return;
        }

        Preferinta p = new Preferinta(result, distanta, Urgente.getByUrgencyValue(urgentaValInt));
        ContainerDate.instance().addPreferinta(p);

        dialog.dismiss();
        Toast.makeText(this, "Ai adaugat un produs nou", Toast.LENGTH_SHORT).show();

        this.finish();
        //this.onBackPressed();
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

    private volatile List<Produs> suggestedProduses;
    private final Object lockInstance = new Object();
    private void notifyAdapter(final List<Produs> produse, final List<String> list){
        synchronized (lockInstance){
            this.suggestedProduses = produse;
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
                List<Produs> produses = APIUtils.getRemoteProducts(arrayAdapters[0]);
                for(Produs p : produses) prodNames.add(p.getName());
                if(running) notifyAdapter(produses, prodNames);
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
