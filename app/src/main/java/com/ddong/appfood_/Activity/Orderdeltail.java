package com.ddong.appfood_.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ddong.appfood_.Adapter.CartAdapter;
import com.ddong.appfood_.Adapter.Orderdeltail_Adapter;
import com.ddong.appfood_.Domain.Foods;
import com.ddong.appfood_.Domain.Order;
import com.ddong.appfood_.Helper.ChangeNumberItemsListener;
import com.ddong.appfood_.Helper.ManagmentCart;
import com.ddong.appfood_.R;
import com.ddong.appfood_.databinding.ActivityOrderdeltailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Orderdeltail extends BaseActivity {
    private ActivityOrderdeltailBinding binding;
    private RecyclerView.Adapter adapter;
    private ArrayList<Foods> orderList;
    private ManagmentCart managmentCart;
    private double tax;
    private String name, address, phoneNumber, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderdeltailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);

        getinit();
        caclulateCart();

        if (orderList != null && !orderList.isEmpty()) {
            Log.d("Orderdeltail", "Order list is not empty. Initializing list...");

            initlist();
        } else {
            Toast.makeText(this, "Empty order list", Toast.LENGTH_SHORT).show();
        }


    }

    private void getinit() {
        // Nhận thông tin người dùng nhập và danh sách đơn hàng từ Intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        phoneNumber = intent.getStringExtra("phoneNumber");
        email = intent.getStringExtra("email");

        orderList = (ArrayList<Foods>) intent.getSerializableExtra("orderList");
        Log.d("Orderdeltail", "Danh sách đơn hàng: " + orderList);


        binding.nameTextView.setText(name);
        binding.addressTextView.setText(address);
        binding.phoneNumberTextView.setText(phoneNumber);
        binding.emailTextView.setText(email);
    }

    private void initlist() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.orderRecyclerView.setLayoutManager(linearLayoutManager);

        // Kiểm tra danh sách đơn hàng trước khi gán adapter
        if (orderList != null && !orderList.isEmpty()) {

            adapter = new Orderdeltail_Adapter(orderList, this, new ChangeNumberItemsListener() {
                @Override
                public void change() {
                    adapter.notifyDataSetChanged();
                    caclulateCart();

                }
            });
            binding.orderRecyclerView.setAdapter(adapter);
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gửi dữ liệu đã cập nhật về CartActivity trước khi kết thúc Orderdeltail
                    adapter.notifyDataSetChanged();
                    sendUpdatedOrderList();
                    finish();
                }
            });


        } else {
            // Hiển thị thông báo hoặc xử lý phù hợp nếu danh sách đơn hàng rỗng
            Toast.makeText(this, "Empty order list", Toast.LENGTH_SHORT).show();
        }
        binding.completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrdertoFirebase();
            }
        });
    }

    private void saveOrdertoFirebase() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        String orderId = ordersRef.push().getKey();

        // Tạo đối tượng Order và đặt các giá trị cho nó
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomer_Name(name);
        order.setCustomer_Address(address);
        order.setCustomer_Phone(phoneNumber);
        order.setCustomer_Email(email);
        order.setTotalPrice(caclulateTotalitem());
        order.setOrderTime(getCurrentDateTime());

        DatabaseReference orderRef = ordersRef.child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        // Lấy danh sách các ID của các mặt hàng từ đơn hàng
                        ArrayList<String> itemIds = order.getItems();
                        if (itemIds != null && !itemIds.isEmpty()) {
                            for (String itemId : itemIds) {
                                // Truy xuất thông tin của mỗi mặt hàng từ ID
                                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
                                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Lấy thông tin của mặt hàng từ dataSnapshot và hiển thị nó
                                            Foods item = dataSnapshot.getValue(Foods.class);
                                            if (item != null) {
                                                Log.d("OrderItem", "Name: " + item.getTitle() + ", Quantity: " + item.getNumberInCart());
                                                // Hiển thị thông tin của mặt hàng trên giao diện người dùng
                                            }
                                        } else {
                                            Log.d("OrderItem", "Item not found");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("OrderItem", "Failed to read item details: " + databaseError.getMessage());
                                    }
                                });
                            }
                        } else {
                            Log.d("OrderItem", "No items in the order");
                        }
                    }
                } else {
                    Log.d("OrderItem", "Order not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("OrderItem", "Failed to read order details: " + databaseError.getMessage());
            }
        });
        orderRef.setValue(order); // Lưu thông tin của đơn hàng

        DatabaseReference itemsRef = orderRef.child("items"); // Tham chiếu đến nút "items" trong đơn hàng

        // Lưu danh sách các mặt hàng vào Firebase
        for (Foods item : orderList) {
            String itemId = itemsRef.push().getKey(); // Tạo ID duy nhất cho mỗi mặt hàng
            itemsRef.child(itemId).setValue(item); // Lưu mặt hàng vào Firebase với ID tương ứng
        }

        // Hiển thị thông báo hoặc thực hiện hành động phù hợp sau khi hoàn tất
        Toast.makeText(this, "Order completed", Toast.LENGTH_SHORT).show();

        // Kết thúc activity và trả về kết quả
        Intent intent = new Intent();
        intent.putExtra("updatedOrderList", orderList);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void sendUpdatedOrderList() {
        // Kết thúc activity và trả về kết quả
        Intent intent = new Intent();
        intent.putExtra("updatedOrderList", orderList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        sendUpdatedOrderList();
        adapter.notifyDataSetChanged();
        super.onBackPressed();
    }

    private void caclulateCart() {
        double persentTax = 0.02;//2%
        double delivery = 10;

        tax = Math.round(managmentCart.getTotalFee() * persentTax * 100.0) / 100;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalfeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);

    }

    private double caclulateTotalitem() {
        double persentTax = 0.02;//2%
        double delivery = 10;

        tax = Math.round(managmentCart.getTotalFee() * persentTax * 100.0) / 100;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100;
        return total;

    }

    private String getCurrentDateTime() {
        // Lấy đối tượng Calendar để lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();

        // Lấy thời gian hiện tại từ đối tượng Calendar
        Date currentTime = calendar.getTime();

        // Định dạng thời gian hiện tại dưới dạng chuỗi
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Chuyển đổi thời gian hiện tại thành chuỗi
        String currentDateTime = dateFormat.format(currentTime);

        // Trả về thời gian hiện tại dưới dạng chuỗi
        return currentDateTime;
    }


}