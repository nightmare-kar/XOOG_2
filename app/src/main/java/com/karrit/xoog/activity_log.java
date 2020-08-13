package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


public class activity_log extends AppCompatActivity {
    HorizontalCalendar horizontalCalendar;
    SimpleDateFormat dateFormat;
    private String TAG="Activity_log";
    ConstraintLayout sports,rubik,health;
    TextView null_activity;
    TextView level_sport,task_sport,credits_sport;
    TextView level_rubik,task_rubik,credits_rubik;
    TextView level_health,task_health,credits_health;
TextView task_des_rubik;
LinearLayout ex_sport;
    LinearLayout ex_health;
sql_sports sql_sports;
sql_rubik sql_rubik;
sql_health sql_health;
task_details task_details_rubik, task_details_sports,task_details_health;
shared share;
String course_id_sport,course_id_rubik,course_id_health;
Calendar selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Calendar endDate = Calendar.getInstance();

        Calendar startDate=Calendar.getInstance();
        ImageView close=findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        Date date=new Date();
        try{
        date=dateFormat.parse("15/07/2020");}catch (Exception e){
            e.printStackTrace();
        }
        startDate.setTime(date);
        sports=findViewById(R.id.sports);
        rubik=findViewById(R.id.rubik);
        health=findViewById(R.id.health);
        null_activity=findViewById(R.id.null_activity);
        level_rubik=findViewById(R.id.level_rubik);
        task_rubik=findViewById(R.id.task_rubik);
        credits_rubik=findViewById(R.id.credits_earned_rubik);
        level_sport=findViewById(R.id.level_sports);
        task_sport=findViewById(R.id.task_sport);
        credits_sport=findViewById(R.id.credits_earned_sports);
        task_des_rubik=findViewById(R.id.task_type_rubik);
        ex_sport=findViewById(R.id.ex_type_sports);

        level_health=findViewById(R.id.level_health);
        task_health=findViewById(R.id.task_health);
        credits_health=findViewById(R.id.credits_earned_health);
        ex_health=findViewById(R.id.ex_type_health);

        share=new shared(this);
        Log.i(TAG,share.getKid1_id());
        sql_rubik=new sql_rubik(this,share.getCurrent_kid());
        sql_sports=new sql_sports(this,share.getCurrent_kid());
        sql_health=new sql_health(this,share.getCurrent_kid());

        task_details_health=new task_details(this,share.getCurrent_kid(),getString(R.string.health_type));
        task_details_rubik=new task_details(this,share.getCurrent_kid(),getString(R.string.rubik_type));
        task_details_sports=new task_details(this,share.getCurrent_kid(),getString(R.string.sport_type));
        course_id_rubik=task_details_rubik.getCourse_id();
        course_id_sport=task_details_sports.getCourse_id();
        course_id_health=task_details_health.getCourse_id();
        String end=dateFormat.format(endDate.getTimeInMillis());
        loadDate(end);
        selectedDate=endDate;

