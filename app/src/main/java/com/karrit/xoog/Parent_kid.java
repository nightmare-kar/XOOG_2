package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StatFs;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;



public class Parent_kid extends AppCompatActivity {
    Group group_2;
    Button add;
    String TAG = "Parent-kid";
    TextView kid_1,kid_2;
    shared share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_kid);
        share = new shared(this);


        Log.i("parent_kid","entered");

        group_2=findViewById(R.id.group_kid_2);







       /* Intent intenttask=new Intent(this,Task_finish.class);
        share.setCurrent_course_type(getString(R.string.rubik_type));
        share.apply();
        startActivity(intenttask);
        Log.i(TAG,"sql get task number"+sqlRubik.getTaskNumber(2));
        Log.i(TAG,"sql get bonus"+sqlRubik.getBonusCredits(2));
      /*  sql_health sqlHealth=new sql_health(this,share.getCurrent_kid());
        sql_leaderBoard sqlLeaderBoard=new sql_leaderBoard(this,share.getCurrent_kid());


       // sqlLeaderBoard.downloadClass();
        //sqlLeaderBoard.downloadOverall();
        //sqlLeaderBoard.downloadSchool();


        share.setCurrent_kid(share.getKid1_id());
        share.apply();
        task_details taskDetails_sport=new task_details(this,share.getKid1_id(),getString(R.string.sport_type));
        taskDetails_sport.setCourse_id(getString(R.string.sport_course_id));
        taskDetails_sport.setExp(-2);
        taskDetails_sport.apply();

        Log.i(TAG,"sql position "+sqlLeaderBoard.getPositionClass()+" "+sqlLeaderBoard.getPositionOverall()+" "+sqlLeaderBoard.getPositionSchool());

sql_rubik sql_rubik=new sql_rubik(this,share.getCurrent_kid());
//sql_rubik.download_next_task(1);
sql_rubik.updateDate(1,1);
sql_rubik.updateCreditsEarned(1,1,50);

sqlHealth.updateDate(1,1);
sqlHealth.updateCreditsEarned(1,1,30);
        String table_name_Overall="overall_leaderBoard";

       String table_name_School;
       String table_name_Class;
        table_name_Class="class_"+share.getCurrent_kid();
        table_name_School="school_"+share.getCurrent_kid();

sqlLeaderBoard.setLastUpdate(table_name_Overall,-2);
sqlLeaderBoard.setLastUpdate(table_name_Class,-2);
sqlLeaderBoard.setLastUpdate(table_name_School,-2);

       /* for(int i=0;i<50;i++){
          HashMap<String,Object> hashMap=new HashMap<>();
          hashMap.put("name","name"+i);
          hashMap.put("xcore",i*20);
          hashMap.put("level",i);
          if(i%2==0){
              hashMap.put("school_code","yrtv");
              if(i%4==0){
                  hashMap.put("grade",Integer.toString(5));
              }else{
                  hashMap.put("grade",Integer.toString(4));
              }
          }else{
              hashMap.put("grade",Integer.toString(0));
              hashMap.put("school_code","none");
          }
          FirebaseFirestore.getInstance().collection("leaderboard").document("user"+i).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Log.i(TAG,"upload complete"+1);
              }
          });
        }
*/



/*task_details task_details=new task_details(this,share.getKid1_id(),getString(R.string.sport_type));
task_details.setCourse_id(getString(R.string.new_user));
task_details.setCurrent_task(1);
task_details.setCurrent_level(1);
task_details.setTaskUnlock(-1);
task_details.apply();
Log.i(TAG,"task unlock "+ task_details.getTaskUnlock()+task_details.checkTaskUnlock());

Log.i(TAG,task_details.getOne_one_link(task_details.getCurrent_level(),task_details.getCurrent_task()));*/
       add=findViewById(R.id.add_button);
        kid_1=findViewById(R.id.kid1Text);
        kid_2=findViewById(R.id.kid2Text);
       /* Intent intent1=new Intent(this,Task_finish.class);
        share.setCurrent_course_type(getString(R.string.rubik_type));
        share.apply();
        intent1.putExtra("course_type",getString(R.string.rubik_type));
        intent1.putExtra("upgrade",true);
       // startActivity(intent1);*/


 Calendar c=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
