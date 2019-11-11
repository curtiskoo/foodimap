package com.main.foodimap.Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.main.foodimap.R;

import java.util.ArrayList;

public class MapMarkersHandler {
    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);

    private GoogleMap gmap;
    private Context context;
    private IconGenerator iconGenerator;

    public MapMarkersHandler(GoogleMap gmap, Context context) {
        this.gmap = gmap;
        this.context = context;
        this.setIconGenerator();
        this.setUpTest();
    }

    private void setUpTest() {
        LatLng place = new LatLng(49.223480, -123.080777);
        Bitmap iconBitmap = iconGenerator.makeIcon("This is a long text test");
        gmap.addMarker(new MarkerOptions().position(place).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).anchor(0f, 0.5f));
        gmap.addMarker(new MarkerOptions().position(place).title("Marker in Place"));
    }

    private void setIconGenerator() {
        iconGenerator = new IconGenerator(context);
        iconGenerator.setBackground(TRANSPARENT_DRAWABLE);
        iconGenerator.setRotation(90);
        iconGenerator.setContentPadding(30,0, 0, 100);
        iconGenerator.setContentRotation(-90);
        iconGenerator.setTextAppearance(R.style.iconGenText);
    }

    public void addMarkers(ArrayList<Restaurant> restaurants) {
        for (int i=0; i < restaurants.size(); i++) {
            setRestaraurantMarkers( restaurants.get(i) );
        }
    }

    private void setRestaraurantMarkers(Restaurant restaurant) {
        LatLng geo = new LatLng(restaurant.latitude, restaurant.longitude);
        restaurant.pointMarker = gmap.addMarker(new MarkerOptions().position(geo));
//        if (restaurant.reviewCount > 10) {
            Bitmap iconBitmap = iconGenerator.makeIcon(restaurant.name);
            restaurant.textMarker = gmap.addMarker(
                    new MarkerOptions()
                            .position(geo)
                            .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                            .anchor(0f, 0.5f));
//        }

    }


}
