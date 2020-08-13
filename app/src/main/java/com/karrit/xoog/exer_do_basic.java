package com.karrit.xoog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;

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

public class exer_do_basic extends AppCompatActivity {
    CountDownTimer countDownTimer;
    boolean isSystemUI,isPlaying,isSlowMo;

    Timer timer;
    TextToSpeech tts;
    private String TAG="EXERCISE_LEARN";
    private String LOGTAG="EXERCISE_LEARN";
    Long timeRemaining;
   Exer_remain exerRemain;
   boolean started;
    VideoView videoView;
    TextView timer_text,timer_text_rest;
    int i;
    ConstraintLayout cons;
    shared share;
    task_details task_details;
    int level,task;
    MediaPlayer mp;
    boolean is_ex;

    sql_health sql;
    sport_details_class details_class;
    int count;
    Group ex,rest;
    ArrayList<sport_exercise_class> arrayList;
    TextView ex_name;
    CallReceiver myreceiver;
    IntentFilter filter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exer_do_basic);
        timer_text=findViewById(R.id.timer_text);

        started=false;


        timer_text_rest=findViewById(R.id.timer_text_rest);

        ex=findViewById(R.id.group_ex);
        rest=findViewById(R.id.group_rest);
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


        mp = MediaPlayer.create(exer_do_basic.this, Uri.parse(localfile.toString()));
        mp.setLooping(true);
        mp.setVolume(log1,log1);
        share=new shared(this);
        task_details=new task_details(this,share.getCurrent_kid(),share.getCurrent_course_type());
        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
        exerRemain=new Exer_remain();
        sql=new sql_health(this,share.getCurrent_kid());
        details_class=sql.readDetails(level,task);
        arrayList=sql.readhealth(level,task);
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
        cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSystemUI){
                    hideSystemUI();
                    if(!isPlaying){
                        videoView.start();
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
        is_ex=true;
        ex.setVisibility(View.VISIBLE);
        rest.setVisibility(View.GONE);

        //  VideoView videoView=findViewById(R.id.videoView);
        Log.i(TAG,"excercise "+i);
        int ex_id=arrayList.get(i).getEx_id();
        ex_name.setText(arrayList.get(i).getEx_name());
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

        timeRemaining=Long.valueOf(arrayList.get(i).getTime()*1000);
        String speak=arrayList.get(i).getEx_name();
        tts.speak(speak,TextToSpeech.QUEUE_FLUSH,null);

        videoView.start();

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
        exerRemain.save();
        if(isPlaying){
            Log.i(TAG,"is playing");

            countDownTimer.cancel();
            videoView.pause();
            mp.pause();
            isPlaying=false;}
    }

    private void showSystemUI() {
        isSystemUI=true;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
                if(is_ex){
                timer_text.setText(Long.toString(l/1000));}else {
                    timer_text_rest.setText(Long.toString(l/1000));
                }

            }

            @Override
            public void onFinish() {


                videoView.pause();

                if (is_ex) {
                    Log.i(TAG,"is ex"+count);
                    if (i < count - 1) {
                        Log.i(TAG,"less than count");

                        videoView.pause();
                        restfn();
                        Log.i(TAG,"next rest "+i);
                    } else  {
                        //go to finish;
                        exerRemain.clearPref();
                        Intent intent=new Intent(exer_do_basic.this,Task_finish.class);
                        startActivity(intent);
                        finish();
                        Log.i(TAG, "Activity finish");
                    }
                }else {
                    i++;
                    exercise();
                    Log.i(TAG,"next exercise "+i);
                }



                //  videoView.stopPlayback();
            }
        }.start();
    }
public void restfn(){
    Log.i(TAG,"rest"+i);
    is_ex=false;
    String speak="Let's take some Rest";
    tts.speak(speak,TextToSpeech.QUEUE_FLUSH,null);

    rest.setVisibility(View.VISIBLE);
    ex.setVisibility(View.GONE);
    timeRemaining=Long.valueOf(arrayList.get(i).getRelax()*1000);
    startTimer();
}



    public void showDialog_start(){
        cons.setClickable(false);
        final Dialog dialogSports = new Dialog(exer_do_basic.this);
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
                dialogSports.dismiss();
                cons.setClickable(true);
                started=true;

            }
        });
        TextView text=dialogSports.findViewById(R.id.text);
        text.setText("Let's Exercise Together");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogSports.show();
    }

    @Override
    protected void onPause() {
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
        shared shared=new shared(exer_do_basic.this);
        private String Time_key = "timeRemaining";
        private String ex_count_key = "ex_count";
        private String is_ex_key = "is_Ex";
        String PREFS_NAME;


        public Exer_remain() {
            PREFS_NAME = shared.getCurrent_kid() + "exer" + level + "_" + task+"_"+shared.getCurrent_course_type();
            this.id = shared.getCurrent_kid();
            settings = exer_do_basic.this.getSharedPreferences(PREFS_NAME,
                             Context.MODE_PRIVATE);
            editor = settings.edit();
        }

        public void save() {
            if(timeRemaining!=null) {
                editor.putLong(getFieldKey(Time_key), timeRemaining);
                Log.i(TAG + "Exer", "time" + timeRemaining);
                Log.i(TAG + "Exer", "is_ex" + is_ex);
                Log.i(TAG + "Exer", "i" + settings);
                editor.putInt(getFieldKey(ex_count_key), i);
                editor.putBoolean(getFieldKey(is_ex_key), is_ex);
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
                is_ex=settings.getBoolean(getFieldKey(is_ex_key),true);
                i=settings.getInt(getFieldKey(ex_count_key),0);
                Log.i(TAG+"Exer","is_ex"+is_ex);
                Log.i(TAG+"Exer","i"+settings);
                if(is_ex){
                    exercise();
                    countDownTimer.cancel();
                    timeRemaining=l;
                    startTimer();

                }else{
                    restfn();
                    countDownTimer.cancel();
                    timeRemaining=l;
                    startTimer();

                }
                return true;
            }
        }


        private String getFieldKey( String fieldKey) {

            return   id +"_"+level+"_"+task +"_"+fieldKey;
        }
    }
}