        horizontalCalendar = new HorizontalCalendar.Builder(activity_log.this, R.id.calendarView).range(startDate, endDate).datesNumberOnScreen(3).mode(HorizontalCalendar.Mode.DAYS).build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (selectedDate.getTimeInMillis() != date.getTimeInMillis()) {
                    selectedDate = date;

                    loadDate(dateFormat.format(date.getTime()));
                    Log.i(TAG,"date"+dateFormat.format(date.getTime()));

                }
            }
        });

    }
    public void loadDate(String date){
        Log.i(TAG,"date "+date);
        null_activity.setVisibility(View.GONE);
        boolean s=false;
        boolean r=false;
        boolean h=false;
        if(course_id_sport.equals(R.string.new_user)){
            sports.setVisibility(View.GONE);
        }else{
         level_task level_task=sql_sports.getTaskLevelFromDate(date);
         if(level_task.getLevel()==0){
             sports.setVisibility(View.GONE);
         }else{
             sports.setVisibility(View.VISIBLE);
             ArrayList<sport_exercise_class> exercise_classes;
             exercise_classes=sql_sports.readSports(level_task.getLevel(),level_task.getTask());

             level_sport.setText(Integer.toString(level_task.getLevel()));
             task_sport.setText(Integer.toString(level_task.getTask()));
             credits_sport.setText(Integer.toString(level_task.getCredits_earned()));
             s=true;
             for (int i=0; i<exercise_classes.size(); i++) {
                 LayoutInflater inflater = getLayoutInflater();
                 View vi = inflater.inflate(R.layout.ex_type_parent, null);
                 TextView ex_name=vi.findViewById(R.id.ex_name);
                 ex_name.setText(exercise_classes.get(i).getEx_name());
                 TextView ex_time=vi.findViewById(R.id.ex_time);
                 ex_time.setText(exercise_classes.get(i).getTime()+"S");
                 ex_sport.addView(vi);
             }

         }
        }
        if(course_id_health.equals(R.string.new_user)){
            health.setVisibility(View.GONE);
        }else{
            level_task level_task=sql_health.getTaskLevelFromDate(date);
            if(level_task.getLevel()==0){
                health.setVisibility(View.GONE);
            }else{
               health.setVisibility(View.VISIBLE);
                ArrayList<sport_exercise_class> exercise_classes;

                exercise_classes=sql_health.readhealth(level_task.getLevel(),level_task.getTask());

                level_health.setText(Integer.toString(level_task.getLevel()));
                task_health.setText(Integer.toString(level_task.getTask()));
                credits_health.setText(Integer.toString(level_task.getCredits_earned()));
                h=true;
                for (int i=0; i<exercise_classes.size(); i++) {
                    LayoutInflater inflater = getLayoutInflater();
                    View vi = inflater.inflate(R.layout.ex_type_parent, null);
                    TextView ex_name=vi.findViewById(R.id.ex_name);
                    ex_name.setText(exercise_classes.get(i).getEx_name());
                    TextView ex_time=vi.findViewById(R.id.ex_time);
                    ex_time.setText(exercise_classes.get(i).getTime()+"S");
                    ex_health.addView(vi);
                }

            }
        }
        if(course_id_rubik.equals(R.string.new_user)){
            rubik.setVisibility(View.GONE);
        }else{
            level_task level_task=sql_rubik.getTaskLevelFromDate(date);
            int level=level_task.getLevel();
            int task=level_task.getTask();
            Log.i(TAG,"task"+task);
            Log.i(TAG,"task"+level_task.getTask_type());
            if(level==0){
                rubik.setVisibility(View.GONE);
            }else{
                rubik.setVisibility(View.VISIBLE);
                String task_type=level_task.getTask_type();
                Log.i(TAG,task_type);
                if(task_type.equals(getString(R.string.one_one_type))){
                    Log.i(TAG,"inside"+getString(R.string.one_one_type));
                    one_one_class one_class=sql_rubik.get_one_one(level,task);
                    task_des_rubik.setText("Interactive Session On "+one_class.getDesciption());
                    r=true;
                }else if(task_type.equals(getString(R.string.upload_photo_type))){
                    Log.i(TAG,"inside"+getString(R.string.upload_photo_type));
                    upload_class upload=sql_rubik.get_upload_photo(level,task);
                    task_des_rubik.setText(upload.getDescription());
                    r=true;
                }else if(task_type.equals(getString(R.string.upload_video_type))){
                    Log.i(TAG,"inside"+getString(R.string.upload_video_type));
                    upload_class upload = sql_rubik.get_upload_video(level, task);
                    task_des_rubik.setText(upload.getDescription());
                    r=true;
                }else if(task_type.equals(getString(R.string.show_content_type))){
                    Log.i(TAG,"inside"+getString(R.string.show_content_type));
                    show_content_class showContentClass = sql_rubik.get_show_content(level, task);
                    task_des_rubik.setText(showContentClass.getDesciption());
                    r=true;
                }else if(task_type.equals(getString(R.string.show_video_type))) {
                    Log.i(TAG,"inside"+getString(R.string.show_video_type));
                    show_video_class show_videoClass = sql_rubik.get_show_video(level, task);
                    task_des_rubik.setText(show_videoClass.getDescription());
                    Log.i(TAG,show_videoClass.getDescription());
                    r=true;
                }
                level_rubik.setText(Integer.toString(level));
                task_rubik.setText(Integer.toString(task));
                credits_rubik.setText(Integer.toString(level_task.getCredits_earned()));
                r=true;


            }

        }
        if((!s)&&(!r)&&(!h)){
            null_activity.setVisibility(View.VISIBLE);
        }

    }

}
