package com.main.foodimap.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.main.foodimap.Controller.MapMarkersHandler;
import com.main.foodimap.Controller.Restaurant;
import com.main.foodimap.Services.DataService;
import com.main.foodimap.Services.LocationHandler;
import com.main.foodimap.R;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MapsTabFragment extends Fragment
        implements OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener,
        Observer, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private Boolean locationEnabled = false;
    private Location lastKnown;
    public Context context;
    private Button searchBtn;
    private DataService dataService;
    private MapMarkersHandler mapMarkersHandler;
    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);


    public MapsTabFragment(Location lastKnown, DataService dataService) {
        this.dataService = dataService;

        if (lastKnown != null) {
            this.lastKnown = lastKnown;
        }
    }

    public MapsTabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_maps, container, false);

        // Search Area Button
        searchBtn = v.findViewById(R.id.searchBtn);
        setupSearchBtn();

        // Load Map onto this Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return v;
    }

    private void setupSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataService.searchButtonClick(mMap.getCameraPosition());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("map ready");
        System.out.println(googleMap);
        System.out.println("context: " + context + getActivity());
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setMinZoomPreference(15); // set this for zoom
        mMap.setMaxZoomPreference(15); // set this for zoom


        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style_json)));

        if (!success) {
            System.out.println("Failed");
        }

        mapMarkersHandler = new MapMarkersHandler(mMap, getContext()); // this sets up handler for new data updates


        if (lastKnown != null) {
            forceOnSetMyLocation(lastKnown);
            LatLng place = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
            update(new LocationHandler(null), lastKnown);
        } else {
            forceOnSetMyLocation(null);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
//        updateLocationUI();
        System.out.println(this);
        System.out.println(mMap);
        System.out.println(context);
        System.out.println(getActivity());
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }



    private void forceOnSetMyLocation(Location location) {
        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            locationEnabled = true;
        } catch (SecurityException e) {
            System.out.println(e);
            mMap.setMyLocationEnabled(false);
            locationEnabled = false;
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        System.out.println("update called");
        if (observable instanceof LocationHandler && mMap != null) {
            System.out.println(o);
            if (!locationEnabled) {
                forceOnSetMyLocation((Location) o);
            } else {
                forceOnSetMyLocation((Location) o);
            }
        } else if (observable instanceof DataService && mMap != null) {
            if (o instanceof ArrayList<?>) {
                mapMarkersHandler.addMarkers((ArrayList<Restaurant>) o);
            }
        }
    }

    public void setLastKnown(Location lastKnown) {
        this.locationEnabled = false;
        this.lastKnown = lastKnown;
        this.locationEnabled = true;
    }

    @Override
    public void onCameraIdle() {
        System.out.println("Camera Idle");

        searchBtn.setVisibility(View.VISIBLE);
    }
}
