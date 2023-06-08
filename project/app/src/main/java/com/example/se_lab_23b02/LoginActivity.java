package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        ServerHelper sHelper = ServerHelper.getInstance();
        sHelper.setActivity(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();


                if (!username.isEmpty() && !password.isEmpty()) {

                    try {
                        sHelper.loginUser(username, password, getApplicationContext());
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
