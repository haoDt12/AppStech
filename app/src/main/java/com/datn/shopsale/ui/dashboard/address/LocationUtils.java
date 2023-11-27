package com.datn.shopsale.ui.dashboard.address;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    public static String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Extract components of the address
                String fullAddress = address.getAddressLine(0);
//                String quanHuyen = address.getSubAdminArea();
//                String tinhThanhPho = address.getAdminArea();

                String[] parts = fullAddress.split(", "); // Split the address by commas and space

                if (parts.length >= 4) {
                    String desiredAddress = TextUtils.join(", ", Arrays.copyOfRange(parts, 1, 4));
                    Log.d("LocationUtils", "Desired Address: " + desiredAddress);
                    //Toast.makeText(context, "Desired Address: " + desiredAddress, Toast.LENGTH_SHORT).show();
                    return desiredAddress;
                } else {
                    Log.d("LocationUtils", "Invalid address format");
                    Toast.makeText(context, "Invalid address format", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            Log.e("LocationUtils", "Error getting address", e);
            Toast.makeText(context, "Error getting address", Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}
