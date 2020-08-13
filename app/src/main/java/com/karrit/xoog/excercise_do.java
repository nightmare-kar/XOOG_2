package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;

import android.content.ContextWrapper;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class excercise_do extends AppCompatActivity implements SurfaceHolder.Callback {
    private String TAG="exercise_do";
    StorageReference storageReference;
    CountDownTimer countDownTimer;
    boolean isSlowMo;
    TextView timer_text;
    boolean running;
    int level,task;
    sql_sports sql;
    shared share;
    task_details task_details;
    MediaPlayer mp;
    Group group_ex,group_rest;
boolean is_ex;
    long timeRemaining;
    Timer timer;
    int width,height;
    boolean isSystemUI;
    VideoView videoView;
    sport_details_class details_class;
    TextView timer_text_ex,timer_text_rest;
    int count,i;
    ArrayList<sport_exercise_class> arrayList;



    private MediaRecorder recorder;
    public static SurfaceHolder holder;
    public static SurfaceView cameraView;
    Intent intent;
    recordTask recordTask;
    public static Camera camera;



    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;

    private CamcorderProfile camcorderProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();


        setContentView(R.layout.activity_excercise_do);
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        recordTask=new recordTask();

        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("upload");
        holder = cameraView.getHolder();

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        TextView textView=findViewById(R.id.timer_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordTask.execute();
            }
        });
        share=new shared(this);
        task_details=new task_details(this,share.getCurrent_kid(),share.getCurrent_course_type());

        group_rest=findViewById(R.id.group_rest);
