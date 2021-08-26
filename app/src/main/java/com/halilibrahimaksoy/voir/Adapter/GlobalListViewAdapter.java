package com.halilibrahimaksoy.voir.Adapter;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.halilibrahimaksoy.voir.Activity.UserDetails;
import com.halilibrahimaksoy.voir.MainActivity;
import com.halilibrahimaksoy.voir.Model.FeedItem;
import com.halilibrahimaksoy.voir.Model.UserItem;
import com.halilibrahimaksoy.voir.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sprylab.android.widget.TextureVideoView;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Halil ibrahim AKSOY on 1.11.2015.
 */
public class GlobalListViewAdapter extends ArrayAdapter<FeedItem> {
    private LayoutInflater layoutInflater;
    private List<FeedItem> feedItemList;
    private Context myContext;
    int stopPosition = 0;
    private ParseUser user;
    private String userId;
    private MediaPlayer mMediaPlayer;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;

    public GlobalListViewAdapter(Context context, int resource, List<FeedItem> objects) {
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
            view = layoutInflater.inflate(R.layout.global_list_item, parent, false);
            holder = new ViewHolder();
            holder.txtFeedUserName = (TextView) view.findViewById(R.id.txtFeedUserName);
            holder.txtFeedDate = (TextView) view.findViewById(R.id.txtFeedDate);
            holder.txtFeedDescription = (TextView) view.findViewById(R.id.txtFeedDescription);
            holder.txtFeedDislike = (TextView) view.findViewById(R.id.txtFeedDislike);
            holder.txtFeedLike = (TextView) view.findViewById(R.id.txtFeedLike);
            holder.txtFeedLocation = (TextView) view.findViewById(R.id.txtFeedLocation);
            holder.txtFeedTitle = (TextView) view.findViewById(R.id.txtFeedTitle);
            holder.imgFeedDislike = (ImageView) view.findViewById(R.id.imgFeedDislike);
            holder.imgFeedImage = (ImageView) view.findViewById(R.id.imgFeedImage);
            holder.imgFeedLike = (ImageView) view.findViewById(R.id.imgFeedLike);
            holder.imgFeedMenu = (ImageView) view.findViewById(R.id.imgFeedMenu);
            holder.imgFeedProfilImage = (CircleImageView) view.findViewById(R.id.imgFeedProfilImage);
            holder.imgFeedReplay = (ImageView) view.findViewById(R.id.imgFeedReplay);
            holder.vdvFeedVideo = (TextureVideoView) view.findViewById(R.id.vdvFeedVideo);//asdfl aslkjdfasd
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        final FeedItem item = feedItemList.get(position);
        holder.txtFeedTitle.setText(item.getTitle());
        holder.txtFeedLocation.setText(item.getLocation());
        holder.txtFeedLike.setText(item.getLike());
        holder.txtFeedDislike.setText(item.getDislike());
        holder.txtFeedDescription.setText(item.getDescription());
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

        long dateDifference = dateNow.getTime() - dateSend.getTime();

        dateDifference /= 1000;

        if ((dateDifference / (60 * 60 * 24 * 30 * 12)) >= 1)
            holder.txtFeedDate.setText(dateDifference / (60 * 60 * 24 * 30 * 12) + " yıl");
        else if (dateDifference / (60 * 60 * 24 * 30) >= 1)
            holder.txtFeedDate.setText(dateDifference / (60 * 60 * 24 * 30) + " ay");
        else if (dateDifference / (60 * 60 * 24) >= 1)
            holder.txtFeedDate.setText(dateDifference / (60 * 60 * 24) + " gün");
        else if (dateDifference / (60 * 60) >= 1)
            holder.txtFeedDate.setText(dateDifference / (60 * 60) + " saat");
        else if (dateDifference / (60) >= 1)
            holder.txtFeedDate.setText(dateDifference / (60) + " dakika");
        else
            holder.txtFeedDate.setText(dateDifference + " saniye");


        holder.imgFeedProfilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent = new Intent(getContext(), UserDetails.class);
                UserItem selectedUser = new UserItem();
                selectedUser.setId(item.getUserId());
                selectedUser.setUserName(item.getUserName());
                selectedUser.setUserProfilImageUrl(item.getUserProfilImageUrl());
                ıntent.putExtra("SelectUser", selectedUser);
                getContext().startActivity(ıntent);
            }
        });
        holder.txtFeedUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent = new Intent(getContext(), UserDetails.class);
                UserItem selectedUser = new UserItem();
                selectedUser.setId(item.getUserId());
                selectedUser.setUserName(item.getUserName());
                selectedUser.setUserProfilImageUrl(item.getUserProfilImageUrl());
                ıntent.putExtra("SelectUser", selectedUser);
                getContext().startActivity(ıntent);
            }
        });
        holder.txtFeedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getLocation() != null) {
                    Intent ıntent = new Intent(getContext(), MainActivity.class);
                    ıntent.putExtra(SearchManager.QUERY, item.getLocation());
                    ıntent.setAction(Intent.ACTION_SEARCH);
                    getContext().startActivity(ıntent);
                }
            }
        });
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

                AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
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

                AlertDialog alert = builder.create();
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
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {


                                    }
                                });
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
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                        alertDialogBuilder.setView(promptView);

                        final EditText editText = (EditText) promptView.findViewById(R.id.edtSikayet);

                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ParseObject complaint = new ParseObject("Voting");
                                        complaint.put("UserId", userId);
                                        complaint.put("Type", "Complaint");
                                        complaint.put("FeedId", feedId);
                                        complaint.put("FeedType", "Video");
                                        complaint.put("ComplaintText", editText.getText() + "");
                                        complaint.saveInBackground();

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

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();

                    } else if (object.getString("Type").equals("DisLike") || object == null) {

                        LayoutInflater layoutInflater = LayoutInflater.from(myContext);
                        View promptView = layoutInflater.inflate(R.layout.complaint_message, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                        alertDialogBuilder.setView(promptView);

                        final EditText editText = (EditText) promptView.findViewById(R.id.edtSikayet);

                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ParseObject complaint = new ParseObject("Voting");
                                        complaint.put("UserId", userId);
                                        complaint.put("Type", "Complaint");
                                        complaint.put("FeedId", feedId);
                                        complaint.put("FeedType", "Video");
                                        complaint.put("ComplaintText", editText.getText() + "");
                                        complaint.saveInBackground();

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

                        AlertDialog alert = alertDialogBuilder.create();
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
                object.deleteInBackground();
            }
        });
    }

    private class ViewHolder {
        public TextView txtFeedUserName, txtFeedLocation, txtFeedDate, txtFeedTitle, txtFeedDescription, txtFeedLike, txtFeedDislike;
        public ImageView imgFeedReplay, imgFeedImage, imgFeedLike, imgFeedDislike, imgFeedMenu;
        public CircleImageView imgFeedProfilImage;
        public TextureVideoView vdvFeedVideo;
    }
}
