package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineshopping.Model.Users;
import com.example.onlineshopping.Prevalent.prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private TextView Registrationtxt,Logintxt;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Registrationtxt = findViewById(R.id.registrationtxtId);
        Logintxt = findViewById(R.id.logintxtId);

        Paper.init(this);

        loadingBar = new ProgressDialog(this);

        Registrationtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        Logintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        String UserPhoneKey = Paper.book().read(prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(prevalent.UserPasswordKey);



        if(UserPhoneKey!=""&&UserPasswordKey!="")
        {

            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait while we are checking the credentials");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                validate(UserPhoneKey,UserPasswordKey);


            }
        }



    }

    private void validate(final String userPhoneKey, final String userPasswordKey) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("users").child(userPhoneKey).exists())
                {

                    Users userdata = dataSnapshot.child("users").child(userPhoneKey).getValue(Users.class);

                    if(userdata.getPhone().equals(userPhoneKey))
                    {
                        if(userdata.getPassword().equals(userPasswordKey))
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "you are already logged in ,", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            prevalent.currentonlineUser = userdata;
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(MainActivity.this, "password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }

                else {
                    Toast.makeText(MainActivity.this, "account with this phone number doesn't exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "you need to create a new account", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
