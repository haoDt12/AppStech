package com.datn.shopsale.ui.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.datn.shopsale.R;

public class SettingActivity extends AppCompatActivity {
    private ImageButton imgBack;
    private LinearLayout idChangePass;
    private CardView imgLock;
    private LinearLayout idNotification;
    private CardView imgNotification;
    private SwitchCompat swbtnNotification;
    private LinearLayout idLanguage;
    private CardView imgLanguage;
    private SwitchCompat swbtnLanguage;
    private LinearLayout idMode;
    private CardView imgMode;
    private SwitchCompat swbtnMode;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        imgBack = (ImageButton) findViewById(R.id.img_back);
        idChangePass = (LinearLayout) findViewById(R.id.id_change_pass);
        imgLock = (CardView) findViewById(R.id.img_lock);
        idNotification = (LinearLayout) findViewById(R.id.id_notification);
        imgNotification = (CardView) findViewById(R.id.img_notification);
        swbtnNotification = (SwitchCompat) findViewById(R.id.swbtn_notification);
        idLanguage = (LinearLayout) findViewById(R.id.id_language);
        imgLanguage = (CardView) findViewById(R.id.img_language);
        swbtnLanguage = (SwitchCompat) findViewById(R.id.swbtn_language);
        idMode = (LinearLayout) findViewById(R.id.id_mode);
        imgMode = (CardView) findViewById(R.id.img_mode);
        swbtnMode = (SwitchCompat) findViewById(R.id.swbtn_mode);

        swbtnNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });
        swbtnLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });
        swbtnMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });
        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
}