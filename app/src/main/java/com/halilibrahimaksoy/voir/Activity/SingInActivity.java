package com.halilibrahimaksoy.voir.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.MainActivity;
import com.halilibrahimaksoy.voir.R;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Halil ibrahim AKSOY on 3.01.2016.
 */
public class SingInActivity extends AppCompatActivity {

    private EditText edtSingInUsername;
    private EditText edtSingInEmail;
    private EditText edtSingInParssword;
    private Button btnSingInEnter;

    private String username, password, email;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        pd = new ProgressDialog(SingInActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Kayıt Yapılıyor..");

        edtSingInUsername = (EditText) findViewById(R.id.edtSingInUsername);
        edtSingInEmail = (EditText) findViewById(R.id.edtSingInEmail);
        edtSingInParssword = (EditText) findViewById(R.id.edtSingInParssword);
        btnSingInEnter = (Button) findViewById(R.id.btnSingInEnter);

        btnSingInEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = edtSingInUsername.getText().toString();
                email = edtSingInEmail.getText().toString();
                password = edtSingInParssword.getText().toString();

                if (username.isEmpty() || username.length() < 5)
                    Toast.makeText(getApplicationContext(), R.string.error_incorrect_username, Toast.LENGTH_SHORT).show();

                else if (email.isEmpty() || email.length() < 4 || !email.contains("@") || !email.contains("."))
                    Toast.makeText(getApplicationContext(), R.string.error_invalid_email, Toast.LENGTH_SHORT).show();

                else if (password.isEmpty() || password.length() < 6)
                    Toast.makeText(getApplicationContext(), R.string.error_invalid_password6, Toast.LENGTH_SHORT).show();

                else {
                    usernameCheck(username);

                }


            }
        });

    }

    private void singIn() {
        pd.show();
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                pd.dismiss();
                                startActivity(new Intent(SingInActivity.this, MainActivity.class));

                            }
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Şu anda kayıt gerçekleştirilemiyor !", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void usernameCheck(String username) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", username);
        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (object != null)
                    Toast.makeText(getApplicationContext(), "Bu kullanıcı adı kullanılmaktadır !", Toast.LENGTH_SHORT).show();
                else
                    emailCheck(email);
            }
        });

    }

    private void emailCheck(String email) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("email", email);

        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (object != null)
                    Toast.makeText(getApplicationContext(), "Bu email adresi kullanılmaktadır !", Toast.LENGTH_SHORT).show();
                else
                    singIn();
            }
        });

    }
}
