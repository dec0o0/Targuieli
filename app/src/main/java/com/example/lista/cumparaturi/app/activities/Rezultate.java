package com.example.lista.cumparaturi.app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lista.cumparaturi.R;

import static com.example.lista.cumparaturi.app.Utils.PREF_EXTRA;

public class Rezultate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezutate);

        String search = getIntent().getStringExtra(PREF_EXTRA);
    }


}
