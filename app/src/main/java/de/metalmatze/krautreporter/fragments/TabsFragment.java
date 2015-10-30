package de.metalmatze.krautreporter.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.TabsAdapter;

public class TabsFragment extends Fragment {

    public TabsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tabs, container, false);

        TabsAdapter tabsAdapter = new TabsAdapter(getFragmentManager());
        ViewPager tabsPager = (ViewPager) fragmentView.findViewById(R.id.tabs_pager);
        tabsPager.setAdapter(tabsAdapter);

        TabLayout tabs = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabs.setupWithViewPager(tabsPager);

        return fragmentView;
    }
}
