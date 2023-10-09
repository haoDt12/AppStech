package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.shopsale.R;

public class InTransitOrderFragment extends Fragment {

    public InTransitOrderFragment() {
        // Required empty public constructor
    }

    public static InTransitOrderFragment newInstance() {
        InTransitOrderFragment fragment = new InTransitOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_transit_order, container, false);
    }
}