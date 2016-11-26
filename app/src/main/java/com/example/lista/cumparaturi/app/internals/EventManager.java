package com.example.lista.cumparaturi.app.internals;

import com.example.lista.cumparaturi.app.beans.Preferinta;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class EventManager {
    private static EventManager instance;
    Set<IPreferintaEventHandler> listeners;
    Set<IPreturiRefreshedEventHandler> refreshListeners;

    private EventManager(){
        this.listeners = new HashSet<>();
        this.refreshListeners = new HashSet<>();
    }

    public static EventManager manager(){
        if(instance == null){
            instance = new EventManager();
        }
        return instance;
    }

    public void addListener(IPreferintaEventHandler listener){
        this.listeners.add(listener);
    }

    public void addRefreshListener(IPreturiRefreshedEventHandler lis){
        this.refreshListeners.add(lis);
    }

    public void triggerAddedEvent(Preferinta p){
        for (IPreferintaEventHandler listener : listeners){
            listener.preferintaNouaAdaugata(p);
        }
    }

    public void triggerPreturiRefresh(){
        for(IPreturiRefreshedEventHandler e: refreshListeners)
            e.onRefresh();
    }
}
