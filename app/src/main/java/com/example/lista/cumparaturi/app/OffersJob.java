package com.example.lista.cumparaturi.app;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.internals.EventManager;
import com.example.lista.cumparaturi.app.stats.StatsManager;

import java.util.concurrent.ExecutionException;

/**
 * Created by macbookproritena on 11/26/16.
 */

public class OffersJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (Preferinta p : ContainerDate.instance().getPreferinte()) {
                    StatsManager.instance().addProdInfo(p.getProdus(),
                            APIUtils.getPreturiProdus(p.getProdus()));
                }
                return null;
            }
        };

        try {
            asyncTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        EventManager.manager().triggerPreturiRefresh();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
