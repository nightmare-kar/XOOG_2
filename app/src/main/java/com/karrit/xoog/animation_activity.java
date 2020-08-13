package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;

public class animation_activity extends AppCompatActivity {

    boolean b;
    ImageView[] rect;
    ImageView image;
    int f;
    MotionLayout motionLayout;
    private String TAG="animationAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_activity);
        b = true;
        motionLayout = findViewById(R.id.cons);

        image = findViewById(R.id.character);
        rect = new ImageView[10];
        rect[1] = findViewById(R.id.rect_1);
        rect[2] = findViewById(R.id.rect_2);
        rect[3] = findViewById(R.id.rect_3);
        rect[4] = findViewById(R.id.rect_4);
        rect[5] = findViewById(R.id.rect_5);
        rect[6] = findViewById(R.id.rect_6);
        rect[7] = findViewById(R.id.rect_7);
        rect[8] = findViewById(R.id.rect_8);
        rect[9] = findViewById(R.id.rect_9);


        //ConstraintSet constraintSet = motionLayout.getConstraintSet(R.id.character);
/*
      /  ConstraintSet constraintSet1 = new ConstraintSet();
        constraintSet1.clone(motionLayout);
        constraintSet1.clear(R.id.character,ConstraintSet.BOTTOM);
        constraintSet1.clear(R.id.character,ConstraintSet.START);
        constraintSet1.clear(R.id.character,ConstraintSet.END);
        constraintSet1.connect(R.id.character, ConstraintSet.BOTTOM, rect[1].getId(), ConstraintSet.TOP, 0);
        constraintSet1.connect(R.id.character, ConstraintSet.START, rect[1].getId(), ConstraintSet.START, 0);
        constraintSet1.connect(R.id.character, ConstraintSet.END, rect[1].getId(), ConstraintSet.END, 0);
        constraintSet1.applyTo(motionLayout);*/
        shared share = new shared(this);
        task_details taskDetails = new task_details(this, share.getCurrent_kid(), share.getCurrent_course_type());
        Intent intent = getIntent();
        boolean upgrade = intent.getBooleanExtra("upgrade", false);
        final int level = taskDetails.getCurrent_level();
        int task = taskDetails.getCurrent_task();
        Log.i(TAG,"level "+level);
        if (upgrade) {
            Log.i(TAG,"upgrade true");
            if (level > 1) {
                Log.i(TAG,"upgrade level");
                motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.BOTTOM, rect[level - 1].getId(), ConstraintSet.TOP);
                motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.START, rect[level - 1].getId(), ConstraintSet.START, 0);
                motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.END, rect[level - 1].getId(), ConstraintSet.END, 0);
           Runnable runnable=new Runnable() {
               @Override
               public void run() {
                   Log.i(TAG,"run true");
                   animFunction(level-1);
               }
           };
                Handler handler=new Handler();
                handler.postDelayed(runnable,500);



            }
        } else {
            Log.i(TAG,"upgrade false");
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.BOTTOM, rect[level].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.START, rect[level].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.END, rect[level].getId(), ConstraintSet.END, 0);
        }
        ImageView water_top = findViewById(R.id.water_top);
        ImageView water_down = findViewById(R.id.water_down);

        int newWidth_water_top = (int) (getWindowManager().getDefaultDisplay().getWidth());
        int newHeigth_water_top = (int) (0.109 * newWidth_water_top);

        //  water_top.getLayoutParams().height = newHeigth_water_top;
        water_down.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());
        water_top.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());


    }

    public void click(View view) {
        Intent intent=new Intent(animation_activity.this,Task_list.class);
        startActivity(intent);

      //
        //
        //
        //
        //
        //
        // animFunction(1);
       /* Log.i("click ","fn");
        EditText from = findViewById(R.id.from);

        f = Integer.parseInt(from.getText().toString());
        Log.i("f",Integer.toString(f)+rect[f].getId());
        motionLayout.getConstraintSet(R.id.start).clear(R.id.character,ConstraintSet.END);
        motionLayout.getConstraintSet(R.id.start).clear(R.id.character,ConstraintSet.START);
        motionLayout.getConstraintSet(R.id.start).clear(R.id.character,ConstraintSet.BOTTOM);
        motionLayout.getConstraintSet(R.id.end).clear(R.id.character,ConstraintSet.END);
        motionLayout.getConstraintSet(R.id.end).clear(R.id.character,ConstraintSet.START);
        motionLayout.getConstraintSet(R.id.end).clear(R.id.character,ConstraintSet.BOTTOM);

        if(b) {
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.BOTTOM, rect[f].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.START, rect[f].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.END, rect[f].getId(), ConstraintSet.END, 0);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.BOTTOM, rect[f + 1].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.START, rect[f + 1].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.END, rect[f + 1].getId(), ConstraintSet.END, 0);
            motionLayout.transitionToEnd();
            b=false;
        }else {
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.BOTTOM, rect[f].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.START, rect[f].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.END, rect[f].getId(), ConstraintSet.END, 0);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.BOTTOM, rect[f + 1].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.START, rect[f + 1].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.END, rect[f + 1].getId(), ConstraintSet.END, 0);
            motionLayout.getTransition(R.id.trans).getKeyFrameList();
            motionLayout.transitionToStart();
            b=true;*/

    }



 /*       ConstraintSet constraintSet = new ConstraintSet();

constraintSet.clear(image.getId(),ConstraintSet.BOTTOM);
        constraintSet.clear(image.getId(),ConstraintSet.START);
        constraintSet.clear(image.getId(),ConstraintSet.END);
        constraintSet.connect(R.id.character, ConstraintSet.BOTTOM, rect[f].getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.character, ConstraintSet.START, rect[f].getId(), ConstraintSet.START, 0);
        constraintSet.connect(R.id.character, ConstraintSet.END, rect[f].getId(), ConstraintSet.END, 0);
        constraintSet.applyTo(cons);
        Log.i("click","finished");

        if (f > 9) {
            Toast.makeText(getApplicationContext(), "game over", Toast.LENGTH_LONG);
        } else {
            Log.i("1 top",rect[f].getTop()+"  "+ rect[f+1].getTop());
            if (rect[f].getTop() < rect[f + 1].getTop()) {
                anim(0, (rect[f + 1].getLeft() - rect[f].getLeft()) / 2, 0, -20, (long) 400);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("running","handler");
                        anim( (rect[f + 1].getLeft() - rect[f].getLeft()) / 2,rect[f + 1].getLeft() - rect[f].getLeft(),-20,(rect[f+1].getTop() - rect[f].getTop()),(long)500);
                    }
                }, 400);
            }
            else{
                anim(0, (rect[f + 1].getLeft() - rect[f].getLeft()) / 2, 0, rect[f+1].getTop()-rect[f].getTop()-20, (long) 500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("running","handler");
                        anim( (rect[f + 1].getLeft() - rect[f].getLeft()) / 2,rect[f + 1].getLeft() - rect[f].getLeft(),rect[f+1].getTop()-rect[f].getTop()-20,(rect[f+1].getTop() - rect[f].getTop()),(long)400);
                    }
                }, 500);
            }
        }


     /*   final Animation animation1 = new TranslateAnimation(0, 110, 0, -20);

        final Animation animation = new TranslateAnimation(110, (rect[2].getLeft() - rect[1].getLeft()), -20, (rect[2].getTop() - rect[1].getTop()));
        animation.setDuration(300);
        animation1.setFillAfter(true);
        animation.setFillAfter(true);
        animation1.setDuration(300);
        animation.setInterpolator(new AccelerateInterpolator());

        Interpolator customInterpolator = PathInterpolatorCompat.create(0.250f, 0.100f, 0.250f, 1.000f);
        Interpolator des_inter = PathInterpolatorCompat.create(0.25f, 1f, 0.25f, 0.1f);
        animation1.setInterpolator(customInterpolator);
        animation.setFillAfter(true);
        image.startAnimation(animation1);

      */


    public void animFunction(int level) {
        Log.i("animFn","anim called");
        motionLayout.getConstraintSet(R.id.start).clear(R.id.character, ConstraintSet.END);
        motionLayout.getConstraintSet(R.id.start).clear(R.id.character, ConstraintSet.START);
        motionLayout.getConstraintSet(R.id.start).clear(R.id.character, ConstraintSet.BOTTOM);
        motionLayout.getConstraintSet(R.id.end).clear(R.id.character, ConstraintSet.END);
        motionLayout.getConstraintSet(R.id.end).clear(R.id.character, ConstraintSet.START);
        motionLayout.getConstraintSet(R.id.end).clear(R.id.character, ConstraintSet.BOTTOM);

        if (b) {
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.BOTTOM, rect[level].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.START, rect[level].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.start).connect(R.id.character, ConstraintSet.END, rect[level].getId(), ConstraintSet.END, 0);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.BOTTOM, rect[level + 1].getId(), ConstraintSet.TOP);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.START, rect[level + 1].getId(), ConstraintSet.START, 0);
            motionLayout.getConstraintSet(R.id.end).connect(R.id.character, ConstraintSet.END, rect[level + 1].getId(), ConstraintSet.END, 0);
            motionLayout.transitionToEnd();
        }

    /*  Handler handler = new Handler();
      float vx = rect[2].getLeft()-rect[1].getLeft();
      float vy_up=-21.225f;
      float vy_down = 0;
      float y2_up=-20f;
      float y2_down=(rect[1].getTop()-rect[2].getTop());*/





   /* public boolean animation_up(float x1,float y1,float y2, float v){
       float y=y1-((v*0.1f)-0.049f);
       float x=x1+(v+0.1f) ;
       v-=0.98f;
       boolean b;
       if(y<y2){
           b=false;
       }
       else{
           Animation animation = new TranslateAnimation(x1,x2,y1,y2);
           animation.setFillAfter(true);
           animation.setDuration(100);
           image.startAnimation(animation);
           animation_up(x,y,y2,v);
           b=true;
       }
       return b;



    }
    public boolean animation_down(float x1,float y1,float x2,float y2, float v){
        float y=y1+(v*0.1f)+0.049f;
        float x=x1+(v+0.1f) ;
        v+=0.98f;
        boolean b;
        if(y>y2){
            b=false;
        }
        else{
            Animation animation = new TranslateAnimation(x1,x2,y1,y2);
            animation.setFillAfter(true);
            animation.setDuration(100);
            image.startAnimation(animation);
            animation_up(x,y,x2,y2,v);
            b=true;
        }
        return b;
    }*/

    }

    @Override
    protected void onDestroy() {
        motionLayout.transitionToStart();
        super.onDestroy();
    }
}