group_ex=findViewById(R.id.group_ex);
// level=task_details.getCurrent_level();
        //task=task_details.getCurrent_task();
        level=1;
        task=2;
        sql=new sql_sports(this,share.getKid1_id());
        details_class=sql.readDetails(level,task);
        timer_text_ex=findViewById(R.id.timer_text);
        timer_text_rest=findViewById(R.id.timer_text_rest);
        arrayList=sql.readSports(level,task);
        count=arrayList.size();
        i=0;


      /*  videoView=findViewById(R.id.videoView);
        ContextWrapper cw = new ContextWrapper(getApplication());
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();}
        String filename="ex_"+1+".txt";
        File localfile=new File(directory,filename);


        Uri uri=Uri.parse(localfile.toString());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(localfile.toString());
        width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
        isSlowMo=false;
        Log.i(TAG,"width"+width+"height"+height);

        int screen_width= (getWindowManager().getDefaultDisplay().getWidth());
        int screen_height=(getWindowManager().getDefaultDisplay().getHeight());
        int width_width=(int)screen_height*width/height;


        if(width_width<=screen_width) {
            Log.i(TAG,"widht_width");
            ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams(width_width,screen_height);
            videoView.setLayoutParams(layoutParams);

        }else{
            Log.i(TAG,"height height");
            int height_height=(int)screen_width*height/width;
            ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams(screen_width,height_height);

            videoView.setLayoutParams(layoutParams);
        }
        ConstraintLayout cons=findViewById(R.id.cons);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cons);
        constraintSet.setHorizontalBias(R.id.videoView, 0.5f);
        constraintSet.connect(R.id.videoView, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.applyTo(cons);*/

        ConstraintLayout cons=findViewById(R.id.cons);
        cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSystemUI){
                    hideSystemUI();
                    videoView.start();
                    startTimer();
                    Log.i(TAG+"ui", "is system ui");
                }else {
                    videoView.pause();
                    showSystemUI();
                    countDownTimer.cancel();
                    Log.i(TAG+"ui", "no system ui");
                }
            }
        });

        videoView=findViewById(R.id.videoView);




        timer_text=findViewById(R.id.timer_text);

        exercise();
        // Intent intent=getIntent();
        //Log.i(TAG,"serializable"+intent.getSerializableExtra("myset").toString());

        ///data/user/0/com.example.datatrasfer/files/1.txt
        ///data/user/0/com.example.datatrasfer/app_xoog_excercise/1.txt*/
       /* HashSet<String> set=new HashSet<>();
        set.add(Integer.toString(1));
        set.add(Integer.toString(2));
        set.add(Integer.toString(3));
        Intent intent=new Intent(this,VideoService.class);
      intent.putExtra("mySet",set);
       startService(intent);
       String key_ex_video_download="ex_download";
       String name = "general";
       SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
       HashSet<String> newSet = new HashSet<String>(sharedPreferences.getStringSet(key_ex_video_download, new HashSet<String>()));
       Log.i(TAG,newSet.toString());
        if(newSet.isEmpty()){
            Log.i(TAG,"string is empty");
        }
*/
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    public void exercise(){
        Log.i(TAG,"excercise "+i);

      group_ex.setVisibility(View.VISIBLE);
      group_rest.setVisibility(View.GONE);
  is_ex=true;

      int ex_id=arrayList.get(i).getEx_id();

        ContextWrapper cw = new ContextWrapper(getApplication());
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();}
        String filename="ex_"+ex_id+".txt";
        File localfile=new File(directory,filename);
        Log.i(TAG,"ex id"+ex_id);
        Log.i(TAG,"ex_name"+arrayList.get(i).getEx_name());

        Uri uri=Uri.parse(localfile.toString());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(localfile.toString());
        width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
        isSlowMo=false;
        Log.i(TAG,"width"+width+"height"+height);

        int screen_width= (getWindowManager().getDefaultDisplay().getWidth());
        int screen_height=(getWindowManager().getDefaultDisplay().getHeight());
        int width_width=(int)screen_height*width/height;
        ConstraintLayout cons=findViewById(R.id.cons);

        if(width_width<=screen_width) {
            Log.i(TAG,"widht_width");
            ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams(width_width,screen_height);
            videoView.setLayoutParams(layoutParams);

        }else{
            Log.i(TAG,"height height");
            int height_height=(int)screen_width*height/width;
            ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams(screen_width,height_height);

            videoView.setLayoutParams(layoutParams);
        }
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cons);
        constraintSet.setHorizontalBias(R.id.videoView, 0.5f);
        constraintSet.connect(R.id.videoView, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.applyTo(cons);

                timeRemaining=arrayList.get(i).getTime()*1000;
                videoView.start();
                startTimer();

    }
    private void hideSystemUI() {
        isSystemUI=false;
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE|

                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void showSystemUI() {
        isSystemUI=true;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    public void slowMotion(){

        timer = new Timer();
        isSlowMo=true;

        // applied timer logic for playing video in slow motion
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                Log.i(TAG,"run activity ");
                if (videoView.isPlaying())
                    videoView.pause();
                else
                    videoView.start();

            }
        }, 100, 150);

    }
    public void startTimer(){
Log.i(TAG,"timer start"+timeRemaining);
        countDownTimer=new CountDownTimer(timeRemaining,1000) {
            @Override
            public void onTick(long l) {
                timeRemaining=l;
                if(is_ex){
                   // Log.i(TAG,"timer text ex set");
                    timer_text_ex.setText(Long.toString(l/1000));
                }else {
                    timer_text_rest.setText(Long.toString(l/1000));
                }

               // Log.i(TAG,timer_text.getText().toString());
            }

            @Override
            public void onFinish() {
                Log.i(TAG,"on finished");
                if(timeRemaining/1000==0) {
                    Log.i(TAG,"time Remaining is 0");
                    if (is_ex) {
                        Log.i(TAG,"is ex"+count);
                        if (i < count - 1) {
                            Log.i(TAG,"less than count");

                            videoView.pause();
                            rest();
                            Log.i(TAG,"next rest "+i);
                        } else  {
                            //go to finish;
                            Log.i(TAG, "Activity finish");
                        }
                    }else {
                        i++;
                        exercise();
                        Log.i(TAG,"next exercise "+i);
                    }
                }



            }
        }.start();
    }


    public void rest(){
        Log.i(TAG,"rest"+i);
        is_ex=false;

        group_rest.setVisibility(View.VISIBLE);
        group_ex.setVisibility(View.GONE);
        timeRemaining=arrayList.get(i).getRelax()*1000;
        startTimer();


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG,"surfaceCreated");

        if (usecamera) {
            camera = Camera.open();

            try{
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;}catch (Exception e){
                e.printStackTrace();
            }


        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(TAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning) {
                camera.stopPreview();
            }


            Camera.Parameters p = camera.getParameters();

            p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
            p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

            camera.setParameters(p);

            // camera.setPreviewDisplay(holder);
            //camera.startPreview();
            previewRunning = true;





        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceDestroyed");


        if (usecamera) {
            previewRunning = false;
            //camera.lock();
            camera.release();
        }
        finish();
    }
}
