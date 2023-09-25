package com.datn.shopsale.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.datn.shopsale.R;
import com.datn.shopsale.databinding.FragmentDashboardBinding;

import java.util.Objects;

public class DashboardFragment extends Fragment {
    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }
    public DashboardFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
    private Button btnLogOut;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnLogOut = view.findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(view1.getContext());
            dialog.setContentView(R.layout.dialog_log_out);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(view1.getContext().getDrawable(R.drawable.dialog_bg));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);
            windowAttributes.gravity = Gravity.BOTTOM;

            ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnCancel.setOnClickListener(view2 -> {
                dialog.cancel();
            });
            btnConfirm.setOnClickListener(view2 -> {
                dialog.dismiss();
            });
            dialog.show();
        });
    }
}