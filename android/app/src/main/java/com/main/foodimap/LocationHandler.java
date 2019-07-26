package com.main.foodimap;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Observable;
import java.util.Observer;


public class LocationHandler extends Observable {

    private Activity activity;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationHandler(Activity activity) {
        if (activity != null) {
            this.activity = activity;
            System.out.println("hello world");
            setupManagerListener();
        }
    }

    private void setupManagerListener() {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                System.out.println(location);
                setChanged();
                notifyObservers(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        setChangedLocation();
        try {
            System.out.println("locationlistner: " + locationListener);
            System.out.println("setting change");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            System.out.println("Error in LocationHandler: " + e);
        }
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    private void setChangedLocation() {
        try {
            setChanged();
            notifyObservers(locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true)));
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        setChangedLocation();
    }

}
