//package com.datn.shopsale.ui.dashboard.address;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.datn.shopsale.Interface.ApiService;
//import com.datn.shopsale.R;
//import com.datn.shopsale.models.modelAddress.City;
//import com.datn.shopsale.models.modelAddress.District;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class CitySelectionActivity extends AppCompatActivity {
//    private ApiService apiService;
//    private ListView cityListView;
//    private List<City> cities;
//    private List<District> dis;
//    private ArrayAdapter<City> cityAdapter;
//    private ArrayAdapter<District> districtAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_city_selection);
//
//        cityListView = findViewById(R.id.lv_city);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://provinces.open-api.vn")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(new OkHttpClient.Builder().build())
//                .build();
//
//        // Tạo một ApiService từ Retrofit
//        apiService = retrofit.create(ApiService.class);
//        // Gọi API để lấy danh sách tỉnh/thành phố
//
//
//        Call<List<City>> cityCall = apiService.getCities();
//        cityCall.enqueue(new Callback<List<City>>() {
//            @Override
//            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    cities = response.body();
//                    displayCities();
//                } else {
//                    Toast.makeText(CitySelectionActivity.this, "Không thể lấy danh sách tỉnh/thành phố", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<City>> call, Throwable t) {
//                Toast.makeText(CitySelectionActivity.this, "Lỗi khi tải danh sách tỉnh/thành phố", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                int selectedCityCode = cities.get(i).getCode();
//                Toast.makeText(CitySelectionActivity.this, "Mã tỉnh/thành phố: " + selectedCityCode, Toast.LENGTH_SHORT).show();
//
//                // Gọi API để lấy thông tin thành phố và danh sách quận/huyện của tỉnh/thành phố đã chọn
//                Call<City> call = apiService.getDistricts(selectedCityCode,2);
//                call.enqueue(new Callback<City>() {
//                    @Override
//                    public void onResponse(Call<City> call, Response<City> response) {
//                        City city = response.body();
//                         if(response.body() != null && response.isSuccessful()){
//                             List<District> districts = city.getDistricts();
//                             displayDistricts(districts);
//                         }else{
//                             Toast.makeText(CitySelectionActivity.this, "Không thể lấy danh sách quận/huyện", Toast.LENGTH_SHORT).show();
//                         }
//                    }
//
//                    @Override
//                    public void onFailure(Call<City> call, Throwable t) {
//                        Toast.makeText(CitySelectionActivity.this, "lỗi tải danh sách", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
//    private void displayCities() {
//        if (cities != null && !cities.isEmpty()) {
//            cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
//            cityListView.setAdapter(cityAdapter);
//        }
//    }
//    private void displayDistricts(List<District> districts) {
//        if (districts != null && !districts.isEmpty()) {
//            districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, districts);
//            cityListView.setAdapter(districtAdapter);
//        }
//    }
//}
