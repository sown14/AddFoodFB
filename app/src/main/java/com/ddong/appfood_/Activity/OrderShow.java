package com.ddong.appfood_.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ddong.appfood_.Adapter.YourOrderAdapter;
import com.ddong.appfood_.Domain.Item;
import com.ddong.appfood_.Helper.ManagmentCart;
import com.ddong.appfood_.R;
import com.ddong.appfood_.databinding.ActivityOrderShowBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderShow extends AppCompatActivity {
    private RecyclerView ordershowview;
    private List<Item> itemList;
    private YourOrderAdapter adapter;
    private ActivityOrderShowBinding binding;
    ManagmentCart managmentCart;
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOrderShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ordershowview = findViewById(R.id.yourOrderView); // Thay đổi ID nếu cần
        ordershowview.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        // Tạo một số dữ liệu mẫu hoặc lấy từ Firebase
        adapter = new YourOrderAdapter(itemList);
        ordershowview.setAdapter(adapter);
        // Load dữ liệu từ Firebase hoặc nguồn khác vào itemList ở đây
        getinit();
    }

    private void getinit() {

        binding.searchPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = binding.searchphoneorderEdt.getText().toString().trim(); // Lấy số điện thoại từ EditText
                if (!phoneNumber.isEmpty()) {
                    showResults(phoneNumber);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.restauranBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderShow.this, MainActivity.class));
            }
        });
        binding.cartyourOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderShow.this, CartActivity.class));
            }
        });
    }

    private void showResults(String phoneNumber) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("orders");

        // Truy vấn dữ liệu dựa trên số điện thoại
        Query phoneQuery = ref.orderByChild("customer_Phone").equalTo(phoneNumber);

        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    itemList.clear(); // Xóa danh sách hiện có
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        // Lấy danh sách item từ mỗi order
                        for (DataSnapshot itemSnapshot : orderSnapshot.child("items").getChildren()) {
                            Item item = itemSnapshot.getValue(Item.class);
                            itemList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "No orders found for this phone number", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}