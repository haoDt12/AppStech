package com.datn.shopsale.ui.dashboard.address;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.ui.dashboard.address.Address.AddressCDW;
import com.datn.shopsale.ui.dashboard.address.Address.DistrictRespone;
import com.datn.shopsale.ui.dashboard.address.Address.WardsRespone;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityActivity extends AppCompatActivity {
    private ListView cityListView;
    private ListView districtListView;
    private ListView wardListView;
    private ApiService apiService;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        cityListView = findViewById(R.id.lv_city);
        districtListView = findViewById(R.id.lv_district);
        wardListView = findViewById(R.id.lv_ward);
        LinearLayout lnlLocation = findViewById(R.id.lnl_location_current);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://provinces.open-api.vn")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        getCities();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        lnlLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    CityActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(
                        CityActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String addressText = LocationUtils.getAddressFromLocation(CityActivity.this, location.getLatitude(), location.getLongitude());
                        if (addressText != null) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isCurrentLocationSelected", true);
                            resultIntent.putExtra("currentLocation", addressText);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(this, "Error getting current location", Toast.LENGTH_SHORT).show());
    }

    private void getCities() {
        Call<List<AddressCDW.City>> call = apiService.getCities();
        call.enqueue(new Callback<List<AddressCDW.City>>() {
            @Override
            public void onResponse(@NonNull Call<List<AddressCDW.City>> call, @NonNull Response<List<AddressCDW.City>> response) {
                if (response.isSuccessful()) {
                    List<AddressCDW.City> cities = response.body();
                    displayCities(cities);
                } else {
                    Toast.makeText(CityActivity.this, "Failed to load cities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AddressCDW.City>> call, @NonNull Throwable t) {
                Toast.makeText(CityActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCities(List<AddressCDW.City> cities) {
        ArrayAdapter<AddressCDW.City> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        cityListView.setAdapter(adapter);
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            cityListView.setVisibility(View.GONE);
            districtListView.setVisibility(View.VISIBLE);
            AddressCDW.City selectedCity = (AddressCDW.City) parent.getItemAtPosition(position);
            getDistricts(selectedCity.getCode(), selectedCity);
        });
    }

    private void getDistricts(int cityCode, AddressCDW.City selectedCity) {
        Call<DistrictRespone> call = apiService.getDistrict(cityCode, 2);
        call.enqueue(new Callback<DistrictRespone>() {
            @Override
            public void onResponse(@NonNull Call<DistrictRespone> call, @NonNull Response<DistrictRespone> response) {
                if (response.isSuccessful()) {
                    DistrictRespone districtResponse = response.body();
                    if (districtResponse != null && districtResponse.getDistricts() != null) {
                        displayDistricts(districtResponse.getDistricts(), selectedCity);
                    } else {
                        Toast.makeText(CityActivity.this, "No districts found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CityActivity.this, "Failed to load districts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DistrictRespone> call, @NonNull Throwable t) {
                Toast.makeText(CityActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWards(int districtCode, AddressCDW.City selectedCity, AddressCDW.District selectedDistrict) {
        Call<WardsRespone> call = apiService.getWard(districtCode, 2);
        call.enqueue(new Callback<WardsRespone>() {
            @Override
            public void onResponse(@NonNull Call<WardsRespone> call, @NonNull Response<WardsRespone> response) {
                if (response.isSuccessful()) {
                    WardsRespone wardsResponse = response.body();
                    if (wardsResponse != null && wardsResponse.getWards() != null) {
                        displayWards(wardsResponse.getWards(), selectedCity, selectedDistrict);
                    } else {
                        Toast.makeText(CityActivity.this, "No wards found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CityActivity.this, "Failed to load wards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WardsRespone> call, @NonNull Throwable t) {
                Toast.makeText(CityActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDistricts(List<AddressCDW.District> districts, AddressCDW.City selectedCity) {
        ArrayAdapter<AddressCDW.District> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, districts);
        districtListView.setAdapter(adapter);

        districtListView.setOnItemClickListener((parent, view, position, id) -> {
            districtListView.setVisibility(View.GONE);
            wardListView.setVisibility(View.VISIBLE);

            AddressCDW.District selectedDistrict = (AddressCDW.District) parent.getItemAtPosition(position);
            getWards(selectedDistrict.getCode(), selectedCity, selectedDistrict);
        });
    }

    private void displayWards(List<AddressCDW.Ward> wards, AddressCDW.City selectedCity, AddressCDW.District selectedDistrict) {
        ArrayAdapter<AddressCDW.Ward> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wards);
        wardListView.setAdapter(adapter);
        wardListView.setOnItemClickListener((parent, view, position, id) -> {
            AddressCDW.Ward selectedWard = (AddressCDW.Ward) parent.getItemAtPosition(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedCity", selectedCity.getName());
            resultIntent.putExtra("selectedDistrict", selectedDistrict.getName());
            resultIntent.putExtra("selectedWard", selectedWard.getName());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}