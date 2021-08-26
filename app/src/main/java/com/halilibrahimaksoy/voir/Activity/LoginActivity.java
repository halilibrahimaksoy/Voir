package com.halilibrahimaksoy.voir.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.MainActivity;
import com.halilibrahimaksoy.voir.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText edtLoginUsername;
    private EditText edtLoginParssword;
    private Button btnLoginEnter;
    private TextView txtLoginGoSingIn;

    private String username, password;

    private ParseUser loginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra("EXIT", false)) {
          //  finish();
            finishAffinity();

        } else {

            loginUser = ParseUser.getCurrentUser();
            if (loginUser != null) {
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Giriş Yapılıyor..");

            edtLoginUsername = (EditText) findViewById(R.id.edtLoginUsername);
            edtLoginParssword = (EditText) findViewById(R.id.edtLoginParssword);
            btnLoginEnter = (Button) findViewById(R.id.btnLoginEnter);
            txtLoginGoSingIn = (TextView) findViewById(R.id.txtLoginGoSingIn);


            btnLoginEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    username = edtLoginUsername.getText().toString();
                    password = edtLoginParssword.getText().toString();

                    if (username.isEmpty() || username.length() < 5)
                        Toast.makeText(getApplicationContext(), R.string.error_invalid_username, Toast.LENGTH_SHORT).show();
                    else if (password.isEmpty() || password.length() < 6)
                        Toast.makeText(getApplicationContext(), R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
                    else {
                        pd.show();
                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    pd.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                } else {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                }
            });


            txtLoginGoSingIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, SingInActivity.class));
                }
            });

        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}