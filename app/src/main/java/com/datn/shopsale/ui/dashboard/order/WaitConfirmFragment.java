package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.shopsale.R;

public class WaitConfirmFragment extends Fragment {

    public WaitConfirmFragment() {
        // Required empty public constructor
    }

    public static WaitConfirmFragment newInstance() {
        WaitConfirmFragment fragment = new WaitConfirmFragment();
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
        return inflater.inflate(R.layout.fragment_wait_confirm, container, false);
    }
}