package com.halilibrahimaksoy.voir.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.MainActivity;
import com.halilibrahimaksoy.voir.Model.UserItem;
import com.halilibrahimaksoy.voir.R;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Halil ibrahim AKSOY on 3.01.2016.
 */
public class UserUpdate extends AppCompatActivity {

    private Toolbar tbUserUpdate;
    private CircleImageView imgUserUpdateProfilImage;
    private EditText edtUserUpdateUsername, edtUserUpdateEmail, edtUserUpdateNewParssword, edtUserUpdateNewParssword2, edtUserUpdateName, edtUserUpdateDescription;
    private Button btnUserUpdateSave;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;
    private UserItem loginUser;
    Bitmap userNewProfilImage = null;
    private ByteArrayOutputStream byteArrayOutputStream;
    private UserItem updateUser;
    ProgressDialog pd;
    boolean nameChange, userNameChange, mailChange, descriptionChange, passwordChange, isUserNameUsing, isEmailUsing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        tbUserUpdate = (Toolbar) findViewById(R.id.tbUserUpdate);

        setSupportActionBar(tbUserUpdate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        pd = new ProgressDialog(UserUpdate.this);
        pd.setCancelable(false);


        setUiElement();
        loginUser = (UserItem) getIntent().getSerializableExtra("LoginUser");

        updateUser = new UserItem();
        getUserInformation(loginUser);
        setUpdateUserInformation(updateUser, loginUser);
        booleanReset();

        imgUserUpdateProfilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        btnUserUpdateSave.setOnClickListener(new View.OnClickListener() {


                                                 @Override
                                                 public void onClick(View v) {
                                                     updateUser.setUserName(edtUserUpdateUsername.getText().toString());
                                                     updateUser.setName(edtUserUpdateName.getText().toString());
                                                     updateUser.setMail(edtUserUpdateEmail.getText().toString());
                                                     updateUser.setDescription(edtUserUpdateDescription.getText().toString());
                                                     updateUser.setPassword(edtUserUpdateNewParssword.getText().toString());

                                                     if (!loginUser.getUserName().equals(updateUser.getUserName())) {
                                                         if (updateUser.getUserName().isEmpty() || updateUser.getUserName().length() < 5)
                                                             Toast.makeText(getApplicationContext(), R.string.error_incorrect_username, Toast.LENGTH_SHORT).show();
                                                         else {
                                                             usernameCheck(updateUser.getUserName());
                                                             if (!isUserNameUsing)
                                                                 userNameChange = true;
                                                         }
                                                     }

                                                     if (!loginUser.getMail().equals(updateUser.getMail())) {
                                                         if (updateUser.getMail().isEmpty() || updateUser.getMail().length() < 4 || !updateUser.getMail().contains("@") || !updateUser.getMail().contains("."))
                                                             Toast.makeText(getApplicationContext(), R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
                                                         else {
                                                             emailCheck(updateUser.getMail());
                                                             if (!isEmailUsing)
                                                                 mailChange = true;
                                                         }
                                                     }
                                                     if (!loginUser.getName().equals(updateUser.getName())) {
                                                         if (updateUser.getName().length() > 40)
                                                             Toast.makeText(getApplicationContext(), "Ad soyad en falza 40 karakter olmalıdır !", Toast.LENGTH_SHORT).show();
                                                         else {

                                                             nameChange = true;
                                                         }

                                                     }
                                                     if (!loginUser.getDescription().equals(updateUser.getDescription())) {
                                                         if (updateUser.getDescription().length() > 100)
                                                             Toast.makeText(getApplicationContext(), "Açıklama en falza 100 karakter olmalıdır !", Toast.LENGTH_SHORT).show();
                                                         else {

                                                             descriptionChange = true;
                                                         }

                                                     }
                                                     if (!edtUserUpdateNewParssword.getText().toString().isEmpty()) {
                                                         if (edtUserUpdateNewParssword.getText().toString().equals(edtUserUpdateNewParssword2.getText().toString())) {
                                                             if (updateUser.getPassword().length() > 5)
                                                                 passwordChange = true;
                                                             else
                                                                 Toast.makeText(getApplicationContext(), R.string.error_invalid_password6, Toast.LENGTH_SHORT).show();
                                                         } else
                                                             Toast.makeText(getApplicationContext(), "Şifreler uyuşmuyor !", Toast.LENGTH_SHORT).show();
                                                     }


                                                     if ((nameChange || userNameChange || mailChange || descriptionChange || passwordChange || byteArrayOutputStream != null) && !isUserNameUsing && !isEmailUsing)

                                                     {
                                                         final ParseQuery<ParseUser> updateQuery = ParseUser.getQuery();
                                                         final boolean finalUserNameChange = userNameChange;
                                                         final boolean finalMailChange = mailChange;
                                                         final boolean finalNameChange = nameChange;
                                                         final boolean finalDescriptionChange = descriptionChange;
                                                         updateQuery.getInBackground(loginUser.getId(), new GetCallback<ParseUser>() {
                                                             @Override
                                                             public void done(ParseUser object, ParseException e) {

                                                                 pd.setMessage("Bilgiler güncelleniyor...");
                                                                 pd.show();
                                                                 if (finalUserNameChange)
                                                                     object.setUsername(updateUser.getUserName());
                                                                 if (finalMailChange)
                                                                     object.setEmail(updateUser.getMail());
                                                                 if (finalNameChange)
                                                                     object.put("Name", updateUser.getName());
                                                                 if (finalDescriptionChange)
                                                                     object.put("Description", updateUser.getDescription());
                                                                 if (passwordChange)
                                                                     object.setPassword(updateUser.getPassword());

                                                                 if (byteArrayOutputStream != null) {
                                                                     byte[] image = byteArrayOutputStream.toByteArray();
                                                                     ParseFile file = new ParseFile(loginUser.getId() + ".jpg", image);
                                                                     // Upload the image into Parse Cloud
                                                                     file.saveInBackground();
                                                                     object.put("UserProfilImage", file);
                                                                 }

                                                                 object.saveInBackground(new SaveCallback() {
                                                                     @Override
                                                                     public void done(ParseException e) {
                                                                         pd.dismiss();
                                                                         if (e == null) {
                                                                             Toast.makeText(getApplicationContext(), "Bilgileriniz güncellendi", Toast.LENGTH_SHORT).show();
                                                                             loginUser = updateUser;
                                                                             booleanReset();
                                                                         }


                                                                     }
                                                                 });
                                                             }
                                                         });
                                                     }
                                                 }
                                             }

        );
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();


                Bitmap myBitmap = BitmapFactory.decodeFile(picturePath);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 2, myBitmap.getHeight() / 2, true);


                Matrix matrix = new Matrix();
                matrix.postRotate(90); // anti-clockwise by 90 degrees

                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                //    img1.setImageBitmap(rotatedBitmap);

                byteArrayOutputStream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);

