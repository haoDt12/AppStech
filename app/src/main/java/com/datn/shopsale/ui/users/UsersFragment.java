package com.datn.shopsale.ui.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.datn.shopsale.R;
import com.datn.shopsale.databinding.FragmentUsersBinding;

public class UsersFragment extends Fragment {
    private FragmentUsersBinding binding;

    public UsersFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.tvUsers;

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}