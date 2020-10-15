package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.onlineshopping.Model.Products;
import com.example.onlineshopping.Prevalent.prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productDescription,productName;
    private Button addToCartBtn;
    private String ProductID = "",state = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ProductID = getIntent().getStringExtra("pid");




        numberButton = findViewById(R.id.number_btn);
        productImage = findViewById(R.id.product_image_details);
        productName= findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);
        addToCartBtn = findViewById(R.id.pd_add_to_cart_btn);

        getproductdetails(ProductID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(state.equals("Order Placed"))
                {
                    Toast.makeText(ProductDetailsActivity.this, "You can order more product after getting your products", Toast.LENGTH_SHORT).show();

                }
                else{
                    addingToCartlist();

                }



            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();

    }

    private void CheckOrderState() {

        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(prevalent.currentonlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    String shippingstate = dataSnapshot.child("state").getValue().toString();

                    if(shippingstate.equals("not shipped"))

                    {
                        state = "Order Placed";


                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void addingToCartlist() {

        String savecurrenttime,savecurrentdate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate =  new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate  = currentDate.format(calForDate.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime = currentTime.format(calForDate.getTime());

        final  DatabaseReference cartlistRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",ProductID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",savecurrentdate);
        cartMap.put("time",savecurrenttime);
        cartMap.put("quantity",numberButton.getNumber());


        cartlistRef.child(prevalent.currentonlineUser.getPhone())
                .child("Products").child(ProductID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                cartlistRef.child("Admin View").child(prevalent.currentonlineUser.getPhone())
                        .child("Products").child(ProductID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(ProductDetailsActivity.this, "Your product is added to the cartlist successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                        startActivity(intent);

                    }
                });



            }
        });





    }

    private void getproductdetails(String productID) {

        DatabaseReference productsref = FirebaseDatabase.getInstance().getReference().child("Products");
        productsref.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                   productDescription.setText(products.getDescription());
                   productPrice.setText(products.getPrice());

                    Picasso.get().load(products.getImage()).into(productImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
