package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class exer_learn extends AppCompatActivity {
    CountDownTimer countDownTimer;
    boolean isSystemUI,isPlaying,isSlowMo;
    TextView skip;
    Timer timer;
    TextToSpeech tts;
    private String TAG="EXERCISE_LEARN";
    private String LOGTAG="EXERCISE_LEARN";
    Long timeRemaining;
    VideoView videoView;
    boolean started;
    TextView timer_text;
    int i;
    ConstraintLayout cons;
    shared share;
    task_details task_details;
    int level,task;
    MediaPlayer mp;
    boolean isSkip;
    sql_health sqlHealth;
    sql_sports sqlSports;
    sport_details_class details_class;
    int count;
    ArrayList<sport_exercise_class> arrayList;
    TextView ex_name;
    Exer_remain exerRemain;
    CallReceiver myreceiver;
    IntentFilter filter1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exer_learn);
        timer_text=findViewById(R.id.timer_text);
        skip=findViewById(R.id.skip);
        share=new shared(this);
        task_details=new task_details(this,share.getCurrent_kid(),share.getCurrent_course_type());
        started=false;
        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
        exerRemain=new Exer_remain();
       isSkip=false;
        i=0;
        ContextWrapper cw = new ContextWrapper(getApplication());
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();}
        String filename="ex_"+getString(R.string.music)+".txt";
        File localfile=new File(directory,filename);
        int maxVolume = 50;
        int currVolume=30;
        float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));

         mp = MediaPlayer.create(exer_learn.this, Uri.parse(localfile.toString()));
        mp.setLooping(true);
        mp.setVolume(log1,log1);
        if(share.getCurrent_course_type().equals(getString(R.string.health_type))){
            sqlHealth=new sql_health(this,share.getCurrent_kid());
            details_class=sqlHealth.readDetails(level,task);
            arrayList=sqlHealth.readhealth(level,task);
        }else if(share.getCurrent_course_type().equals(getString(R.string.sport_type))){
            sqlSports=new sql_sports(this,share.getCurrent_kid());
            details_class=sqlSports.readDetails(level,task);
            arrayList=sqlSports.readSports(level,task);
        }


        count=arrayList.size();
        videoView=findViewById(R.id.videoView);
        ex_name=findViewById(R.id.ex_name);
        cons=findViewById(R.id.cons);
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                 tts.setLanguage(Locale.ENGLISH);
            }
        });
        isPlaying=true;

        showDialog_start();


        myreceiver= new CallReceiver(){
            @Override
            protected void onIncomingCallReceived(Context ctx, String number, Date start) {
                Log.i(TAG,"incoming call received");
               stop();

                super.onIncomingCallReceived(ctx, number, start);
            }

            @Override
            protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {


                super.onIncomingCallEnded(ctx, number, start, end);
            }

            @Override
            protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
                Log.i(TAG,"incoming call answered");

                super.onIncomingCallAnswered(ctx, number, start);
            }

            @Override
            protected void onMissedCall(Context ctx, String number, Date start) {

                super.onMissedCall(ctx, number, start);
            }

            @Override
            protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
                Log.i(TAG,"outgoing call ended");
                super.onOutgoingCallEnded(ctx, number, start, end);
            }

            @Override
            protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
                Log.i(TAG,"outgoing call startted");
                super.onOutgoingCallStarted(ctx, number, start);
            }
        };
        filter1 = new IntentFilter("android.intent.action.PHONE_STATE");
        filter1.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(myreceiver, filter1);

        //----------------------set Video view-----------------------------------
       /* videoView=findViewById(R.id.videoView);
        ContextWrapper cw = new ContextWrapper(getApplication());
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();}
        String filename="ex_"+1+".txt";
        File localfile=new File(directory,filename);


        Uri uri=Uri.parse(localfile.toString());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(localfile.toString());
        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
       int  height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
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
        cons=findViewById(R.id.cons);
        Log.i(TAG,"width "+width);
        Log.i(TAG,"height "+height);
        Log.i(TAG,"screen width "+screen_width);
        Log.i(TAG,"screeen height "+screen_height);
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
        constraintSet.connect(R.id.videoView, ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.applyTo(cons);
        videoView.start();
        timeRemaining=Long.valueOf(10000);
        startTimer();
       slowMotion();

        */
       skip.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               stop();
               if(i<count-1) {
                   Log.i(TAG,"i<count)");
                   i++;
                   exercise();
               }else {
                   Log.i(TAG,"i==count");
                   exerRemain.clearPref();
                   Intent intent=new Intent(exer_learn.this,Task_finish.class);
                   startActivity(intent);
                   finish();
               }
           }
       });
        cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSystemUI){
                    hideSystemUI();
                    if(!isPlaying){
                    videoView.start();
                    slowMotion();
                    mp.start();
                    startTimer();}

                    Log.i(TAG+"ui", "is system ui");
                }else {
                       /* countDownTimer.cancel();
                        isPlaying=false;
                        timer.cancel();
                        videoView.pause();*/
                       stop();
                    showSystemUI();

                    Log.i(TAG+"ui", "no system ui");
                }
            }
        });

    }
    public void exercise(){
        skip.setVisibility(View.GONE);
        //  VideoView videoView=findViewById(R.id.videoView);
        Log.i(TAG,"excercise "+i+" "+arrayList.get(i).getEx_name());
        int ex_id=arrayList.get(i).getEx_id();
        String ex=arrayList.get(i).getEx_name().replace(" ","\n");
        ex_name.setText(ex);

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
        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
         int  height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();
        Log.i(TAG,uri.toString());
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
        isSlowMo=false;

        int screen_width= (getWindowManager().getDefaultDisplay().getWidth());
        int screen_height=(getWindowManager().getDefaultDisplay().getHeight());
        int width_width=(int)screen_height*width/height;
        cons=findViewById(R.id.cons);
        Log.i(TAG,"width "+width);
        Log.i(TAG,"height "+height);
        Log.i(TAG,"screen width "+screen_width);
        Log.i(TAG,"screeen height "+screen_height);
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
        constraintSet.connect(R.id.videoView, ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(R.id.videoView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.applyTo(cons);
        Log.i(TAG,"width"+width+"height"+height);

        timeRemaining=Long.valueOf(20000);
        isSkip=false;

        String toSpeak=arrayList.get(i).getEx_name();
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
       videoView.start();
        slowMotion();
        mp.start();
        startTimer();


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
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
    public void stop(){
        if(isPlaying){
            Log.i(TAG,"is playing");
        timer.cancel();
        countDownTimer.cancel();
        videoView.pause();
        mp.pause();
        isPlaying=false;}
    }
    public void start(){
        Log.i(TAG,"start fn");
        Log.i(TAG,"is playing"+isPlaying);
        if(!isPlaying){
            Log.i(TAG,"is playing");
            startTimer();
            slowMotion();
            mp.start();
            videoView.start();

        }
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
             try {


                 if (videoView.isPlaying())
                     videoView.pause();
                 else
                     videoView.start();
             }catch (Exception e){
                 e.printStackTrace();
             }

            }
        }, 100, 150);

    }
    public void startTimer(){
        // record.startRecord();
        //videoView.start();
        isPlaying=true;
        countDownTimer=new CountDownTimer(timeRemaining,1000) {
            @Override
            public void onTick(long l) {
                timeRemaining=l;
                if(timeRemaining<4000&&timeRemaining>1000){
                    Long speak=Long.valueOf(timeRemaining/1000);
                    String toSpeak=speak.toString();
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                timer_text.setText(Long.toString(l/1000));

            }

            @Override
            public void onFinish() {

                timer.cancel();
                videoView.pause();
               if(isSkip){
                   if(i<count-1){
                   i++;
                   exercise();}else{
                       exerRemain.clearPref();
                       Intent intent=new Intent(exer_learn.this,Task_finish.class);
                       startActivity(intent);
                       finish();
                       Log.i(TAG,"stay finish");
                   }
               }else {
                   showDialog_Try();
               }
                //  videoView.stopPlayback();
            }
        }.start();
    }


    public void slow_motion(View view) {
        if(isSlowMo){
            timer.cancel();
            timer.purge();
            isSlowMo=false;
            Log.i(TAG,"isSlow");
            videoView.start();
        }else {
            Log.i(TAG,"noSlow");
            slowMotion();
        }
    }
    public void showDialog_Try(){
        final Dialog dialogTry = new Dialog(exer_learn.this);
        dialogTry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTry.setCancelable(false);
        dialogTry.setContentView(R.layout.start_dialog_sports);
        Window window = dialogTry.getWindow();
        TextView text=dialogTry.findViewById(R.id.start);
        text.setText("Try it for yourself");
        Button close=dialogTry.findViewById(R.id.start);
        Log.i("main","pop_up_rubik");

        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogTry.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogTry.show();
        isPlaying=false;
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                dialogTry.dismiss();
                videoView.start();
                slowMotion();
                isSkip=true;
                timeRemaining=Long.valueOf(120000);
                startTimer();
                skip.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(runnable,2000);

    }

    public void showDialog_start(){
        cons.setClickable(false);
        final Dialog dialogSports = new Dialog(exer_learn.this);
        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.start_layout_dialog);
        Window window = dialogSports.getWindow();
        TextView close=dialogSports.findViewById(R.id.start);
        Log.i("main","pop_up_rubik");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ex=exerRemain.getBack();
                if(!ex){
                    exercise();}
                started=true;
                dialogSports.dismiss();
                cons.setClickable(true);

            }
        });
        TextView textView=dialogSports.findViewById(R.id.text);
        textView.setText("Watch and Learn");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogSports.show();
    }

    @Override
    protected void onPause() {
        exerRemain.save();
        try {
            if (myreceiver != null)
                unregisterReceiver(myreceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i(TAG,"onPause");
        if(started) {
            stop();
        }
       super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"on Destroy");
        mp.stop();
        mp.release();
        tts.shutdown();

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        if (!isPlaying){
            startTimer();
        slowMotion();
        videoView.start();
        mp.start();}

        try{
            registerReceiver(myreceiver,filter1);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStart();
    }
    class Exer_remain {
        private SharedPreferences settings;
        private SharedPreferences.Editor editor;
        String id;
        shared shared=new shared(exer_learn.this);
        private String Time_key = "timeRemaining";
        private String ex_count_key = "ex_count";
        String PREFS_NAME;
        private String is_skip_key = "is_skip";

        public Exer_remain() {
            PREFS_NAME = shared.getCurrent_kid() + "exer" + level + "_" + task+"_"+shared.getCurrent_course_type();
            Log.i(TAG,"pref Name "+PREFS_NAME);
            this.id = shared.getCurrent_kid();
            Log.i(TAG,"field key "+getFieldKey(ex_count_key));

            settings = exer_learn.this.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
            editor = settings.edit();
        }

        public void save() {
            if(timeRemaining!=null) {
                editor.putLong(getFieldKey(Time_key), timeRemaining);
                Log.i(TAG + "Exer", "time" + timeRemaining);
                Log.i(TAG + "Exer", "i" + settings);
                Log.i(TAG + "Exer", "skip" + isSkip);
                editor.putInt(getFieldKey(ex_count_key), i);
                editor.putBoolean(getFieldKey(is_skip_key),isSkip);
                editor.apply();
            }

        }
        public void clearPref(){
            Log.i(TAG,"exer pref cleared "+editor.clear().commit());

        }

        public boolean getBack() {
            Long l = settings.getLong(getFieldKey(Time_key), -10);
            if (l == (-10)) {
                return false;
            } else {
                Log.i(TAG+"Exer","time"+l);
                timeRemaining=l;
                i=settings.getInt(getFieldKey(ex_count_key),0);
                Log.i(TAG+"Exer","i"+settings);

                    exercise();
                    countDownTimer.cancel();
                    timeRemaining=l;
                    startTimer();
                    isSkip=settings.getBoolean(getFieldKey(is_skip_key),true);
                    if(isSkip){
                        skip.setVisibility(View.VISIBLE);
                    }

                return true;
            }
        }


        private String getFieldKey( String fieldKey) {

            return   id +"_"+level+"_"+task +"_"+fieldKey;
        }
    }
}
