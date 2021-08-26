package com.halilibrahimaksoy.voir.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halilibrahimaksoy.voir.Activity.FeedRecord;
import com.halilibrahimaksoy.voir.Adapter.ViewPagerAdapter;
import com.halilibrahimaksoy.voir.R;


public class Global extends Fragment {

    TabLayout tblGlobal;
    ViewPager vpgrGlobal;
    FloatingActionButton fabGlobalFeedAdd;
    private String query = null;

    public Global() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_global, container, false);
        fabGlobalFeedAdd = (FloatingActionButton) view.findViewById(R.id.fabGlobalFeedAdd);
        fabGlobalFeedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FeedRecord.class));
            }
        });

        return view;
    }

    public Global(String query) {

        this.query = query;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tblGlobal = (TabLayout) getActivity().findViewById(R.id.tblGlobal);
        vpgrGlobal = (ViewPager) getActivity().findViewById(R.id.vpgrGlobal);

        setupViewPager(vpgrGlobal);


        tblGlobal.setupWithViewPager(vpgrGlobal);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new GlobalNew(query), "NEW");
        adapter.addFrag(new GlobalPopular(query), "POPULAR");
        viewPager.setAdapter(adapter);
    }


}
