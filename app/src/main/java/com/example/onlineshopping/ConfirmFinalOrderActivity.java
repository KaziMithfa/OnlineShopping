package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlineshopping.Prevalent.prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEdittext,addressEditText,cityaddressEditText;
    private Button confirmOrderBtn;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price "+totalAmount+"Tk.", Toast.LENGTH_LONG).show();

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        nameEditText = findViewById(R.id.shippment_name);
        phoneEdittext = findViewById(R.id.shippment_phone_number);
        addressEditText = findViewById(R.id.shippment_address);
        cityaddressEditText = findViewById(R.id.shippment_city);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Check();

            }
        });


    }

    private void Check() {


        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please provide your full name", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(phoneEdittext.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please provide your phone number", Toast.LENGTH_SHORT).show();
        }

        else  if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please provide your address", Toast.LENGTH_SHORT).show();
        }

        else  if(TextUtils.isEmpty(cityaddressEditText.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please provide your city name", Toast.LENGTH_SHORT).show();
        }
        else {
            ConfirmOrder();
        }

    }

    private void ConfirmOrder() {

        String savecurrenttime,savecurrentdate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate =  new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate  = currentDate.format(calForDate.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime = currentTime.format(calForDate.getTime());


        final DatabaseReference ordersref =  FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(prevalent.currentonlineUser.getPhone());

        HashMap<String,Object> orderMap = new HashMap<>();

        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",phoneEdittext.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("city",cityaddressEditText.getText().toString());
        orderMap.put("date",savecurrentdate);
        orderMap.put("time",savecurrenttime);
        orderMap.put("state","not shipped");

        ordersref.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child(prevalent.currentonlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                Toast.makeText(ConfirmFinalOrderActivity.this, "your final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }

                        }
                    });



                }



            }
        });




    }
}
