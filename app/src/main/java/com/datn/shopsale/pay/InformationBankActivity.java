package com.datn.shopsale.pay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.datn.shopsale.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InformationBankActivity extends AppCompatActivity {
    private EditText edDayOfRelease;
    private Toolbar toolbarInfoBank;
    private TextView tvPrice;
    private TextView tvPriceProduct;
    private TextView tvTransactionCost;
    private TextView tvCodeOrders;
    private TextView tvSupplier;
    private TextInputEditText edNumberCard;
    private TextInputEditText edUserName;
    private TextInputEditText edDate;
    private Button btnCancle;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation_bank);
        init();
        edDayOfRelease = findViewById(R.id.ed_date);
        edDayOfRelease.setKeepScreenOn(false);
        edDayOfRelease.setOnClickListener(v -> {
            chooseDate();
        });
    }
    private void init(){
        toolbarInfoBank = (Toolbar) findViewById(R.id.toolbar_info_bank);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        tvPriceProduct = (TextView) findViewById(R.id.tv_price_product);
        tvTransactionCost = (TextView) findViewById(R.id.tv_transaction_cost);
        tvCodeOrders = (TextView) findViewById(R.id.tv_code_orders);
        tvSupplier = (TextView) findViewById(R.id.tv_supplier);
        edNumberCard = (TextInputEditText) findViewById(R.id.ed_number_card);
        edUserName = (TextInputEditText) findViewById(R.id.ed_user_name);
        edDate = (TextInputEditText) findViewById(R.id.ed_date);
        btnCancle = (Button) findViewById(R.id.btn_cancle);
        btnSave = (Button) findViewById(R.id.btn_save);
        setSupportActionBar(toolbarInfoBank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarInfoBank.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
    private void chooseDate() {

        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");
                edDayOfRelease.setText(simpleDateFormat.format(calendar.getTime()));
            }
        },nam, thang, ngay);
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.show();
    }
}