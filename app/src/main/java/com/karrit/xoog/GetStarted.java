package com.karrit.xoog;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GetStarted extends AppCompatActivity {

   boolean buttonText;
    ViewFlipper imageFrame;
    ConstraintLayout slideShowBtn;
    Handler handler;
    Runnable runnable;
    private int[] sliderImageId;
    String[] line1;
    String[] line2;
    TextView t1;
    TextView t2;
    ImageView[] dot_image;
    LinearLayout dots;
    Button bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        imageFrame = findViewById(R.id.imageFrames);
      Log.i("Main thread",Long.toString(Thread.currentThread().getId()));
        buttonText=false;
        bottom = (Button)findViewById(R.id.button2);
        sliderImageId = new int[]{
                R.drawable.icon, R.drawable.cube, R.drawable.gift
        };
        dots=findViewById(R.id.linear_dots);
        handler = new Handler();
        line1 = getResources().getStringArray(R.array.line1);
        line2 = getResources().getStringArray(R.array.line2);
        t1 = findViewById(R.id.text1);
        t2 = findViewById(R.id.text2);
        dot_image=new ImageView[3];
        for(int i=0;i<3;i++){
            dot_image[i]=new ImageView(GetStarted.this);
            if(i==0){
                Log.i("active","dots");
                dot_image[i].setImageResource(R.drawable.selected_dot_getstarted);
            }else{
                Log.i("non active","dots");
                dot_image[i].setImageResource(R.drawable.non_selected_dot_getstarted);
            }
            int dp=(int)getResources().getDimension(R.dimen.value_dots_getStarted);
            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(dp,dp);
            params.setMargins(8,0,8,0);
            dots.addView(dot_image[i],params);
        }
       addFlipperImages(imageFrame);

     runnable = new Runnable() {

            @Override
            public void run() {
                int i= imageFrame.indexOfChild(imageFrame.getCurrentView());
                Log.i("child",Integer.toString(i));
                Log.i("handler thread",Long.toString(Thread.currentThread().getId()));
                if(i<1){

                    handler.postDelayed(runnable, 3000);
                }
                if(i==1){
                    bottom.setText("Sign up");
                    buttonText=true;
                }
                imageFrame.setInAnimation(inFromRightAnimation());
                imageFrame.setOutAnimation(outToLeftAnimation());
                imageFrame.showNext();
                for(int j=0;j<3;j++){
                    dot_image[j].setImageResource(R.drawable.non_selected_dot_getstarted);
                }
                dot_image[i+1].setImageResource(R.drawable.selected_dot_getstarted);

                t1.setText(line1[i+1]);
                t2.setText(line2[i+1]);


            }
        };
        handler.postDelayed(runnable, 3000);
        // Gesture detection



        slideShowBtn = (ConstraintLayout) findViewById(R.id.cons);
        //slideShowBtn.setOnTouchListener(gestureListener);
        slideShowBtn.setOnTouchListener(new slideshow(GetStarted.this){
            public void onSwipeRight() {

                int i= imageFrame.indexOfChild(imageFrame.getCurrentView());
                handler.removeCallbacks(runnable);
                imageFrame.setInAnimation(inFromLeftAnimation());
                imageFrame.setOutAnimation(outToRightAnimation());
                if(i>0){
                    for(int j=0;j<3;j++){
                        dot_image[j].setImageResource(R.drawable.non_selected_dot_getstarted);
                    }
                    dot_image[i-1].setImageResource(R.drawable.selected_dot_getstarted);
                    imageFrame.showPrevious();
                    t1.setText(line1[i-1]);
                    t2.setText(line2[i-1]);
                }


            }
            public void onSwipeLeft() {

                int i= imageFrame.indexOfChild(imageFrame.getCurrentView());

                //  int i= imageFrame.indexOfChild(imageFrame.getCurrentView());
                handler.removeCallbacks(runnable);
                imageFrame.setInAnimation(inFromRightAnimation());
                imageFrame.setOutAnimation(outToLeftAnimation());
                if(i==1&&(!buttonText)){
                    bottom.setText("Sign up");
                }

                if(i<2){
                    for(int j=0;j<3;j++){
                        dot_image[j].setImageResource(R.drawable.non_selected_dot_getstarted);
                    }
                    dot_image[i+1].setImageResource(R.drawable.selected_dot_getstarted);
                    imageFrame.showNext();
                    t1.setText(line1[i+1]);
                    t2.setText(line2[i+1]);

                }
                if(i==0){
                    handler.postDelayed(runnable, 3000);}

            }
        });

    }

    private void addFlipperImages(ViewFlipper flipper) {
        int imageCount = sliderImageId.length;

        for (int count = 0; count < imageCount; count++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(sliderImageId[count]);

            //ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
            //params.horizontalBias=0.5f;
            Log.i("count",Integer.toString(count));
            flipper.addView(imageView);
        }

    }
    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    public void clickfn(View view) {

   Intent intent = new Intent(getApplicationContext(),login.class);
        startActivity(intent);

    }

}