//------------------------------go to Parent Page--------------------------------------------------
        ImageView parent=findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Parent_kid.this,Parent.class);
                startActivity(intent);
            }
        });
        Log.i(TAG,share.getKid1_id());

        task_details taskDetails=new task_details(this,share.getKid2_id(),getString(R.string.health_type));


        Log.i(TAG,"mentor id "+taskDetails.getMentor_id());
        Log.i(TAG,"current level"+ taskDetails.getCurrent_level());
        Log.i(TAG,"current task"+ taskDetails.getCurrent_task());
        Log.i(TAG,"current task number"+ taskDetails.getCurrent_task_number());
        Log.i(TAG,"current exp"+ taskDetails.getExp());
        Log.i(TAG,"current total level"+ taskDetails.getTotal_level());
        Log.i(TAG,"current course_id"+ taskDetails.getCourse_id());
        task_details task=new task_details(this,share.getKid2_id(),getString(R.string.rubik_type));

        Log.i(TAG,"mentor id "+task.getMentor_id());
        Log.i(TAG,"current level"+ task.getCurrent_level());
        Log.i(TAG,"current task"+ task.getCurrent_task());
        Log.i(TAG,"current task number"+ task.getCurrent_task_number());
        Log.i(TAG,"current exp"+ task.getExp());
        Log.i(TAG,"current total level"+ task.getTotal_level());
        Log.i(TAG,"rubik course_id"+ task.getCourse_id());

//------------------------------------------------------------------------------------------------
//---------------------------------reward system--------------------------------------------------
     /*   FirebaseFirestore db=FirebaseFirestore.getInstance();
      DocumentReference doc= db.collection("users").document(share.getUser_id()).collection(share.getKid1_id()).document("payments");
     doc.collection("premium").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
         @Override
         public void onComplete(@NonNull Task<QuerySnapshot> task) {
             if (task.isSuccessful()) {
                 if(task.getResult().size() > 0) {
                     for (DocumentSnapshot document : task.getResult()) {
                         Log.i(TAG, "Room already exists, start the chat");

                     }
                 } else {
                     grantRewards();
                     Log.i(TAG, "room doesn't exist create a new room");

                 }
             } else {
                 Log.i(TAG, "Error getting documents: ", task.getException());

             }
         }
     });*/


    //----------------------------------------------------

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        SimpleDateFormat dateFormat_1 = new SimpleDateFormat("dd_MM");
try {
    Calendar d1 = Calendar.getInstance();

    Date d = dateFormat.parse("28_07_2020");
    d1.setTime(d);
    Calendar dnow = Calendar.getInstance();
    if (dnow.get(Calendar.DATE)== d1.get(Calendar.DATE)) {
        Log.i(TAG, "now equal");

    }else if(dnow.get(Calendar.DATE) > d1.get(Calendar.DATE)){
        Log.i(TAG,"d is big");
    }else{
        Log.i(TAG,"d is less");
    }
    String time="04:00 pm";
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    Date dtime=timeFormat.parse(time);
    Calendar timeC=Calendar.getInstance();
    timeC.setTime(dtime);
    if(timeC.get(Calendar.HOUR)>dnow.get(Calendar.HOUR)){
        Log.i(TAG,"dtime is bigger");
    }else{
        Log.i(TAG,"now is bigger");
    }

}catch (Exception e){
    e.printStackTrace();
}

///data/user/0/com.example.xoog/app_xoog_excercise/ex_1.txt

