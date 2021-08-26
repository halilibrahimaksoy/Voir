package com.halilibrahimaksoy.voir.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.halilibrahimaksoy.voir.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sprylab.android.widget.TextureVideoView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Halil ibrahim AKSOY on 10.01.2016.
 */
public class FeedSend extends AppCompatActivity {
    private String videoName;
    private String videoPath;
    private byte[] totalVideoByteArray;
    private ParseUser parseUser;
    private String userId;
    int stopPosition = 0;
    private Toolbar tbFeedSend;
    private TextureVideoView vdvFeedSend;
    private EditText edtFeedSendTitle;
    private EditText edtFeedSendDescription;
    private ImageView imgFeedSave;

    private TextInputLayout tılFeedSendTitle, tılFeedSendDesciption;
    private String title = "";
    private String description = "";


    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_send);

        tbFeedSend = (Toolbar) findViewById(R.id.tbFeedSend);
        setSupportActionBar(tbFeedSend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vdvFeedSend = (TextureVideoView) findViewById(R.id.vdvFeedSend);
        edtFeedSendTitle = (EditText) findViewById(R.id.edtFeedSendTitle);
        edtFeedSendDescription = (EditText) findViewById(R.id.edtFeedSendDescription);
        imgFeedSave = (ImageView) findViewById(R.id.imgFeedSave);

        tılFeedSendTitle = (TextInputLayout) findViewById(R.id.tılFeedSendTitle);
        tılFeedSendDesciption = (TextInputLayout) findViewById(R.id.tılFeedSendDesciption);

        parseUser = ParseUser.getCurrentUser();
        userId = parseUser.getObjectId();


        videoName = getIntent().getStringExtra("VideoName");
        videoPath = getIntent().getStringExtra("VideoPath");

        if (getIntent().getStringExtra("HeadLine") != null) {
            tılFeedSendTitle.setVisibility(View.INVISIBLE);
            tılFeedSendDesciption.setVisibility(View.INVISIBLE);

        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(videoPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];


            for (int readNum; (readNum = fileInputStream.read(buf)) != -1; ) {
                byteArrayOutputStream.write(buf, 0, readNum);
            }
            byte[] totalVideoByteArray = byteArrayOutputStream.toByteArray();
            this.totalVideoByteArray = totalVideoByteArray;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        vdvFeedSend.setRotation(90.0f);
        vdvFeedSend.setVideoPath(videoPath);
        vdvFeedSend.seekTo(100);


        vdvFeedSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            Log.e("click", "double click");

                            if (vdvFeedSend.isPlaying()) {
                                stopPosition = vdvFeedSend.getCurrentPosition();
                                vdvFeedSend.pause();

                            } else {
                                vdvFeedSend.seekTo(stopPosition);
                                vdvFeedSend.start();
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

        imgFeedSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getStringExtra("HeadLine") != null)
                    sendFeed();
                else {
                    title = edtFeedSendTitle.getText().toString();
                    description = edtFeedSendDescription.getText().toString();
                    if (title.length() > 40)
                        Toast.makeText(getApplicationContext(), "Başlık en fazla 40 karakter olabilir !", Toast.LENGTH_SHORT).show();
                    else if (title.isEmpty() || title.length() < 5)
                        Toast.makeText(getApplicationContext(), "Başlık en az 5 karakter olmalıdır !", Toast.LENGTH_SHORT).show();
                    else if (description.length() > 200)
                        Toast.makeText(getApplicationContext(), "Açıklama en falza 200 karakter olabilir !", Toast.LENGTH_SHORT).show();
                    else
                        sendFeed();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent ıntent = new Intent(FeedSend.this, FeedRecord.class);
                if (getIntent().getStringExtra("HeadLine") != null)
                    ıntent.putExtra("HeadLine", getIntent().getStringExtra("HeadLine"));
                startActivity(ıntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendFeed() {
        final ProgressDialog pd = new ProgressDialog(FeedSend.this);
        pd.setMessage("Yükleniyor ...");
        pd.setCancelable(false);
        pd.show();

        ParseFile file = new ParseFile(videoName, totalVideoByteArray);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                }
            }
        });

        Bitmap previewImageBıtmap = ThumbnailUtils.createVideoThumbnail(videoPath,
                MediaStore.Images.Thumbnails.MINI_KIND);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        previewImageBıtmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] previewImageByte = stream.toByteArray();


        ParseFile file1 = new ParseFile("preview" + videoName.split(".mp4")[0] + ".jpg", previewImageByte);
        file1.saveInBackground();

        ParseObject saveFeed = new ParseObject("Feed");
        saveFeed.put("FeedVideo", file);
        saveFeed.put("FeedImage", file1);
        saveFeed.put("UserId", userId);
        saveFeed.put("Title", title);
        saveFeed.put("Description", description);
        saveFeed.put("LikeCounter", 0);
        saveFeed.put("DisLikeCounter", 0);
        saveFeed.put("ComplaintCounter", 0);
        if (getIntent().getStringExtra("HeadLine") != null)
            saveFeed.put("Category", getIntent().getStringExtra("HeadLine"));
        else
            saveFeed.put("Category", "Global");
        saveFeed.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    pd.dismiss();
                    finish();
                }
                else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
