package com.halilibrahimaksoy.voir.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.halilibrahimaksoy.voir.Model.FeedItem;
import com.halilibrahimaksoy.voir.R;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sprylab.android.widget.TextureVideoView;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Halil ibrahim AKSOY on 29.12.2015.
 */
public class HeadLineListAdapter extends ArrayAdapter<FeedItem> {
    private LayoutInflater layoutInflater;
    private List<FeedItem> feedItemList;
    private Context myContext;
    int stopPosition = 0;
    private ParseUser user;
    private String userId;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
    long lastClickTime = 0;

    public HeadLineListAdapter(Context context, int resource, List<FeedItem> objects) {
        super(context, resource, objects);
        feedItemList = objects;

        layoutInflater = LayoutInflater.from(context);
        myContext = context;
        user = ParseUser.getCurrentUser();
        userId = user.getObjectId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.headline_list_item, parent, false);
            holder = new ViewHolder();
            holder.txtFeedUserName = (TextView) view.findViewById(R.id.txtFeedUserName);
            holder.txtFeedDate = (TextView) view.findViewById(R.id.txtFeedDate);
            holder.txtFeedDislike = (TextView) view.findViewById(R.id.txtFeedDislike);
            holder.txtFeedLike = (TextView) view.findViewById(R.id.txtFeedLike);
            holder.txtFeedLocation = (TextView) view.findViewById(R.id.txtFeedLocation);
            holder.imgFeedDislike = (ImageView) view.findViewById(R.id.imgFeedDislike);
            holder.imgFeedImage = (ImageView) view.findViewById(R.id.imgFeedImage);
            holder.imgFeedLike = (ImageView) view.findViewById(R.id.imgFeedLike);
            holder.imgFeedMenu = (ImageView) view.findViewById(R.id.imgFeedMenu);
            holder.imgFeedProfilImage = (CircleImageView) view.findViewById(R.id.imgFeedProfilImage);
            holder.imgFeedDate = (ImageView) view.findViewById(R.id.imgFeedReplay);
            holder.vdvFeedVideo = (TextureVideoView) view.findViewById(R.id.vdvFeedVideo);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        final FeedItem item = feedItemList.get(position);
        holder.txtFeedLocation.setText(item.getLocation());
        holder.txtFeedLike.setText(item.getLike());
        holder.txtFeedDislike.setText(item.getDislike());

        holder.txtFeedUserName.setText(item.getUserName());

        if (item.getUserProfilImageUrl() != null)
            Picasso.with(getContext()).load(item.getUserProfilImageUrl()).into(holder.imgFeedProfilImage);
        else
            holder.imgFeedProfilImage.setImageResource(R.drawable.ic_profile_default);


        holder.vdvFeedVideo.setVideoPath(item.getFeedVideoUrl());
        holder.vdvFeedVideo.setRotation(90.0f);
        Picasso.with(getContext()).load(item.getFeedImageUrl()).into(holder.imgFeedImage);


