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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.Adapter.UserListAdapter;
import com.halilibrahimaksoy.voir.Model.UserItem;
import com.halilibrahimaksoy.voir.R;
import com.halilibrahimaksoy.voir.Activity.UserDetails;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Trending extends Fragment {
    private ListView lsvUserList;
    private List<UserItem> userItemList = new ArrayList<>();
    private UserListAdapter userListAdapter;

    private String query = null;
    private UserItem loginUser;

    private CircleImageView imgLoginUserProfilImage;
    private TextView txtLoginUserUsername, txtLoginUserName, txtLoginUserLike;

    private RelativeLayout rltvLoginUser;

    public Trending() {

    }

    public Trending(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_trending, container, false);
        lsvUserList = (ListView) view.findViewById(R.id.lsvUserList);

        imgLoginUserProfilImage = (CircleImageView) view.findViewById(R.id.imgLoginUserProfilImage);
        txtLoginUserUsername = (TextView) view.findViewById(R.id.txtLoginUserUsername);
        txtLoginUserName = (TextView) view.findViewById(R.id.txtLoginUserName);
        txtLoginUserLike = (TextView) view.findViewById(R.id.txtLoginUserLike);

        rltvLoginUser = (RelativeLayout) view.findViewById(R.id.rltvLoginUser);
        userListAdapter = new UserListAdapter(getContext(), 0, userItemList);
        lsvUserList.setAdapter(userListAdapter);

        Log.e("onAttach", "OnCreate çagrıldı trending");

        getLoginUserInformation();


        lsvUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), UserDetails.class);
                intent.putExtra("SelectUser", userItemList.get(position));
                startActivity(intent);
            }
        });

        rltvLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), UserDetails.class);
                intent.putExtra("SelectUser", loginUser);
                intent.putExtra("LoginUser", true);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getLoginUserInformation() {
        getUserLikeCounter(loginUser, false);
        if (loginUser.getUserProfilImageUrl() != null)
            Picasso.with(getContext()).load(loginUser.getUserProfilImageUrl()).into(imgLoginUserProfilImage);
        else
            imgLoginUserProfilImage.setImageResource(R.drawable.ic_profile_default);
        txtLoginUserUsername.setText(loginUser.getUserName());
        txtLoginUserName.setText(loginUser.getName());
    }

    private void setLoginUserInformation() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            loginUser = new UserItem();

            loginUser.setId(currentUser.getObjectId());
            loginUser.setUserName(currentUser.getUsername());
            if (currentUser.getString("Name") != null)
                loginUser.setName(currentUser.getString("Name"));
            else
                loginUser.setName("");
            if (currentUser.getParseFile("UserProfilImage") != null)
                loginUser.setUserProfilImageUrl(currentUser.getParseFile("UserProfilImage").getUrl());

        }
    }

    private class getUserList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getList(query);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            userListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresumw", "OnResume cagırıldı Trending");
        setLoginUserInformation();
        getLoginUserInformation();

    }

    private void getList(String query) {

        ParseQuery<ParseUser> userParseQuery;
        if (query != null) {
            ParseQuery<ParseUser> username = ParseUser.getQuery();
            username.whereContains("username", query);
            ParseQuery<ParseUser> name = ParseUser.getQuery();
            name.whereContains("Name", query);
            ParseQuery<ParseUser> email = ParseUser.getQuery();
            email.whereContains("email", query);
            ParseQuery<ParseUser> description = ParseUser.getQuery();
            description.whereContains("Description", query);

            List<ParseQuery<ParseUser>> queries = new ArrayList<>();
            queries.add(username);
            queries.add(name);
            queries.add(email);
            queries.add(description);

            userParseQuery = ParseQuery.or(queries);
        } else {
            userParseQuery = ParseUser.getQuery();
        }

        userParseQuery.orderByDescending("createdAt");
        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    userItemList.clear();
                    for (ParseUser parseUser : objects) {


                        final UserItem userItem = new UserItem();

                        userItem.setId(parseUser.getObjectId());

                        if (userItem.getId().equals(loginUser.getId()))
                            continue;
                        userItem.setUserName(parseUser.getUsername());

                        if (parseUser.getString("Name") != null)
                            userItem.setName(parseUser.getString("Name"));
                        else
                            userItem.setName("");


                        if (parseUser.getParseFile("UserProfilImage") != null)
                            userItem.setUserProfilImageUrl(parseUser.getParseFile("UserProfilImage").getUrl());

                        getUserLikeCounter(userItem, true);
                        userItemList.add(userItem);
                        //  userListAdapter.notifyDataSetChanged();
                    }


                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserLikeCounter(final UserItem userItem, final boolean isList) {

        ParseQuery<ParseObject> likeQuery = ParseQuery.getQuery("Feed");
        likeQuery.whereEqualTo("UserId", userItem.getId());
        likeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                int count = 0;
                if (e == null)
                    for (ParseObject object : objects) {
                        count += object.getInt("LikeCounter");
                    }
                userItem.setUserLikeCounter(count);
                if (isList) {
                    Collections.sort(userItemList, new CustomComparator());
                    Collections.reverse(userItemList);
                    userListAdapter.notifyDataSetChanged();
                } else

                    txtLoginUserLike.setText(userItem.getUserLikeCounter() + "");
            }
        });

    }

    public class CustomComparator implements Comparator<UserItem> {
        @Override
        public int compare(UserItem o1, UserItem o2) {
            int returnVal;
            if (o1.getUserLikeCounter() < o2.getUserLikeCounter())
                returnVal = -1;
            else if (o1.getUserLikeCounter() > o2.getUserLikeCounter())
                returnVal = 1;
            else
                returnVal = 0;
            return returnVal;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setLoginUserInformation();


        Log.e("onAttach", "OnAttach çagrıldı trending");
        new getUserList().execute((Void) null);
    }
}
