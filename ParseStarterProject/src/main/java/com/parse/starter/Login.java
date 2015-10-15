package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signin_Btn = (Button)findViewById(R.id.signin_Btn);
        Button signup_Btn = (Button)findViewById(R.id.signup_Btn);
        final EditText usernameInput = (EditText)findViewById(R.id.username_input);
        final EditText passwordInput = (EditText)findViewById(R.id.password_input);
        signin_Btn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        String username = usernameInput.getText().toString();
                        String password = passwordInput.getText().toString();
                        //Do we need another thread
                      //  intentToMain();

                        ParseUser.logInInBackground(username, password,
                                new LogInCallback() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (parseUser != null) {
                                            intentToMain();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Plese enter again", Toast.LENGTH_SHORT).show();
                                            usernameInput.setText("");
                                            passwordInput.setText("");
                                        }
                                    }
                                });
                    }
                }
        );
        signup_Btn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        intentToSignup();
                    }
                }
        );

    }
    private void intentToMain(){
        Intent intent = new Intent(Login.this,MainActivity.class);
      //  intent.putExtra("Name","Jackson");
        startActivity(intent);
    }
    private void intentToSignup(){
        Intent intent = new Intent(Login.this,Signup.class);
        startActivity(intent);
    }
}

