package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText phoneEditText,passwordEditText;
    private CheckBox checkBox;

    private TextView forgetpassword;
    private Button loginbtn;
    private TextView adminpanel,notadminpanel;

    private ProgressDialog loadingBar;


    private String parentDbName = "users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageView = findViewById(R.id.ImageviewloginId);
        phoneEditText= findViewById(R.id.phoneloginId);
       passwordEditText= findViewById(R.id.passwordloginId);
       checkBox = findViewById(R.id.checkbox);
       forgetpassword = findViewById(R.id.forgetpassword);

       loginbtn = findViewById(R.id.loginbtnId);
       adminpanel = findViewById(R.id.adminpanellink);
       notadminpanel = findViewById(R.id.notadminpanellink);

       loadingBar = new ProgressDialog(this);


        Paper.init(this);






       adminpanel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               loginbtn.setText("Login Admin");
               adminpanel.setVisibility(View.INVISIBLE);
               notadminpanel.setVisibility(View.VISIBLE);
               parentDbName = "admins";

           }
       });

       notadminpanel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               loginbtn.setText("Login User");
               adminpanel.setVisibility(View.VISIBLE);
               notadminpanel.setVisibility(View.INVISIBLE);
               parentDbName = "users";

           }
       });


       loginbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               login();
           }
       });





    }

    private void login() {

        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write  your phone number", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write  your password", Toast.LENGTH_SHORT).show();
        }


        else{

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validate(phone,password);


        }
    }

    private void validate(final String phone, final String password) {



        if(checkBox.isChecked())
        {
            Paper.book().write(prevalent.UserPhoneKey,phone);
            Paper.book().write(prevalent.UserPasswordKey,password);

        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {

                    Users userdata = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(userdata.getPhone().equals(phone))
                    {
                        if(userdata.getPassword().equals(password))
                        {
                            if(parentDbName.equals("admins"))
                            {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Welcome  admin, you are logged in successfully ,", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this,AdminHomeActivity.class);
                                startActivity(intent);

                            }
                            else if(parentDbName.equals("users")) {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Welcome , you are logged in successfully ,", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                prevalent.currentonlineUser = userdata;
                                startActivity(intent);

                            }


                        }
                        else {
                            Toast.makeText(LoginActivity.this, "password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }

                else {
                    Toast.makeText(LoginActivity.this, "account with this phone number doesn't exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "you need to create a new account", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
