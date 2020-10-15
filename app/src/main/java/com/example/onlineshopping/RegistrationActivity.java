package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText nameEditText,phoneEditText,passwordEditText;
    private Button createAccountBtn;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        imageView = findViewById(R.id.ImageviewId);
       nameEditText = findViewById(R.id.nameEditTextId);
       phoneEditText = findViewById(R.id.phoneEditTextId);
        passwordEditText = findViewById(R.id.passwordEditTextId);
        createAccountBtn = findViewById(R.id.registerbtnId);

        loadingBar = new ProgressDialog(this);


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAccount();

            }
        });








    }

    private void createAccount() {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please , write your name", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please , write your phone number", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please , write your password", Toast.LENGTH_SHORT).show();
        }

        else{

            loadingBar.setTitle("Create the accout");
            loadingBar.setMessage("Please wait while we are creating the account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatephoneNumber(name,phone,password);




        }



    }

    private void validatephoneNumber(final String name, final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child("users").child(phone).exists())
                {

                    HashMap<String,Object>userDataMap = new HashMap<>();

                    userDataMap.put("name",name);
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);

                    RootRef.child("users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegistrationActivity.this, "your account has been created successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }

                                    else{
                                        Toast.makeText(RegistrationActivity.this, "Network problem, please try again after some time", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();


                                    }

                                }
                            });





                }
                else {
                    Toast.makeText(RegistrationActivity.this, "Account with this number has already exist,please try with another number", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent  intent = new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
