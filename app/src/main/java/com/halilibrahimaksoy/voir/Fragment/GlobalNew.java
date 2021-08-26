package com.halilibrahimaksoy.voir.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.Adapter.GlobalListViewAdapter;
import com.halilibrahimaksoy.voir.MainActivity;
import com.halilibrahimaksoy.voir.Model.FeedItem;
import com.halilibrahimaksoy.voir.Model.HeadLineItem;
import com.halilibrahimaksoy.voir.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalNew extends Fragment {
    ListView lsvGlobalNew;
    List<FeedItem> listGlobal = new ArrayList<FeedItem>();
    GlobalListViewAdapter globalListViewAdapter;
    private String query;

    public GlobalNew(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_global_new, container, false);
        lsvGlobalNew = (ListView) view.findViewById(R.id.lsvGlobalNew);
        globalListViewAdapter = new GlobalListViewAdapter(getContext(), 0, listGlobal);
        lsvGlobalNew.setAdapter(globalListViewAdapter);

        return view;
    }


    private class getGlobalList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getList(query);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //  super.onPostExecute(aVoid);
            globalListViewAdapter.notifyDataSetChanged();
        }
    }

    private void getList(String query) {
        ParseQuery<ParseObject> globalNewQuery;
        if (query != null) {
            ParseQuery title = new ParseQuery("Feed");
            title.whereContains("Title", query);
            title.whereEqualTo("Category", "Global");

            ParseQuery description = new ParseQuery("Feed");
            description.whereContains("Description", query);
            description.whereEqualTo("Category", "Global");

            ParseQuery location = new ParseQuery("Feed");
            location.whereContains("LocationString", query);
            location.whereEqualTo("Category", "Global");

            ParseQuery sender = new ParseQuery("Feed");
            sender.whereEqualTo("UserId", query);

            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(title);
            queries.add(description);
            queries.add(location);
            queries.add(sender);
            globalNewQuery = ParseQuery.or(queries);

        } else {

            globalNewQuery = ParseQuery.getQuery("Feed");
            globalNewQuery.whereEqualTo("Category", "Global");
        }


        globalNewQuery.orderByDescending("createdAt");
        globalNewQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    listGlobal.clear();
                    globalListViewAdapter.clear();
                    for (final ParseObject feed : objects) {
                        final FeedItem feedItem = new FeedItem();
                        feedItem.setId(feed.getObjectId());
                        feedItem.setCategori(feed.getString("Categori"));
                        feedItem.setDate(feed.getCreatedAt());
                        feedItem.setTitle(feed.getString("Title"));
                        feedItem.setDescription(feed.getString("Description"));

                        feedItem.setUserId(feed.getString("UserId"));
                        feedItem.setFeedImageUrl(feed.getParseFile("FeedImage").getUrl());
                        feedItem.setFeedVideoUrl(feed.getParseFile("FeedVideo").getUrl());
                        feedItem.setLike(feed.getNumber("LikeCounter") + "");
                        feedItem.setDislike(feed.getNumber("DisLikeCounter") + "");
                        feedItem.setLocation(feed.getString("LocationString"));

                        getUserInformation(feedItem);
                        listGlobal.add(feedItem);
                        //  globalListViewAdapter.notifyDataSetChanged();


                    }


                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu !", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume","OnResume cagırıldı globalnew");
        new getGlobalList().execute((Void) null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        new getGlobalList().execute((Void) null);
    }

    private void getUserInformation(final FeedItem feedItem) {
        ParseQuery<ParseUser> feedUserQuery = ParseUser.getQuery();
        feedUserQuery.whereEqualTo("objectId", feedItem.getUserId());
        feedUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    feedItem.setUserName(object.getString("username"));
                    feedItem.setUserProfilImageUrl(object.getParseFile("UserProfilImage").getUrl());

                    globalListViewAdapter.notifyDataSetChanged();

                } else {
                }
            }
        });

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("onAttach", "OnAttach çagrıldı globalNew");
        new getGlobalList().execute((Void) null);
    }
}
