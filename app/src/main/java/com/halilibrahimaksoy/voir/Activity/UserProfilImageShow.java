package com.halilibrahimaksoy.voir.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.halilibrahimaksoy.voir.R;
import com.squareup.picasso.Picasso;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class UserProfilImageShow extends AppCompatActivity {
    private String UserProfilImageUrl;
    private ImageView imgUserProfilImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil_image_show);
        imgUserProfilImage = (ImageView) findViewById(R.id.imgUserProfilImage);
        UserProfilImageUrl = getIntent().getStringExtra("SelectUser");
        if (UserProfilImageUrl != null)
            Picasso.with(getApplicationContext()).load(UserProfilImageUrl).into(imgUserProfilImage);
        else
            imgUserProfilImage.setImageResource(R.drawable.ic_profile_default);


    }
}
