package com.karrit.xoog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private Context mContext = MainActivity.this;
    private String TAG="MainActivity";
    private String key_ex_video_download="ex_download";
   // MotionLayout motionLayout;
    //firebase
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ImageView imageView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // hideSystemUI();
       // setTheme(R.style.splashScreenTheme);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        HashSet<String> newSet = new HashSet<String>(preferences.getStringSet(key_ex_video_download, new HashSet<String>()));
       Log.i(TAG,"new String"+newSet.toString());
        if(!newSet.isEmpty()){
            Log.i(TAG,"service set is not empty");
            Intent intent=new Intent(this,VideoService.class);
            intent.putExtra("mySet",newSet);
            startService(intent);
        }
        ContextWrapper cw = new ContextWrapper(getApplication());
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);


        long availableSizeInBytes=new StatFs(directory.getPath()).getAvailableBytes();
        Log.i(TAG," available bytes "+availableSizeInBytes);

        setupFirebaseAuth();
        getRefferal();
        db=FirebaseFirestore.getInstance();
        shared share=new shared(this);
        Log.i(TAG,"share token"+share.getToken());
        imageView=findViewById(R.id.splash);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.small_to_big);
     //   motionLayout=findViewById(R.id.motion);
   //     motionLayout.transitionToEnd();
        final Animation animation_big=AnimationUtils.loadAnimation(this,R.anim.big_to_small);
//imageView.startAnimation(animation);
        setAlphaAnimation(imageView);

        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run(){
                startActivity(intent);
                finish();
            }
        };
        Runnable animRunnable=new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(animation_big);
            }
        };
     //   handler.postDelayed(animRunnable,1000);
        handler.postDelayed(runnable,2000);
    }
    public void getRefferal(){
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Log.i(TAG,"on success deep link ");
                Uri deeplink=null;
                if(pendingDynamicLinkData!=null){
                    deeplink=pendingDynamicLinkData.getLink();
                }
                if(deeplink!=null) {
                    Log.i(TAG, "Url" + deeplink.toString());
                    String referrerUid = deeplink.getQueryParameter("invitedby");
                    String SchoolUid = deeplink.getQueryParameter("schoolid");
                    String AcademyUid = deeplink.getQueryParameter("academyId");

                    Log.i(TAG, "referred" + referrerUid);
                    Log.i(TAG, "school " + SchoolUid);
                    Log.i(TAG, "academy " + AcademyUid);
                    shared share = new shared(MainActivity.this);
                    Log.i(TAG, "share " + share.getReferedby());
                    if (share.getReferedby().equals(getString(R.string.empty)) && share.getSchoolId().equals(getString(R.string.empty)) && share.getAcademyId().equals(getString(R.string.empty))) {
                        if (!(referrerUid==null)) {
                            share.setSchoolId(SchoolUid);
                            Log.i(TAG, "set share");
                            share.setReferedby(referrerUid);
                            Log.i(TAG, "referred" + referrerUid);
                            share.apply();
                        }
                        if (!(SchoolUid==null)) {
                            Log.i(TAG, "set share");
                            Log.i(TAG, "school " + SchoolUid);
                            share.apply();
                        }
                        if (!(AcademyUid==null)) {
                            Log.i(TAG, "set share");
                            share.setAcademyId(AcademyUid);
                            Log.i(TAG, "academy " + AcademyUid);
                            share.apply();
                        }

                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"no url");
            }
        });
    }

    private void setupFirebaseAuth(){
        Log.d("console", "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d("console", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("console", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    private void checkCurrentUser(FirebaseUser user){
        Log.d("console", "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            intent = new Intent(mContext, GetStarted.class);
         //   startActivity(intent);
           // finish();
        }
        else{
           shared shared= new shared(this);
            int process= shared.getProcess();
            Log.i("main",Integer.toString(process));
            if(process==2){
            intent = new Intent(mContext, Parent_kid.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Log.i("process","entered");
           // startActivity(intent);
               // finish();
                }else {
                intent=new Intent(mContext,GetStarted.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
              //  startActivity(intent);
            }
          /*  else if(process==3){
                Intent intent= new Intent(mContext,choose_subscription.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();}
           else if(process==4){
               Intent intent=new Intent(mContext,choose_plan.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               finish();

                }
           else{
                Intent intent=new Intent(mContext,GetStarted.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

           */
            }
        }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void hideSystemUI() {

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

    @Override
    protected void onDestroy() {
   //     motionLayout.transitionToStart();
        super.onDestroy();
    }
    public static void setAlphaAnimation(View v) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f);
        fadeIn.setDuration(1000);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v,"scaleY", 1f, 1.08f);
        scaleY.setDuration(1000);

        ObjectAnimator scalex = ObjectAnimator.ofFloat(v,"scaleX", 1f, 1.08f);
        ObjectAnimator TOscaleY = ObjectAnimator.ofFloat(v,"scaleY", 1.08f, 1f);
        ObjectAnimator Toscalex = ObjectAnimator.ofFloat(v,"scaleX", 1.08f, 1f);
        scalex.setDuration(1000);
        TOscaleY.setDuration(1000);
        Toscalex.setDuration(1000);
        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.playTogether(scalex,scaleY,fadeIn);

        final AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(Toscalex,TOscaleY);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet.start();
            }
        });
        mAnimationSet.start();
    }
}