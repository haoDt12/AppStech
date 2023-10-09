package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.shopsale.R;

public class PayCompleteFragment extends Fragment {

    public PayCompleteFragment() {
        // Required empty public constructor
    }

    public static PayCompleteFragment newInstance() {
        PayCompleteFragment fragment = new PayCompleteFragment();
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
        return inflater.inflate(R.layout.fragment_pay_complete, container, false);
    }
}