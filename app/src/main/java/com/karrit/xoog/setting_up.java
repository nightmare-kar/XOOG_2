
package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class setting_up extends AppCompatActivity {
    String kid__user_id;
    String course_type;
    Map<String,Object> task_data;
    shared share;
    String user_id;
    String TAG="settings";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up);
        Intent intent=getIntent();
        share=new shared(this);
        kid__user_id=share.getCurrent_kid();
        Log.i(TAG,kid__user_id);
        course_type=intent.getStringExtra("course_type");
        Log.i(TAG,"course_id"+course_type);
        Log.i("kid_number","kif"+kid__user_id);
        db=FirebaseFirestore.getInstance();
        user_id=share.getUser_id();
        //updating Firebase Task
        //Store in Internal Data
        if(course_type.equals(getString(R.string.sport_type))){
            task_details task_details = new task_details(this,kid__user_id,course_type);
            task_details.setCourse_id(getString(R.string.sport_course_id));
            task_details.setExp(30);
            task_details.createdb();
            task_details.setCourse_id_Cloud(getString(R.string.sport_course_id));
            task_details.apply();
        }else if(course_type.equals(getString(R.string.rubik_type))){
            Log.i(TAG,"inside if");
            task_details task_details = new task_details(this,kid__user_id,course_type);
            task_details.setCourse_id(getString(R.string.rubik_course_id));
            task_details.createdb();
            task_details.setCourse_id_Cloud(getString(R.string.rubik_course_id));
            task_details.setExp(30);
            task_details.apply();
            Log.i(TAG,"inside if");
            Log.i(TAG,"inside course type"+ task_details.getCourse_id());

        }else if(course_type.equals(getString(R.string.special_type))){

            task_details rubik=new task_details(this,kid__user_id,getString(R.string.sport_type));
            if(rubik.getCourse_id().equals(R.string.new_user)){
                rubik.newTask(getString(R.string.rubik_course_id),30);
            }else {
                rubik.setCourse_id(getString(R.string.rubik_course_id));
                rubik.setSpecialExp(30);
                rubik.createdb();
                rubik.setCourse_id_Cloud(getString(R.string.rubik_course_id));
                rubik.apply();
            }
            task_details sports = new task_details(this,kid__user_id,getString(R.string.rubik_type));
            if(sports.getCourse_id().equals(R.string.new_user)){
                sports.newTask(getString(R.string.rubik_course_id),30);
            }else {
                sports.setCourse_id(getString(R.string.sport_course_id));
                sports.createdb();
                sports.setCourse_id_Cloud(getString(R.string.sport_course_id));
                sports.setSpecialExp(30);
                sports.apply();
            }
        }

        //-----------updating process------------------

//update subscribe extra ------------------------------------------------------------------------------------------------------------------------//------------------------------------updating DOJ------------------------------------------------------

        //-----------------------------------updating share----------------------------------------------------
        //Starting Activity
        Intent i = new Intent(this,Home_page.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

//        sql_rubik sql= new sql_rubik(getApplicationContext(),course_id,share.getKids_id(kid_number));
      //  sql.download_next_task();
    }

}
