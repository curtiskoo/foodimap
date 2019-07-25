package com.example.maptrial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;

public class MapsTabFragment extends Fragment
        implements OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener, Observer {

    private GoogleMap mMap;
    private Boolean locationEnabled = false;
    private Location lastKnown;
    public Context context;

    public MapsTabFragment(Location lastKnown) {
        if (lastKnown != null) {
            this.lastKnown = lastKnown;
        }
    }

    public MapsTabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_maps, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("map ready");
        System.out.println(googleMap);
        System.out.println("context: " + context + getActivity());
        mMap = googleMap;

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style_json)));

        if (!success) {
            System.out.println("Failed");
        }

        LatLng place = new LatLng(49.223480, -123.080777);

        mMap.addMarker(new MarkerOptions().position(place).title("Marker in Place"));

        if (lastKnown != null) {
            System.out.println(1);
            forceOnSetMyLocation(lastKnown);
            place = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
            update(new LocationHandler(null), lastKnown);
        } else {
            System.out.println(2);
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


    private void updateLocationUI(Location location) {
        System.out.println("here in new updatelocationui");
        if (mMap == null) {
            return;
        }
        if (location != null) {
            if (!locationEnabled) {
                LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));
                return;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void forceOnSetMyLocation(Location location) {
        try {
            System.out.println(1);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            if (location != null) {
                updateLocationUI(location);
            }
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
        }
    }

    public void setLastKnown(Location lastKnown) {
        this.locationEnabled = false;
        this.lastKnown = lastKnown;
        updateLocationUI(lastKnown);
        this.locationEnabled = true;
    }
}
