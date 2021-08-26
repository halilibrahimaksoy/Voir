package com.halilibrahimaksoy.voir.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar;
import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.halilibrahimaksoy.voir.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.InflaterInputStream;

/**
 * Created by Halil ibrahim AKSOY on 8.01.2016.
 */
public class FeedRecord extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private RoundCornerProgressBar prbFeedRecord;
    private String TAG = "camera uygulaması ";
    private Toolbar tbFeedRecord;
    private boolean hasTouch = false;
    private ImageView imgCameraSwitch;
    private ImageView imgVideoSend;
    Movie movie;
    private boolean isCameraBack = true;
    private List<File> recordVideos;
    private long[] rotate90 = new long[]

            {0, 0x00010000, 0,

                    -0x00010000, 0, 0,

                    0, 0, 0x40000000};


    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_record);
        pd = new ProgressDialog(FeedRecord.this);
        pd.setCancelable(false);

        tbFeedRecord = (Toolbar) findViewById(R.id.tbFeedRecord);
        setSupportActionBar(tbFeedRecord);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recordVideos = new ArrayList<>();
        imgCameraSwitch = (ImageView) findViewById(R.id.imgCameraSwitch);
        imgCameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ıntent = new Intent(FeedRecord.this, FeedRecord.class);
                ıntent.putExtra("SelectCamera", !isCameraBack);
                startActivity(ıntent);
            }
        });
        imgVideoSend = (ImageView) findViewById(R.id.imgVideoSend);
        imgVideoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Video Yapılandırılıyor ...");
                pd.show();

                sendVideo();
            }
        });
        movie = new Movie();
        isCameraBack = getIntent().getBooleanExtra("SelectCamera", true);

        mCamera = getCameraInstance(isCameraBack);
        mPreview = new CameraPreview(this, mCamera);

        prbFeedRecord = (RoundCornerProgressBar) findViewById(R.id.prbFeedRecord);
        final FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
        preview.addView(mPreview);
        final Handler handler = new Handler();
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                if (hasTouch) {
                    prbFeedRecord.setProgress(prbFeedRecord.getProgress() + 1);
                    handler.postDelayed(this, 40);
                }
            }
        };


        prbFeedRecord.setOnProgressChangedListener(new BaseRoundCornerProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int viewId, float progress, boolean isPrimaryProgress, boolean isSecondaryProgress) {
                if (progress >= 375) {
                    preview.setOnTouchListener(null);
                }
                if (progress <= 5)
                    imgVideoSend.setVisibility(View.INVISIBLE);
                else
                    imgVideoSend.setVisibility(View.VISIBLE);
            }
        });
        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hasTouch = true;

                        startRecord();
                        handler.post(run);
                        break;
                    case MotionEvent.ACTION_UP:
                        hasTouch = false;
                        stopRecord();
                        break;
                }
                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startRecord() {
        try {
            if (!isRecording) {
                if (prepareVideoRecorder()) {
                    mMediaRecorder.start();

                    isRecording = true;
                } else {
                    releaseMediaRecorder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        try {


            if (isRecording) {
                mMediaRecorder.stop();
                releaseMediaRecorder();
                mCamera.lock();

                isRecording = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void appendVideos() {
        pd.show();
        try {
            List<Movie> movies = new LinkedList<Movie>();

            for (int i = 0; i < recordVideos.size(); i++) {
                InputStream in = new FileInputStream(recordVideos.get(i));
                movies.add(MovieCreator.build(Channels.newChannel(in)));
            }
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();


            for (Movie m : movies) {

                for (Track track : m.getTracks()) {
                    if (track.getHandler().equals("vide")) {
                        videoTracks.add(track);
                    }
                    if (track.getHandler().equals("soun")) {
                        audioTracks.add(track);
                    }
                }
            }

            Movie concatMovie = new Movie();


            concatMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            concatMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));


            IsoFile out2 = new DefaultMp4Builder().build(concatMovie);


            out2.getMovieBox().getMovieHeaderBox().setMatrix(rotate90);

            {
                FileChannel fc = new RandomAccessFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString(), "rw").getChannel();

                fc.position(0);
                out2.getBox(fc);
                fc.close();
            }
            pd.dismiss();
        } catch (Exception e) {
            Log.e("ex", "HATA --" + e.getMessage());
        }

    }

    private void sendVideo() {


        appendVideos();
        try {
            Intent ıntent = new Intent(FeedRecord.this, FeedSend.class);
            ıntent.putExtra("VideoName", recordVideos.get(recordVideos.size() - 1).getName().toString());
            ıntent.putExtra("VideoPath", recordVideos.get(recordVideos.size() - 1).getAbsolutePath().toString());

            if(getIntent().getStringExtra("HeadLine")!=null)
            ıntent.putExtra("HeadLine", getIntent().getStringExtra("HeadLine"));

            pd.dismiss();
            startActivity(ıntent);


        } catch (Exception e) {
            Log.e("ex", "HATA---" + e.getMessage());
        }

    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Voir-Videos");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        recordVideos.add(mediaFile);
        return mediaFile;
    }

    private boolean prepareVideoRecorder() {

        mMediaRecorder = new MediaRecorder();


        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);


        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        mMediaRecorder.setMaxDuration(15000);
        mMediaRecorder.setMaxFileSize(10000000);


        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }


        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        releaseCamera();
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private static int findFrontFacingCamera() {
        int cameraId = -1;

        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }

        return cameraId;
    }

    private static int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static Camera getCameraInstance(boolean isCameraBack) {
        Camera c = null;
        try {

            if (isCameraBack)
                c = Camera.open(findBackFacingCamera());
            else
                c = Camera.open(findFrontFacingCamera());
            c.setDisplayOrientation(90);
        } catch (Exception e) {
        }
        return c;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            if (mHolder.getSurface() == null) {
                return;
            }

            try {
                mCamera.stopPreview();
            } catch (Exception e) {
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}