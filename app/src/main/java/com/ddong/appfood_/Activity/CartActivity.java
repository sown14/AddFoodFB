package com.ddong.appfood_.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ddong.appfood_.Adapter.CartAdapter;
import com.ddong.appfood_.Domain.Foods;
import com.ddong.appfood_.Helper.ChangeNumberItemsListener;
import com.ddong.appfood_.Helper.ManagmentCart;
import com.ddong.appfood_.Helper.TinyDB;
import com.ddong.appfood_.R;
import com.ddong.appfood_.databinding.ActivityCartBinding;
import com.google.android.play.core.integrity.b;

import java.util.ArrayList;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;
    private static final int REQUEST_ORDER_DETAIL = 1; // Đặt mã yêu cầu
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);
        tinyDB = new TinyDB(this);


        setVariable();
        caclulateCart();
        initList();
        dialog();

    }

    private void openDialogInformation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_customer_information, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        AppCompatButton cancel = dialogView.findViewById(R.id.cancel_Btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        AppCompatButton confirm = dialogView.findViewById(R.id.confirm_Btn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEditText = dialogView.findViewById(R.id.name_Edt);
                EditText addressEditText = dialogView.findViewById(R.id.address_Edt);
                EditText phoneNumberEditText = dialogView.findViewById(R.id.phonenumber_Edt);
                EditText emailEditText = dialogView.findViewById(R.id.email_Edt);

                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String email = emailEditText.getText().toString();

                if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
                    // Hiển thị thông báo lỗi nếu có trường nào đó trống
                    Toast.makeText(getApplicationContext(), "Please complete all information", Toast.LENGTH_SHORT).show();
                } else if (!isValidPhoneNumber(phoneNumber)) {
                    // Kiểm tra số điện thoại
                    phoneNumberEditText.setError("Phone number is not valid");
                    Toast.makeText(CartActivity.this, "Phone number must contain 10 digits", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    // Kiểm tra email
                    emailEditText.setError("Email is not valid");
                    Toast.makeText(CartActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                } else {
                    // Dữ liệu nhập vào là hợp lệ, tiếp tục xử lý

                    Intent intent = new Intent(CartActivity.this, Orderdeltail.class);
                    intent.putExtra("name", name);
                    intent.putExtra("address", address);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("email", email);

                    // Lấy danh sách đơn hàng từ ManagmentCart
                    ArrayList<Foods> orderList = managmentCart.getListCart();

                    // Đính kèm danh sách đơn hàng vào Intent
//                    intent.putParcelableArrayListExtra("orderList", (ArrayList<? extends Parcelable>) orderList);
                    intent.putExtra("orderList", orderList);

                    // Khởi chạy Activity mới với Intent đã đính kèm dữ liệu
                    startActivity(intent);
                }

                // Đóng dialog sau khi xác nhận
                alertDialog.dismiss();
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Kiểm tra định dạng số điện thoại (có đúng 10 chữ số không)
        return phoneNumber.length() == 10 && phoneNumber.matches("[0-9]+");
    }

    private boolean isValidEmail(String email) {
        // Kiểm tra định dạng email
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void dialog() {
        binding.placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Notification")
                        .setMessage("Confirm order")
                        .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openDialogInformation();
                                Toast.makeText(CartActivity.this, "Success", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Toast.makeText(CartActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ORDER_DETAIL && resultCode == RESULT_OK && data != null) {
            ArrayList<Foods> updatedOrderList = (ArrayList<Foods>) getIntent().getSerializableExtra("updatedOrderList");
            // Cập nhật danh sách đơn hàng trong giỏ hàng
            managmentCart.updateCart(updatedOrderList);
            // Cập nhật giao diện
            adapter.notifyDataSetChanged();
            caclulateCart();
        }
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

    }

    private void initList() {
//        if (managmentCart.getListCart().isEmpty()) {
//            binding.emptyTxt.setVisibility(View.VISIBLE);
//            binding.scrollviewCart.setVisibility(View.GONE);
//        } else {
//            binding.emptyTxt.setVisibility(View.GONE);
//            binding.scrollviewCart.setVisibility(View.VISIBLE);
//        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                caclulateCart();
            }
        });
        binding.cartView.setAdapter(adapter);
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


}