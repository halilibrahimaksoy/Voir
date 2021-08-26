package com.halilibrahimaksoy.voir.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.Adapter.HeadLineAdapter;
import com.halilibrahimaksoy.voir.Model.HeadLineItem;
import com.halilibrahimaksoy.voir.Model.UserItem;
import com.halilibrahimaksoy.voir.R;
import com.halilibrahimaksoy.voir.Activity.HeadLineList;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class HeadLine extends Fragment {
    private ListView lsvHeadLine;
    private List<HeadLineItem> headLineItemList = new ArrayList<>();
    private HeadLineAdapter headLineAdapter;
    private String query = null;
    private FloatingActionButton fabHeadLineAdd;

    private ParseUser parseUser;
    private UserItem loginUser;

    public HeadLine() {

    }

    public HeadLine(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_headline, container, false);
        lsvHeadLine = (ListView) view.findViewById(R.id.lsvHeadLine);

        headLineAdapter = new HeadLineAdapter(getContext(), 0, headLineItemList);

        lsvHeadLine.setAdapter(headLineAdapter);


        lsvHeadLine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ıntent = new Intent(getActivity().getApplicationContext(), HeadLineList.class);
                ıntent.putExtra("Type", "HeadLine");
                ıntent.putExtra("CategoryId", headLineItemList.get(position).getId());

                ıntent.putExtra("Title", headLineItemList.get(position).getHeadLineText());
                ıntent.putExtra("UserId", headLineItemList.get(position).getUserId());
                ıntent.putExtra("SelectedHeadLineId", headLineItemList.get(position).getId());
                startActivity(ıntent);
            }
        });
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setMessage("HeadLine ekleniyor ...");
        fabHeadLineAdd = (FloatingActionButton) view.findViewById(R.id.fabHeadLineAdd);
        fabHeadLineAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.headline_add_view, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptView);
                alertDialogBuilder.setTitle("Yeni Bir HeadLine Ekle");

                final EditText editText = (EditText) promptView.findViewById(R.id.edtHeadLineAddTitle);


                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!editText.getText().toString().isEmpty() || editText.getText().length() > 5) {
                                    pd.show();
                                    ParseObject headline = new ParseObject("HeadLine");
                                    headline.put("UserId", loginUser.getId());
                                    headline.put("Title", editText.getText().toString());
                                    headline.put("FeedCounter", 0);
                                    headline.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            pd.dismiss();

                                            if (e != null)
                                                Toast.makeText(getContext(), "Bir hata oluştu !", Toast.LENGTH_SHORT).show();
                                            else
                                                new getHeadLineList().execute((Void) null);
                                        }

                                    });
                                } else {
                                    Toast.makeText(getContext(), "Bir HeadLine en az 5 harfli olmalıdır !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("İptal",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.e("onresumw", "OnResume cagırıldı HeadLine");
        new getHeadLineList().execute((Void) null);
    }


    private class getHeadLineList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getList(query);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            headLineAdapter.notifyDataSetChanged();
        }
    }

    private void getList(String query) {

        ParseQuery<ParseObject> headLineQuery;
        if (query != null) {
            ParseQuery title = new ParseQuery("HeadLine");
            title.whereContains("Title", query);
            ParseQuery sender = new ParseQuery("HeadLine");
            sender.whereEqualTo("UserId", query);
            List<ParseQuery<ParseObject>> queries = new ArrayList<>();

            queries.add(title);
            queries.add(sender);
            headLineQuery = ParseQuery.or(queries);

        } else {

            headLineQuery = ParseQuery.getQuery("HeadLine");
        }
        headLineQuery.orderByDescending("createdAt");
        headLineQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    headLineItemList.clear();
                    for (ParseObject headLine : objects) {

                        final HeadLineItem headLineItem = new HeadLineItem();

                        headLineItem.setId(headLine.getObjectId());
                        headLineItem.setUserId(headLine.getString("UserId"));
                        headLineItem.setFeedCount((Integer) headLine.getNumber("FeedCounter"));
                        headLineItem.setHeadLineText(headLine.getString("Title"));
                        headLineItem.setDate(headLine.getCreatedAt());
                        headLineItemList.add(headLineItem);
                        headLineAdapter.notifyDataSetChanged();

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.e("onAttach", "OnAttach çagrıldı headline");
        new getHeadLineList().execute((Void) null);
        parseUser = ParseUser.getCurrentUser();
        loginUser = new UserItem();
        if (parseUser != null)
            loginUser.setId(parseUser.getObjectId());
    }
}
