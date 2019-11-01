package com.main.foodimap.Services;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.CameraPosition;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class DataService extends Observable {
    private RequestQueue queue;
    private double lat;
    private double lon;

    public DataService(RequestQueue queue) {
        this.queue = queue;
    }

    public void searchButtonClick(CameraPosition c) {
        this.lat = c.target.latitude;
        this.lon = c.target.longitude;
        geoQueryPostRequest(this.lat, this.lon, 500);
        setChanged();
        notifyObservers(c.target);
    }

    public Object[] geoQueryPostRequest(double lat, double lon, double distance) {
//        double lat = c.target.latitude;
//        double lon = c.target.longitude;

        String url = "https://foodimap-api.herokuapp.com/geoQuery";
        Map<String, Number> params = new HashMap();
        params.put("latitude", lat);
        params.put("longitude", lon);
        params.put("distance", distance);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                System.out.println(response);
                setChanged();
                notifyObservers(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
            }
        });

        queue.add(jsonRequest);

        return null;
    }
}
