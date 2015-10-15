package com.parse.starter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Signup extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //toolbar initialize
        toolbar = (Toolbar)findViewById(R.id.login_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //content UI initialize
        Button signup_confirm_Btn = (Button)findViewById(R.id.signup_confirm_Btn);
        final EditText username_regsiter = (EditText)findViewById(R.id.username_regsiter);
        final EditText password_regsiter = (EditText)findViewById(R.id.password_regsiter);
        final EditText email_register = (EditText)findViewById(R.id.email_register);
        signup_confirm_Btn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        String username = username_regsiter.getText().toString();
                        String password = password_regsiter.getText().toString();
                        String email = email_register.getText().toString();
                        System.out.println(username);
                        System.out.println(password);
                        System.out.println(email);
                        ParseUser user = new ParseUser();
                        user.setUsername(username);
                        user.setPassword(password);


                        ParseObject userInfo = new ParseObject("UserInfo");
                        userInfo.put("username", username);
                        userInfo.put("password", password);
                        userInfo.put("email", email);
                        userInfo.saveInBackground();

                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    intentToPhoto();
                                } else {
                                    Toast.makeText(getApplicationContext(),"Plese enter again", Toast.LENGTH_SHORT).show();
                                    username_regsiter.setText("");
                                    password_regsiter.setText("");
                                    email_register.setText("");
                                }
                            }
                        });
                    }
                }
        );
    }
    private void intentToPhoto(){
        Intent intent = new Intent(Signup.this,SignPhoto.class);
        startActivity(intent);
    }
}
