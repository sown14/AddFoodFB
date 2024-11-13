package com.ddong.appfood_.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.ddong.appfood_.Adapter.BestFoodAdapter;
import com.ddong.appfood_.Adapter.CategoryAdapter;
import com.ddong.appfood_.Adapter.SlideshowAdapter;
import com.ddong.appfood_.Domain.Category;
import com.ddong.appfood_.Domain.Foods;
import com.ddong.appfood_.Domain.Location;
import com.ddong.appfood_.Domain.Price;
import com.ddong.appfood_.Domain.Time;
import com.ddong.appfood_.R;
import com.ddong.appfood_.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private RecyclerView slideshowRecyclerView;
    private Timer timer;
    private final long DELAY_MS = 500; // Thời gian trễ giữa các lần chuyển đổi, tính bằng mili giây
    private final long PERIOD_MS = 2000; // Thời gian chuyển đổi giữa các hình ảnh, tính bằng mili giây
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocation();
        initPrice();
        initTime();
        initBestFood();
        initCategory();
        setVariable();
        initSlideshow();
    }

    private void initSlideshow() {
        slideshowRecyclerView = findViewById(R.id.slideshowView);
        layoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        slideshowRecyclerView.setLayoutManager(layoutManager);
        SlideshowAdapter slideshowAdapter = new SlideshowAdapter();
        slideshowRecyclerView.setAdapter(slideshowAdapter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (layoutManager != null && slideshowAdapter != null) {
                            int newPosition = layoutManager.findFirstVisibleItemPosition() + 1;
                            if (newPosition == slideshowAdapter.getItemCount()) { // Nếu RecyclerView đã hiển thị hết tất cả các mục
                                newPosition = 0; // Đặt vị trí về 0 để chuyển đến mục đầu tiên
                            }
                            slideshowRecyclerView.smoothScrollToPosition(newPosition);
                        }
                    }
                });
            }
        }, DELAY_MS, PERIOD_MS);
    }
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void setVariable() {
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.searchEdt.getText().toString().trim();
                if (!text.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                    intent.putExtra("text", text);
                    intent.putExtra("isSearch", true);
                    startActivity(intent);
                }
            }
        });
        binding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });
        binding.restauranBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderShow.class));
            }
        });
        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            }
        });

    }

    private void initBestFood() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarbestFood.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query = myRef.orderByChild("BestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size() > 0) {
                        binding.bestFoodView.setLayoutManager(new LinearLayoutManager
                                (MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter adapter = new BestFoodAdapter(list);
                        binding.bestFoodView.setAdapter(adapter);

                    }
                    binding.progressBarbestFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (list.size() > 0) {
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                        RecyclerView.Adapter adapter = new CategoryAdapter(list);
                        binding.categoryView.setAdapter(adapter);

                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initLocation() {
        DatabaseReference myRed = database.getReference("Location");//lấy tham chiếu tới vùng (node) "location" trong cơ sở dữ liệu Firebase
        ArrayList<Location> list = new ArrayList<>();

        // lắng nghe sự kiện khi một lần duy nhất có dữ liệu thay đổi tại một vùng (node) cụ thể trong cơ sở dữ liệu.

        //value Even : interface được triển khai để xử lý sự kiện khi có sự thay đổi dữ liệu tại vùng (node) được tham chiếu.
        myRed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //được gọi khi có dữ liệu mới được trả về từ vùng (node) được tham chiếu.

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.locationSp.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // được gọi khi có lỗi xảy ra trong quá trình lắng nghe sự kiện.

            }
        });
    }

    private void initTime() {
        DatabaseReference myRed = database.getReference("Time");//lấy tham chiếu tới vùng (node) "Time" trong cơ sở dữ liệu Firebase
        ArrayList<Time> list = new ArrayList<>();

        // lắng nghe sự kiện khi một lần duy nhất có dữ liệu thay đổi tại một vùng (node) cụ thể trong cơ sở dữ liệu.

        //value Even : interface được triển khai để xử lý sự kiện khi có sự thay đổi dữ liệu tại vùng (node) được tham chiếu.
        myRed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //được gọi khi có dữ liệu mới được trả về từ vùng (node) được tham chiếu.

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Time.class));
                    }
                    ArrayAdapter<Time> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.timeSp.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // được gọi khi có lỗi xảy ra trong quá trình lắng nghe sự kiện.

            }
        });
    }

    private void initPrice() {
        DatabaseReference myRed = database.getReference("Price");//lấy tham chiếu tới vùng (node) "Price" trong cơ sở dữ liệu Firebase
        ArrayList<Price> list = new ArrayList<>();

        // lắng nghe sự kiện khi một lần duy nhất có dữ liệu thay đổi tại một vùng (node) cụ thể trong cơ sở dữ liệu.

        //value Even : interface được triển khai để xử lý sự kiện khi có sự thay đổi dữ liệu tại vùng (node) được tham chiếu.
        myRed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //được gọi khi có dữ liệu mới được trả về từ vùng (node) được tham chiếu.

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Price.class));
                    }
                    ArrayAdapter<Price> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.priceSp.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // được gọi khi có lỗi xảy ra trong quá trình lắng nghe sự kiện.

            }
        });
    }
}