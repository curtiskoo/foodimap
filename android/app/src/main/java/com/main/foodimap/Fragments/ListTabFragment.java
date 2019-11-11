package com.main.foodimap.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.main.foodimap.Controller.Restaurant;
import com.main.foodimap.MainActivity;
import com.main.foodimap.R;
import com.main.foodimap.Services.DataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ListTabFragment extends Fragment implements Observer {
    private DataService dataService;
    private ArrayList<String> restaurantObjects = new ArrayList<>();
    private ArrayAdapter<String> listViewAdapter;

    public ListTabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_list, container, false);

        ListView listView = view.findViewById(R.id.listView);


        listViewAdapter = new ArrayAdapter<String>(
            getContext(),
            android.R.layout.simple_list_item_1,
            restaurantObjects
        );

        listView.setAdapter(listViewAdapter);

        return view;
    }


    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof DataService) {
            if (o instanceof ArrayList<?>) {
                ArrayList<String> listdata = getArrayFromRestaurantJSON((ArrayList<Restaurant>) o);
                listViewAdapter.clear();
                listViewAdapter.addAll(listdata);
                listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private ArrayList<String> getArrayFromRestaurantJSON(ArrayList<Restaurant> results) {
        ArrayList<String> listdata = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            listdata.add(results.get(i).name);
        }
        return listdata;
    }


}
