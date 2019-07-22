package com.example.maptrial;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends FragmentActivity  {

    private ViewPager pager;
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Tabs
        pager = findViewById(R.id.pager);
        adapter = new TabAdapter(getSupportFragmentManager());
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        adapter.addFragment(new MapsTabFragment(), "Map");
        adapter.addFragment(new ListTabFragment(), "List");
        pager.setAdapter(adapter);
    }
}
