package com.example.lista.cumparaturi.app.beans;

import android.support.annotation.NonNull;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class Preferinta {
    private final Produs produs;
    private final int maxDistanta;
    private final Urgente urgente;

    public Preferinta(@NonNull Produs produs, int maxDistanta, Urgente urgente) {
        this.produs = produs;
        this.maxDistanta = maxDistanta;
        this.urgente = urgente;
    }

    public Produs getProdus() {
        return produs;
    }

    public int getMaxDistanta() {
        return maxDistanta;
    }

    public Urgente getUrgente() {
        return urgente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Preferinta that = (Preferinta) o;

        if (maxDistanta != that.maxDistanta) return false;
        if (!produs.equals(that.produs)) return false;
        return urgente == that.urgente;

    }

    @Override
    public int hashCode() {
        int result = produs.hashCode();
        result = 31 * result + maxDistanta;
        result = 31 * result + urgente.hashCode();
        return result;
    }
}
