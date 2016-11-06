package com.example.lista.cumparaturi.layouts;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lista.cumparaturi.ContainerDate;
import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.beans.Preferinta;
import com.example.lista.cumparaturi.beans.Produs;
import com.example.lista.cumparaturi.beans.Urgente;
import com.github.channguyen.rsv.RangeSliderView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

public class AdaugaProdusNou extends AppCompatActivity {

    MaterialEditText numeProdusEditText;
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

        numeProdusEditText = (MaterialEditText) findViewById(R.id.produs_name_input);
        distantaSlider = (RangeSliderView)findViewById(R.id.distanta_input_val);
        urgentaSlider = (RangeSliderView) findViewById(R.id.urgency_input);
        distantaTextView = (TextView) findViewById(R.id.distanta_textView);
        urgentaTextView = (TextView) findViewById(R.id.urgenta_textview);

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

        // TODO : remove && replace with search results

        ((Button)findViewById(R.id.adauga_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        numeProdusEditText.addValidator(new METValidator("Numele produsului e necompletat sau prea scurt (<5)") {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && text.length() > 5;
            }
        });
        numeProdusEditText.setValidateOnFocusLost(true);
    }

    private void save(){
        if(!numeProdusEditText.validate()){
            Toast.makeText(this, "Formularul este incomplet ...", Toast.LENGTH_SHORT);
            return;
        }

        Preferinta p = new Preferinta(new Produs(numeProdusEditText.getText().toString()), distanta, Urgente.getByUrgencyValue(urgentaValInt));
        ContainerDate.instance().addPreferinta(p);
        Toast.makeText(this, "Ai adaugat un produs nou", Toast.LENGTH_SHORT).show();
        this.onBackPressed();
    }

    private String createNiceTextViewTag(String textViewText, String newText){
        return textViewText + " - " + newText;
    }



}
