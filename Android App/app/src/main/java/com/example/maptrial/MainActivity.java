package com.example.maptrial;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

public class MainActivity extends FragmentActivity implements OnRequestPermissionsResultCallback{

    private ViewPager pager;
    private TabAdapter adapter;
    private LocationHandler locationHandler;
    private MapsTabFragment mapsTabFragment;
    private ListTabFragment listTabFragment;
    private LocationManager locationManager;
    private Location lastKnown;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            lastKnown = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
            System.out.println(lastKnown);
            mapsTabFragment = new MapsTabFragment(lastKnown);
        } catch (SecurityException e) {
            lastKnown = null;
        } catch (IllegalArgumentException e) {
            lastKnown = null;
        } finally {
            if (mapsTabFragment == null) {
                System.out.println("mapstabfragment null");
                mapsTabFragment = new MapsTabFragment();
            }
        }

        listTabFragment = new ListTabFragment();
        getLocationPermission();

        // Setup Tabs
        pager = findViewById(R.id.pager);
        adapter = new TabAdapter(getSupportFragmentManager());
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        adapter.addFragment(mapsTabFragment, "Map");
        adapter.addFragment(listTabFragment, "List");
        pager.setAdapter(adapter);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationHandler = new LocationHandler(this);
            setupLocationHandlerObservers();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        System.out.println(requestCode);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        doRestart();
                        System.out.println("permission granted in MainActivity");
                        mapsTabFragment = new MapsTabFragment(lastKnown);
                        locationHandler = new LocationHandler(this);
                        setupLocationHandlerObservers();
                    } catch (SecurityException e) {
                        Log.e("Exception: %s", e.getMessage());
                    }
                }
            }
        }
    }

    private void doRestart() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finishAffinity();
    }

    private void setupLocationHandlerObservers() {
        System.out.println(locationHandler);
        System.out.println(mapsTabFragment);
        locationHandler.addObserver(mapsTabFragment);
    }

}
