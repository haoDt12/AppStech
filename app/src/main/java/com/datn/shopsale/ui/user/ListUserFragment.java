package com.datn.shopsale.ui.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.shopsale.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListUserFragment extends Fragment {

    public ListUserFragment() {
        // Required empty public constructor
    }

    public static ListUserFragment newInstance() {
        ListUserFragment fragment = new ListUserFragment();
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
        return inflater.inflate(R.layout.fragment_list_user, container, false);
    }
}