///data/user/0/com.example.xoog/app_xoog_excercise/ex_5.txt



    /*    for(int i=1;i<=4;i++) {
            String task_type = sql.getTasktype(level, i);
            Rubik_task_table_class task_t1 = sql.get_task_table(level, i);
            Log.i(TAG, "task_table");
            Log.i(TAG, task_t1.getTask_type());
            Log.i(TAG, "credits " + task_t1.getCredits());
            Log.i(TAG, "rest " + task_t1.getRest());
            if (task_type.equals(getString(R.string.one_one_type))) {

                one_one_class one = sql.get_one_one(level, i);
                Log.i(TAG, "one_one_class");
                Log.i(TAG, one.getDesciption());
                Log.i(TAG, "time " + one.getTime());
            } else if (task_type.equals(getString(R.string.show_content_type))) {

                show_content_class show = sql.get_show_content(level, i);
                Log.i(TAG, "show_content_class");
                Log.i(TAG, show.getDesciption());

            } else if (task_type.equals(getString(R.string.show_video_type))) {
                show_video_class show = sql.get_show_video(level, i);
                Log.i(TAG, "show_video_class");
                Log.i(TAG, show.getDescription());
                Log.i(TAG, show.getLink());
            } else if (task_type.equals(getString(R.string.upload_photo_type))) {

                upload_class photo = sql.get_upload_photo(level, i);
                Log.i(TAG, "upload_photo");
                Log.i(TAG, photo.getDescription());
                Log.i(TAG, "number pictures" + photo.getNumber_pictures() + photo.getShow_scramble());
            } else if (task_type.equals(getString(R.string.upload_video_type))) {
                upload_class video = sql.get_upload_video(level, i);
                Log.i(TAG, "upload_photo");
                Log.i(TAG, video.getDescription());
                Log.i(TAG, "number pictures" + video.getNumber_pictures() + video.getShow_scramble());
            }
        }*/






//nCATlpR7MyTjES7mhx0qhhdbawx22
//nCATlpR7MyTjES7mhx0qhhdbawx22
//nCATlpR7MyTjES7mhx0qhhdbawx22
//nCATlpR7MyTjES7mhx0qhhdbawx22
     //   sql_rubik sql= new sql_rubik(getApplicationContext(),course_id,share.getKids_id(kid_number));
       // sql.download_next_task();
      //  sql_sports sql_sports=new sql_sports(getApplication(),share.getKids_id(kid_number));
        //sql_sports.download_next_task();
        //task_details task = new task_details(this,share.getKid1_id(),"sport");
        //Log.i("level and task","he"+task.getCurrent_task()+task.getCurrent_level());


  add.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Intent i=new Intent(Parent_kid.this,account_subscipe.class);
          i.putExtra("kid_number",2);
          startActivity(i);
      }
  });



    }


    public void sendToGamePage(View view) {
        Intent intent = new Intent(getApplicationContext(),Home_page.class);
        shared share=new shared(this);
        switch (view.getId()){
            case R.id.kid1:
                share.setCurrent_kid(share.getKid1_id());
                share.apply();
                Log.i(TAG,"kid 1"+share.getCurrent_kid());
                break;
            case R.id.kid2:

                share.setCurrent_kid(share.getKid2_id());
                Log.i(TAG,"kid 2 get"+share.getKid2_id());
                share.apply();
                Log.i(TAG,"kid 2"+share.getCurrent_kid());
                break;
            default:
                Log.i("select anything","choose properly");
                break;

        }

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        account_details accountDetails=new account_details(this,share.getKid1_id());
        Log.i("kid1",accountDetails.getKid_name());
        Log.i("kid1",accountDetails.getPhone_number());
        Log.i("kid1",accountDetails.getEmail());

        kid_1.setText(accountDetails.getKid_name());
        account_details account=new account_details(this,share.getKid2_id());
        Log.i(TAG,share.getKid2_id());
        Log.i(TAG,"name"+accountDetails.getKid_name());
        Log.i(TAG,"email"+account.getEmail());
        Log.i(TAG,"doj"+account.getDOJ());
        Log.i(TAG,"bith"+account.getDOB());
        Log.i(TAG,"grade"+account.getGrade());
        Log.i(TAG,"gender"+account.getGender());
        Log.i(TAG,"xcore"+account.getXcore());
        Log.i(TAG,"xcash"+account.getXcash());
        int i=share.getKids_number();
        if(i==1){
            group_2.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);

        }else if(i==2) {
            group_2.setVisibility(View.VISIBLE);
            kid_2.setText(account.getKid_name());
            add.setVisibility(View.GONE);

        }
        super.onStart();
    }
}
