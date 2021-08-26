package com.halilibrahimaksoy.voir.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.Adapter.HeadLineListAdapter;
import com.halilibrahimaksoy.voir.Model.FeedItem;
import com.halilibrahimaksoy.voir.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Halil ibrahim AKSOY on 26.12.2015.
 */
public class HeadLineList extends AppCompatActivity {

    private Toolbar tbSpecialList;
    private ListView lsvSpecialList;
    private List<FeedItem> feedItemList = new ArrayList<>();
    private HeadLineListAdapter headLineListAdapter;
    private String title;
    private String Id;
    private FloatingActionButton fabSpecialList;

    private ParseUser user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headline_list);


        tbSpecialList = (Toolbar) findViewById(R.id.tbSpecialList);

        setSupportActionBar(tbSpecialList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));

        fabSpecialList = (FloatingActionButton) findViewById(R.id.fabSpecialList);
        lsvSpecialList = (ListView) findViewById(R.id.lsvSpecialList);
        headLineListAdapter = new HeadLineListAdapter(HeadLineList.this, 0, feedItemList);
        lsvSpecialList.setAdapter(headLineListAdapter);


        userId = getIntent().getStringExtra("UserId");

        fabSpecialList.setVisibility(View.VISIBLE);
        final String category = getIntent().getStringExtra("CategoryId");
        Id = getIntent().getStringExtra("SelectedHeadLineId");
        ParseQuery<ParseObject> globalNewQuery = ParseQuery.getQuery("Feed");
        globalNewQuery.whereEqualTo("Category", category);
        getList(globalNewQuery);

        user = ParseUser.getCurrentUser();

        fabSpecialList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent = new Intent(HeadLineList.this, FeedRecord.class);
                ıntent.putExtra("HeadLine", category);
                startActivity(ıntent);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        final String category = getIntent().getStringExtra("CategoryId");
        Id = getIntent().getStringExtra("SelectedHeadLineId");
        ParseQuery<ParseObject> globalNewQuery = ParseQuery.getQuery("Feed");
        globalNewQuery.whereEqualTo("Category", category);
        getList(globalNewQuery);

    }

    private void getList(ParseQuery<ParseObject> globalNewQuery) {
        //   Log.d("getList", "get list çağrıldı");
        globalNewQuery.orderByDescending("createdAt");
        globalNewQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    feedItemList.clear();
                    headLineListAdapter.clear();
                    for (final ParseObject feed : objects) {
                        final FeedItem feedItem = new FeedItem();
                        feedItem.setId(feed.getObjectId());
                        feedItem.setCategori(feed.getString("Categori"));
                        feedItem.setDate(feed.getCreatedAt());
                        feedItem.setUserId(feed.getString("UserId"));
                        feedItem.setFeedImageUrl(feed.getParseFile("FeedImage").getUrl());
                        feedItem.setFeedVideoUrl(feed.getParseFile("FeedVideo").getUrl());
                        feedItem.setLike(feed.getNumber("LikeCounter") + "");
                        feedItem.setDislike(feed.getNumber("DisLikeCounter") + "");
                        feedItem.setLocation(feed.getString("LocationString"));

                        getUserInformation(feedItem);
                        feedItemList.add(feedItem);
                        headLineListAdapter.notifyDataSetChanged();


                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Bir Hata Oluştu !", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void getUserInformation(final FeedItem feedItem) {
        ParseQuery<ParseUser> feedUserQuery = ParseUser.getQuery();
        feedUserQuery.whereEqualTo("objectId", feedItem.getUserId());
        feedUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    feedItem.setUserName(object.getUsername());
                    feedItem.setUserProfilImageUrl(object.getParseFile("UserProfilImage").getUrl());

                    headLineListAdapter.notifyDataSetChanged();

                } else {
                    Log.e("hata", "USER_INFORMATION--" + e.getMessage());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        if (userId.equals(user.getObjectId()))
            menuInflater.inflate(R.menu.headline_delete_menu, menu);
        else
            menuInflater.inflate(R.menu.headline_complain_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void deleteHeadLine(String objectId) {
        ParseQuery<ParseObject> deleteQuery = ParseQuery.getQuery("HeadLine");
        deleteQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        onBackPressed();
                    }
                });
            }
        });
    }

    private void complaint() {
        LayoutInflater layoutInflater = LayoutInflater.from(HeadLineList.this);
        View promptView = layoutInflater.inflate(R.layout.complaint_message, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(HeadLineList.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edtSikayet);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParseObject like = new ParseObject("Voting");
                        like.put("UserId", userId);
                        like.put("Type", "Complaint");
                        like.put("FeedId", Id);
                        like.put("FeedType", "HeadLine");
                        like.put("ComplaintText", editText.getText() + "");
                        like.saveInBackground();
                    }
                })
                .setNegativeButton("İptal",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.headline_delete:
                deleteHeadLine(Id);
                break;
            case R.id.headline_complaint:
                complaint();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
