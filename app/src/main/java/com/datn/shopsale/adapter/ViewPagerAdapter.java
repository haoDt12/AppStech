package com.datn.shopsale.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.datn.shopsale.ui.dashboard.order.CancelOrderFragment;
import com.datn.shopsale.ui.dashboard.order.InTransitOrderFragment;
import com.datn.shopsale.ui.dashboard.order.PayCompleteFragment;
import com.datn.shopsale.ui.dashboard.order.WaitConfirmFragment;
import com.datn.shopsale.ui.dashboard.order.WaitngGetOrderFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = WaitConfirmFragment.newInstance();
                break;
            case 1:
                fragment = WaitngGetOrderFragment.newInstance();
                break;
            case 2:
                fragment = InTransitOrderFragment.newInstance();
                break;
            case 3:
                fragment = PayCompleteFragment.newInstance();
                break;
            case 4:
                fragment = CancelOrderFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
