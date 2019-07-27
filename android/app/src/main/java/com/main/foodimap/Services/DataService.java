package com.main.foodimap.Services;

import com.google.android.gms.maps.model.CameraPosition;

import java.util.Observable;

public class DataService extends Observable {

    public DataService() {}

    public void searchButtonClick(CameraPosition c) {
        setChanged();
        notifyObservers(c.target);
    }
}
