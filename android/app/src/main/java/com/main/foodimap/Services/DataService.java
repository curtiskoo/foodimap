package com.main.foodimap.Services;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.CameraPosition;
import com.android.volley.toolbox.Volley;
import com.main.foodimap.Controller.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class DataService extends Observable {
    private RequestQueue queue;
    private double lat;
    private double lon;
    public ArrayList<Restaurant> restaurants;
//    private JSONParser parser = new JSONParser();

    public DataService(RequestQueue queue) {
        this.queue = queue;
    }

    public void searchButtonClick(CameraPosition c) {
        this.lat = c.target.latitude;
        this.lon = c.target.longitude;
        resetAllRestaurantsMarkers();
        geoQueryPostRequest(this.lat, this.lon, 500);
        setChanged();
        notifyObservers(c.target);
    }

    private void resetAllRestaurantsMarkers() {
        if (restaurants != null) {
            for (int i=0; i < restaurants.size(); i++) {
                restaurants.get(i).clearMarkers();
            }
        }
    }

    private void getRestaurantsFromPostRequest(JSONObject results) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        JSONArray resultsArray;
        try {
            resultsArray = results.getJSONArray("result");
        } catch (JSONException e) {
            resultsArray = new JSONArray();
        }
        for (int i=0; i<resultsArray.length(); i++) {
            try {
                restaurants.add(new Restaurant(resultsArray.getJSONObject(i)));
            } catch (JSONException e) {
                continue;
            }
        }
        System.out.println(restaurants);
        this.restaurants = restaurants;
    }

    private void geoQueryPostRequest(double lat, double lon, double distance) {

        String url = "https://foodimap-api.herokuapp.com/geoQuery";
        Map<String, Number> params = new HashMap();
        params.put("latitude", lat);
        params.put("longitude", lon);
        params.put("distance", distance);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, url,
                parameters, null
        , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
            }
        }) {
            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
                getRestaurantsFromPostRequest(response);
                setChanged();
                notifyObservers(restaurants);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(
                            response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    System.out.println("HERE\n");
                    System.out.println(json);
                    return Response.success(new JSONObject(json),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        queue.add(jsonRequest);
    }
}
