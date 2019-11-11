package com.main.foodimap.Controller;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant {

    private JSONObject json;

    public String id;
    public String name;
    public double latitude;
    public double longitude;

    public int reviewCount;
    public Marker pointMarker;
    public Marker textMarker;
//    public float rating;


    public Restaurant(JSONObject json) {
        this.json = json;
        runSetters();
    }

    private void runSetters() {
        setId();
        setName();
        setLatLong();
        setReviewCount();
    }

    private void setName() {
        try {
            this.name = json.getString("name");
        } catch (JSONException e) {
            this.name = "";
        }
    }

    private void setId() {
        try {
            this.id = json.getString("_id");
        } catch (JSONException e) {
            this.id = "";
        }
    }

    private void setLatitude(JSONObject coordinates) {
        try {
            this.latitude = coordinates.getDouble("latitude");
        } catch (JSONException e) {
            this.latitude = 0;
        }
    }

    private void setLatLong() {
        try {
            JSONObject coordinates = json.getJSONObject("coordinates");
            setLatitude(coordinates);
            setLongitude(coordinates);
        } catch (JSONException e) {

        }
    }

    private void setLongitude(JSONObject coordinates) {
        try {
            this.longitude = coordinates.getDouble("longitude");
        } catch (JSONException e) {
            this.longitude = 0;
        }
    }

    private void setReviewCount() {
        try {
            this.reviewCount = json.getInt("review_count");
        } catch (JSONException e) {
            this.reviewCount = 0;
        }
    }

    public void clearMarkers() {
        if (pointMarker != null) {
            pointMarker.remove();
            pointMarker = null;
        }

        if (textMarker != null) {
            textMarker.remove();
            textMarker = null;
        }
    }


    @NonNull
    @Override
    public String toString() {
        return String.format("{name: %s, id: %s, lat: %s, long: %s, reviews: %s}", name, id, latitude, longitude, reviewCount);
    }
}
