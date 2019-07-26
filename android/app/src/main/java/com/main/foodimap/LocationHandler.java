package com.main.foodimap;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Observable;
import java.util.Observer;


public class LocationHandler extends Observable {

    private Activity activity;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;

    public LocationHandler(Activity activity, FusedLocationProviderClient fusedLocationProviderClient) {
        if (activity != null) {
            this.activity = activity;
            System.out.println("hello world");
//            setupManagerListener();
            if (fusedLocationProviderClient != null) {
                this.fusedLocationProviderClient = fusedLocationProviderClient;
                setupFusedLocationProvider();
            }
        }
    }

    private void setupFusedLocationProvider() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
                lastLocation = locationResult.getLastLocation();
//                System.out.println(lastLocation);
                setChanged();
                notifyObservers(lastLocation);
            }
        };

//        setChangedLocation();
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e){
            System.out.println("fusedlocationhandler exception " + e);
        }
    }

    private void setupManagerListener() {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                System.out.println(location);
//                setChanged();
//                notifyObservers(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                System.out.println("Status Changed " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                System.out.println("Provider Enabled " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                System.out.println("Provider Disabled " + s);
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
            notifyObservers(lastLocation);
//            notifyObservers(locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true)));
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
