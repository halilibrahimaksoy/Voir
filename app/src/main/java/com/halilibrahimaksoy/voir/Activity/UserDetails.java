package com.halilibrahimaksoy.voir.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.Adapter.ViewPagerAdapter;
import com.halilibrahimaksoy.voir.Fragment.GlobalNew;
import com.halilibrahimaksoy.voir.Fragment.HeadLine;
import com.halilibrahimaksoy.voir.Model.UserItem;
import com.halilibrahimaksoy.voir.NonSwipeableViewPager;
import com.halilibrahimaksoy.voir.R;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Halil ibrahim AKSOY on 31.12.2015.
 */
public class UserDetails extends AppCompatActivity {
    private Toolbar tbUserDetails;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private String query = null;
    private UserItem userItem;
    private CircleImageView imgUserDetailsProfilImage;
    private TextView txtUserDetailsUserName, txtUserDetailsName, txtUserDetailsEmail, txtUserDetailsDescription;

    private boolean isLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        tbUserDetails = (Toolbar) findViewById(R.id.tbUserDetails);
        setSupportActionBar(tbUserDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        imgUserDetailsProfilImage = (CircleImageView) findViewById(R.id.imgUserDetailsProfilImage);
        txtUserDetailsUserName = (TextView) findViewById(R.id.txtUserDetailsUserName);
        txtUserDetailsName = (TextView) findViewById(R.id.txtUserDetailsName);
        txtUserDetailsEmail = (TextView) findViewById(R.id.txtUserDetailsEmail);
        txtUserDetailsDescription = (TextView) findViewById(R.id.txtUserDetailsDescription);

        userItem = (UserItem) getIntent().getSerializableExtra("SelectUser");
        isLoginUser = getIntent().getBooleanExtra("LoginUser", false);

        if (userItem.getUserProfilImageUrl() != null)
            Picasso.with(getApplicationContext()).load(userItem.getUserProfilImageUrl()).into(imgUserDetailsProfilImage);
        else
            imgUserDetailsProfilImage.setImageResource(R.drawable.ic_profile_default);
        txtUserDetailsUserName.setText(userItem.getUserName());
        txtUserDetailsName.setText(userItem.getName());

        final ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("objectId", userItem.getId());
        userParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    if (userItem.getUserProfilImageUrl() == null) {
                        if (object.getParseFile("UserProfilImage") != null)
                            userItem.setUserProfilImageUrl(object.getParseFile("UserProfilImage").getUrl());
                        if (userItem.getUserProfilImageUrl() != null)
                            Picasso.with(getApplicationContext()).load(userItem.getUserProfilImageUrl()).into(imgUserDetailsProfilImage);
                        else
                            imgUserDetailsProfilImage.setImageResource(R.drawable.ic_profile_default);
                    }
                    userItem.setMail(object.getEmail());
                    txtUserDetailsEmail.setText(userItem.getMail());
                    if (object.getString("Description") != null)
                        userItem.setDescription(object.getString("Description"));
                    else
                        userItem.setDescription("");
                    txtUserDetailsDescription.setText(userItem.getDescription());
                }
            }
        });


        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpagerglobal);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabsglobal);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        imgUserDetailsProfilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent = new Intent(getApplicationContext(), UserProfilImageShow.class);
                ıntent.putExtra("SelectUser", userItem.getUserProfilImageUrl());

                startActivity(ıntent);
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new GlobalNew(userItem.getId()), "");
        adapter.addFrag(new HeadLine(userItem.getId()), "");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.user_details_video_icon_selector);
        tabLayout.getTabAt(1).setIcon(R.drawable.user_details_headline_icon_selector);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                LogOut();
                break;
            case R.id.action_profil_update:
                ProfilUpdate();
                break;
            case R.id.action_profil_delete:
                ProfilDelete();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ProfilDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetails.this);
        builder.setTitle("Hesabı silmek istediğinize emin misiniz ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserDelete();
            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void ProfilUpdate() {
        Intent ıntent = new Intent(UserDetails.this, UserUpdate.class);
        ıntent.putExtra("LoginUser", userItem);
        finish();
        startActivity(ıntent);
    }

    private void UserDelete() {
        ParseUser parseUser = ParseUser.getCurrentUser();

        parseUser.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent(UserDetails.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", false);
                finish();
                ParseUser.logOut();
                startActivity(intent);
            }
        });
    }

    private void LogOut() {
        ParseUser.logOut();
        System.gc();
        finish();
        startActivity(new Intent(UserDetails.this, LoginActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isLoginUser) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.user_details_menu, menu);
        }
        return true;
    }
}
