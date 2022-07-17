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

import com.example.onlineshopping.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersref;
    private DatabaseReference cartRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersref = FirebaseDatabase.getInstance().getReference().child("Orders");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        orderList = findViewById(R.id.orderList);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders>options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersref,AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder>adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder adminOrdersViewHolder, final int i, @NonNull AdminOrders adminOrders) {




                adminOrdersViewHolder.userName.setText("UserName : "+adminOrders.getName());
                adminOrdersViewHolder.userPhoneNumber.setText("Phone : "+adminOrders.getPhone());
                adminOrdersViewHolder.userTotalPrice.setText("Total Amount : "+adminOrders.getTotalAmount()+"Tk.");
                adminOrdersViewHolder.userDateTime.setText("Order at : "+adminOrders.getDate()+""+adminOrders.getTime());
                adminOrdersViewHolder.userShippingAddress.setText("Shipping Address :  "+adminOrders.getAddress()+"\n"+adminOrders.getCity());

                adminOrdersViewHolder.ShowordersButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String  uID = getRef(i).getKey();

                        Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductActivity.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);

                    }
                });

                adminOrdersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence[] options = new CharSequence[]
                                {
                                        "Yes",
                                        "No"

                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Have you shipped this order product ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {

                                if(j == 0)
                                {
                                   String uID = getRef(i).getKey();

                                    RemoveOrder(uID);

                                }
                                else{
                                    finish();
                                }



                            }
                        });
                        builder.show();


                    }
                });




            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_item_layout,parent,false);
                return new AdminOrdersViewHolder(view);
            }
        };

        orderList.setAdapter(adapter);
        adapter.startListening();




    }

    private void RemoveOrder(String uID) {

        ordersref.child(uID).removeValue();
        cartRef.child("Admin View").child(uID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminNewOrdersActivity.this, "The order has been shifted successfully....", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminNewOrdersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    public  static  class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress;
        public Button ShowordersButton;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowordersButton = itemView.findViewById(R.id.show_all_products_btn);



        }
    }
}
