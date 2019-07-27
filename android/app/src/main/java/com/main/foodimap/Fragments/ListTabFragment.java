package com.main.foodimap.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.main.foodimap.R;
import com.main.foodimap.Services.DataService;

import java.util.Observable;
import java.util.Observer;

public class ListTabFragment extends Fragment implements Observer {
    private DataService dataService;

    public ListTabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_list, container, false);
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println(o);
    }
}
