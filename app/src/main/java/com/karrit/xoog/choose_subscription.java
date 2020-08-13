package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class choose_subscription extends AppCompatActivity {
    static Map<String, Object> task_data;
    String user_id;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_subscription);

        //---------------------------------------------------------UI----------------------------------------------------------------
        TextView title = findViewById(R.id.title2);
        db = FirebaseFirestore.getInstance();

        size(title,5,9,"with XOOG",2f);
        TextView head2 = findViewById(R.id.head2);
        TextView head3 = findViewById(R.id.head3);
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        TextView text3 = findViewById(R.id.text3);
        TextView text4 = findViewById(R.id.text4);
        TextView text5 = findViewById(R.id.text5);
        TextView text_bottom = findViewById(R.id.text_bottom);
        size(text_bottom,17,41,"CHOOSE YOUR PLAN\nTO AVAIL EXCITING OFFERS",0.65f);
        String t1="Daily Activities\nEngaging kids";
        size(text1,0,16,"Daily Activities\nEngaging kids",1.3f);
        size(text2,0,14,"Sports Fitness\nTo improve performance and strength",1.3f);
        size(text3,0,14,"Rubiks Puzzles\nto improve concentration and coordination",1.3f);
        size(text4,0,19,"Customised Programs\nDeveloped as per the kids requirement",1.3f);
        size(text5,0,18,"Get Exciting Gifts\nwith points earned through activitites",1.3f);
        size(head2,7,17,"7 Days\nFree Trial",0.5f);
        size(head3,4,15,"XOOG\nMembership",0.5f);

        //---------------------------------------------------Intent----------------------------------------------------------------------
        Intent intent= getIntent();
        shared share =new shared(this);
        user_id = share.getUser_id();
        Log.i("user_id",share.getUser_id());

    }
    public void size(TextView textView,int start, int end,String s,float size){
        SpannableString span = new SpannableString(s);
        span.setSpan(new RelativeSizeSpan(size),start,end,SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(span);
    }

    public void free_trial_function(View view) {
        task_data= new HashMap<>();
        task_data.put("current_level",1);
        task_data.put("current_task",1);
        task_data.put("DOJ",Timestamp.now());
        task_data.put("customized_program",false);
        task_data.put("change_program",false);
        task_data.put("course_id","trial");
        task_data.put("current_credits",0);



        //updating Firebase Task
        db.collection("phone_num").document(user_id).update("login",true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Free Trial","uploaded");

            }
        });
        db.collection("users").document(user_id).collection(user_id+"1").document("account_details").update("course_id","trial");

        db.collection("users").document(user_id).collection(user_id+"1").document("task_trial").set(task_data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("task_data","uploaded");

            }});
        //Store in Internal Data
      task_details task_details = new task_details(this,user_id+"1","trial");
      task_details.setExp(7);
      task_details.setChange_program(false);
      task_details.setCurrent_credits(0);
      task_details.setCurrent_level(1);
      task_details.setCurrent_task(1);
      task_details.setCustomized_program(false);
      task_details.apply();
      //-----------updating process------------------
      shared shared= new shared(this);
      shared.setCourses(1,1);
      shared.setCourse_id_1("trail",1);
      shared.setProcess(2);
      shared.apply();

        //Starting Activity
        Intent i = new Intent(choose_subscription.this,Parent_kid.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);


    }

    public void subscription_function(View view) {
        Intent intent = new Intent(choose_subscription.this,account_subscipe.class);
        intent.putExtra("kid_number",1);
        startActivity(intent);
    }
}
