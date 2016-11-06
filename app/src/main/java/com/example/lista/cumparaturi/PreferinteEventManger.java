package com.example.lista.cumparaturi;

import com.example.lista.cumparaturi.beans.Preferinta;
import com.example.lista.cumparaturi.beans.Produs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class PreferinteEventManger {
    private static PreferinteEventManger instance;
    Set<IPreferintaEventHandler> listeners;

    private PreferinteEventManger(){
        this.listeners = new HashSet<>();
    }

    public static PreferinteEventManger manager(){
        if(instance == null){
            instance = new PreferinteEventManger();
        }
        return instance;
    }

    public void addListener(IPreferintaEventHandler listener){
        this.listeners.add(listener);
    }

    public void triggerAddedEvent(Preferinta p){
        for (IPreferintaEventHandler listener : listeners){
            listener.preferintaNouaAdaugata(p);
        }
    }
}