        holder.imgFeedImage.setRotation(90);
        holder.vdvFeedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.vdvFeedVideo.isPlaying()) {
                    stopPosition = holder.vdvFeedVideo.getCurrentPosition();
                    holder.vdvFeedVideo.pause();

                } else {
                    holder.vdvFeedVideo.seekTo(stopPosition);
                    holder.vdvFeedVideo.start();
                }
            }
        });


        holder.imgFeedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgFeedImage.setVisibility(View.INVISIBLE);
                holder.vdvFeedVideo.start();
            }
        });


        holder.vdvFeedVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            Log.e("click", "double click");

                            if (holder.vdvFeedVideo.isPlaying()) {
                                stopPosition = holder.vdvFeedVideo.getCurrentPosition();
                                holder.vdvFeedVideo.pause();

                            } else {
                                holder.vdvFeedVideo.seekTo(stopPosition);
                                holder.vdvFeedVideo.start();
                            }
                        } else {
                            Log.e("click", "single click");
                        }
                        lastClickTime = clickTime;
                        break;
                }
                return true;
            }
        });

        Date dateNow = new Date();
        Date dateSend;
        dateSend = item.getDate();

        float dateDifference = dateNow.getTime() - dateSend.getTime() + (1000 * 60 * 60 * 2);


        if (((int) (dateDifference / (1000 * 60 * 60 * 24 * 30))) >= 1)
            holder.txtFeedDate.setText(String.valueOf((int) (dateDifference) / (1000 * 60 * 60 * 24 * 30)) + " ay");
        else if (((int) (dateDifference / (1000 * 60 * 60 * 24))) >= 1)
            holder.txtFeedDate.setText(String.valueOf((int) (dateDifference) / (1000 * 60 * 60 * 24)) + " gün");
        else if (((int) (dateDifference / (1000 * 60 * 60))) >= 1)
            holder.txtFeedDate.setText(String.valueOf((int) (dateDifference) / (1000 * 60 * 60)) + " saat");
        else if (((int) (dateDifference / (1000 * 60))) >= 1)
            holder.txtFeedDate.setText(String.valueOf((int) (dateDifference) / (1000 * 60)) + " dakika");


        holder.imgFeedLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVoting(item.getId(), userId, "Like");
                holder.txtFeedLike.setText((Integer.parseInt(holder.txtFeedLike.getText().toString()) + 1) + "");
            }
        });
        holder.imgFeedDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVoting(item.getId(), userId, "DisLike");
                holder.txtFeedDislike.setText((Integer.parseInt(holder.txtFeedDislike.getText().toString()) + 1) + "");
            }
        });
        holder.imgFeedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.imgFeedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(myContext);
                builder.setMessage(item.getUserId().equals(userId) ? "Sil" : "Şikayet et !");
                builder.setCancelable(false)
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (item.getUserId().equals(userId)) {

                                    deleteFeed(item.getId());

                                } else {
                                    setVoting(item.getId(), userId, "Complaint");

                                }
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("İptal",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                android.app.AlertDialog alert = builder.create();
                alert.show();


            }
        });
        return view;
    }

    private void setVoting(final String feedId, final String userId, final String type) {


        ParseQuery<ParseObject> votingQuery = ParseQuery.getQuery("Voting");
        votingQuery.whereEqualTo("FeedId", feedId);
        votingQuery.whereEqualTo("UserId", userId);
        votingQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (type.equals("Like")) {
                    if (object == null) {
                        ParseObject likeQuery = new ParseObject("Voting");
                        likeQuery.put("FeedId", feedId);
                        likeQuery.put("UserId", userId);
                        likeQuery.put("Type", "Like");
                        likeQuery.put("FeedType", "Video");
                        likeQuery.saveInBackground();

                        ParseQuery<ParseObject> feedQuery = ParseQuery.getQuery("Feed");
                        feedQuery.getInBackground(feedId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                object.put("LikeCounter", ((Integer) object.getNumber("LikeCounter") + 1));
                                object.saveInBackground();
                            }
                        });

                    }
                } else if (type.equals("DisLike")) {

                    if (object == null) {
                        ParseObject disLikeQuery = new ParseObject("Voting");
                        disLikeQuery.put("FeedId", feedId);
                        disLikeQuery.put("UserId", userId);
                        disLikeQuery.put("Type", "DisLike");
                        disLikeQuery.put("FeedType", "Video");
                        disLikeQuery.saveInBackground();

                        ParseQuery<ParseObject> feedQuery = ParseQuery.getQuery("Feed");
                        feedQuery.getInBackground(feedId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                object.put("DisLikeCounter", ((Integer) object.getNumber("DisLikeCounter") + 1));
                                object.saveInBackground();
                            }
                        });
                    } else if (object.getString("Type").equals("Complaint")) {

                        ParseObject disLikeQuery = new ParseObject("Voting");
                        disLikeQuery.put("FeedId", feedId);
                        disLikeQuery.put("UserId", userId);
                        disLikeQuery.put("Type", "DisLike");
                        disLikeQuery.put("FeedType", "Video");

                        disLikeQuery.saveInBackground();

                        ParseQuery<ParseObject> feedQuery = ParseQuery.getQuery("Feed");
                        feedQuery.getInBackground(feedId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                object.put("DisLikeCounter", ((Integer) object.getNumber("DisLikeCounter") + 1));
                                object.saveInBackground();
                            }
                        });
                    }
                } else if (type.equals("Complaint")) {
                    if (object == null) {
                        LayoutInflater layoutInflater = LayoutInflater.from(myContext);
                        View promptView = layoutInflater.inflate(R.layout.complaint_message, null);
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(myContext);
                        alertDialogBuilder.setView(promptView);

                        final EditText editText = (EditText) promptView.findViewById(R.id.edtSikayet);

                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ParseObject like = new ParseObject("Voting");
                                        like.put("UserId", userId);
                                        like.put("Type", "Complaint");
                                        like.put("FeedId", feedId);
                                        like.put("FeedType", "Video");
                                        like.put("ComplaintText", editText.getText() + "");
                                        like.saveInBackground();

                                        ParseQuery<ParseObject> feedQuery = ParseQuery.getQuery("Feed");
                                        feedQuery.getInBackground(feedId, new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                object.put("ComplaintCounter", ((Integer) object.getNumber("ComplaintCounter") + 1));
                                                object.saveInBackground();
                                            }
                                        });
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

                    } else if (object.getString("Type").equals("DisLike") || object == null) {

                        LayoutInflater layoutInflater = LayoutInflater.from(myContext);
                        View promptView = layoutInflater.inflate(R.layout.complaint_message, null);
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(myContext);
                        alertDialogBuilder.setView(promptView);

                        final EditText editText = (EditText) promptView.findViewById(R.id.edtSikayet);

                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ParseObject like = new ParseObject("Voting");
                                        like.put("UserId", userId);
                                        like.put("Type", "Complaint");
                                        like.put("FeedId", feedId);
                                        like.put("FeedType", "Video");
                                        like.put("ComplaintText", editText.getText() + "");
                                        like.saveInBackground();

                                        ParseQuery<ParseObject> feedQuery = ParseQuery.getQuery("Feed");
                                        feedQuery.getInBackground(feedId, new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                object.put("ComplaintCounter", ((Integer) object.getNumber("ComplaintCounter") + 1));
                                                object.saveInBackground();
                                            }
                                        });
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

                }
            }
        });


    }

    private void deleteFeed(String feedId) {
        ParseQuery<ParseObject> deleteQuery = ParseQuery.getQuery("Feed");
        deleteQuery.getInBackground(feedId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {

                    }
                });
            }
        });
    }

    private class ViewHolder {
        public TextView txtFeedUserName, txtFeedLocation, txtFeedDate, txtFeedLike, txtFeedDislike;
        public ImageView imgFeedDate, imgFeedImage, imgFeedLike, imgFeedDislike, imgFeedMenu;
        public CircleImageView imgFeedProfilImage;
        public TextureVideoView vdvFeedVideo;
    }
}
