package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class task_activity_rubik extends AppCompatActivity {
Fragment SelectedFragment;
int level;
int task;
    String task_type;
String TAG="task activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_rubik);
        shared share=new shared(this);
        sql_rubik sql=new sql_rubik(this,share.getKid1_id());
        Log.i(TAG,"current course type "+share.getCurrent_course_type());
        task_details rubik=new task_details(this,share.getCurrent_kid(),share.getCurrent_course_type());
        level=rubik.getCurrent_level();
        task=rubik.getCurrent_task();
        Log.i(TAG,"level "+level+ " task= "+task);


        task_type = sql.getTasktype(level, task);
        Log.i(TAG,"task_type"+task_type);
        if (task_type.equals(getString(R.string.one_one_type))) {
            if(rubik.getOne_one_Date(level,task).equals(getString(R.string.empty))){
                Intent intent=new Intent(task_activity_rubik.this,slot_book.class);
                one_one_class oneOneClass=sql.get_one_one(level,task);
                intent.putExtra("topic",oneOneClass.getDesciption());
                intent.putExtra("time",oneOneClass.getTime());
                startActivity(intent);
            }else {
                Log.i(TAG, "one_one");
                SelectedFragment = new one_one();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SelectedFragment).commit();

            }
        } else if (task_type.equals(getString(R.string.show_content_type))) {


            Log.i(TAG, "show_content_class");
            SelectedFragment = new show_content();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,SelectedFragment).commit();


        } else if (task_type.equals(getString(R.string.show_video_type))) {

            Log.i(TAG, "show_video_class");
            SelectedFragment = new show_video();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,SelectedFragment).commit();
        } else if (task_type.equals(getString(R.string.upload_photo_type))) {


            Log.i(TAG, "upload_photo");
            SelectedFragment = new upload_picture();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,SelectedFragment).commit();
        } else if (task_type.equals(getString(R.string.upload_video_type))) {

            Log.i(TAG, "upload_video");
            SelectedFragment = new upload_video();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,SelectedFragment).commit();

        }
    }

    @Override
    public void onBackPressed() {
        if(task_type.equals(getString(R.string.upload_photo_type))||task_type.equals(getString(R.string.upload_video_type))){
            showBack_Dialog();
        }else {
            finish();
            super.onBackPressed();
        }
    }
    public void showBack_Dialog(){
        final Dialog dialogSports = new Dialog(task_activity_rubik.this);
        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(true);
        dialogSports.setContentView(R.layout.dialog_back);
        Window window = dialogSports.getWindow();

        Log.i("main","pop_up_rubik");
        TextView leave=dialogSports.findViewById(R.id.leave);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSports.dismiss();
                finish();
                Log.i(TAG,"leave");
            }
        });
        TextView stay=dialogSports.findViewById(R.id.stay);
        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSports.dismiss();
                Log.i(TAG,"stay");
            }
        });

        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogSports.show();
    }



}

