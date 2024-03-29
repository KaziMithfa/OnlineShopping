package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineshopping.Model.Cart;
import com.example.onlineshopping.Prevalent.prevalent;
import com.example.onlineshopping.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int overTotalprice = 0;


    private Button NextprocessBtn;
    private TextView txtTotalAmount, txtMsg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartlist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextprocessBtn = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.total_price);

        txtMsg1 = findViewById(R.id.msg1);

        NextprocessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalprice));
                startActivity(intent);
                finish();

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final  DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart>options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child(prevalent.currentonlineUser.getPhone()).child("Products"),Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart,CartViewHolder>adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                cartViewHolder.txtProductName.setText(cart.getPname());
                cartViewHolder.txtProductQuantity.setText("Quantity = "+cart.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price = "+cart.getPrice());

                int oneTypeProductPrice = (Integer.valueOf(cart.getPrice())*Integer.valueOf(cart.getQuantity()));
                overTotalprice = overTotalprice + oneTypeProductPrice;
                txtTotalAmount.setText("Total Price : "+String.valueOf(overTotalprice)+"Tk.");




                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"

                                };
                       AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                       builder.setTitle("Cart Options");
                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if(i == 0)
                               {
                                   Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                   intent.putExtra("pid",cart.getPid());
                                   startActivity(intent);

                               }
                               if(i ==1)
                               {
                                   cartListRef.child(prevalent.currentonlineUser.getPhone())
                                           .child("Products").child(cart.getPid())
                                           .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           cartListRef.child("Admin View").child(prevalent.currentonlineUser.getPhone())
                                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {

                                                   Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                   Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                                                   startActivity(intent);

                                               }
                                           });



                                       }
                                   });
                               }


                           }
                       });

                       builder.show();



                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder cartViewHolder = new CartViewHolder(view);
                return cartViewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void CheckOrderState() {

        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(prevalent.currentonlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("not shipped"))

                    {
                        txtTotalAmount.setText("Shipping state = not shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        NextprocessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products,once you received your previous  order", Toast.LENGTH_SHORT).show();

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}


