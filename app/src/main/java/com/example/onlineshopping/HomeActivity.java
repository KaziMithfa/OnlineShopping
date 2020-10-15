package com.example.onlineshopping;

import android.content.Intent;
import android.os.Bundle;

import com.example.onlineshopping.Model.Products;
import com.example.onlineshopping.Prevalent.prevalent;
import com.example.onlineshopping.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference productsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null)
        {
            type = getIntent().getExtras().get("Admin").toString();
        }





        productsRef = FirebaseDatabase.getInstance().getReference("Products");
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!type.equals("Admin"))
                {
                    Intent intent = new Intent(HomeActivity.this,CartActivity.class);
                    startActivity(intent);

                }

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);



        if(!type.equals("Admin"))
        {
            userNameTextView.setText(prevalent.currentonlineUser.getName());
            Picasso.get().load(prevalent.currentonlineUser.getImage()).into(profileImageView);
        }





    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products>options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productsRef,Products.class).build();

       FirebaseRecyclerAdapter<Products,ProductViewHolder>adapter
               = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {

               productViewHolder.txtproductName.setText(products.getPname());
               productViewHolder.txtProductDescription.setText(products.getDescription());
               productViewHolder.txtProductPrice.setText("Price = "+products.getPrice()+"Tk.");
               Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

               productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       if(type.equals("Admin"))
                       {
                           Intent intent = new Intent(HomeActivity.this,AdminMaintainActivity.class);
                           intent.putExtra("pid",products.getPid());
                           startActivity(intent);

                       }

                       else {
                           Intent intent = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                           intent.putExtra("pid",products.getPid());
                           startActivity(intent);
                       }



                   }
               });



           }

           @NonNull
           @Override
           public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item_layout,parent,false);
               ProductViewHolder productViewHolder = new ProductViewHolder(view);
               return  productViewHolder;
           }
       };

       recyclerView.setAdapter(adapter);
       adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {

            if(!type.equals("Admin"))
            {
                Intent intent = new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);

            }




        }
        else if (id == R.id.nav_search) {


        } else if (id == R.id.nav_categories) {

        }
        else if (id == R.id.nav_settings) {

            if(!type.equals("Admin")){
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        }
        else if (id == R.id.nav_logout) {

            if(!type.equals("Admin")){

                Paper.book().destroy();

                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }





        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