                //   userNewProfilImage = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                imgUserUpdateProfilImage.setImageBitmap(rotatedBitmap);
                updateUser.setUserProfilImageUrl(null);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Seçilen fotoğraf uygun değil !", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void booleanReset() {
        nameChange = userNameChange = mailChange = passwordChange = descriptionChange = isUserNameUsing = isEmailUsing = false;

    }

    private boolean usernameCheck(String username) {
        pd.setMessage("Kullanıcı adı kontrol ediliyor ...");
        pd.show();
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", username);

        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {

            @Override
            public void done(ParseUser object, ParseException e) {

                if (object != null) {
                    isUserNameUsing = true;
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Bu kullanıcı adı kullanılmaktadır !", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return false;
    }

    private boolean emailCheck(String email) {
        pd.setMessage("Email adresi kontrol ediliyor ...");
        pd.show();
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("email", email);
        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (object != null) {
                    isEmailUsing = true;
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Bu email adresi kullanılmaktadır !", Toast.LENGTH_SHORT).show();

                }


            }
        });

        return false;
    }

    private void setUpdateUserInformation(UserItem updateUser, UserItem loginUser) {
        //   updateUser=new UserItem();
        updateUser.setId(loginUser.getId());
        updateUser.setUserName(loginUser.getUserName());
        updateUser.setName(loginUser.getName());
        updateUser.setPassword(loginUser.getPassword());
        updateUser.setDescription(loginUser.getDescription());
        updateUser.setMail(loginUser.getMail());
        updateUser.setUserLikeCounter(loginUser.getUserLikeCounter());
        updateUser.setUserProfilImageUrl(loginUser.getUserProfilImageUrl());
    }

    private void setUiElement() {
        imgUserUpdateProfilImage = (CircleImageView) findViewById(R.id.imgUserUpdateProfilImage);
        edtUserUpdateUsername = (EditText) findViewById(R.id.edtUserUpdateUsername);
        edtUserUpdateEmail = (EditText) findViewById(R.id.edtUserUpdateEmail);
        edtUserUpdateNewParssword = (EditText) findViewById(R.id.edtUserUpdateNewParssword);
        edtUserUpdateNewParssword2 = (EditText) findViewById(R.id.edtUserUpdateNewParssword2);
        edtUserUpdateName = (EditText) findViewById(R.id.edtUserUpdateName);
        edtUserUpdateDescription = (EditText) findViewById(R.id.edtUserUpdateDescription);
        btnUserUpdateSave = (Button) findViewById(R.id.btnUserUpdateSave);
    }

    private void getUserInformation(UserItem loginUser) {

        if (loginUser.getUserProfilImageUrl() != null)
            Picasso.with(getApplicationContext()).load(loginUser.getUserProfilImageUrl()).into(imgUserUpdateProfilImage);
        else
            imgUserUpdateProfilImage.setImageResource(R.drawable.ic_profile_default);

        edtUserUpdateUsername.setText(loginUser.getUserName());
        edtUserUpdateEmail.setText(loginUser.getMail());
        edtUserUpdateName.setText(loginUser.getName());
        edtUserUpdateDescription.setText(loginUser.getDescription());

    }

    private void getUserDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), UserDetails.class);
        intent.putExtra("SelectUser", loginUser);
        intent.putExtra("LoginUser", true);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //     onBackPressed();
                getUserDetailsActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
