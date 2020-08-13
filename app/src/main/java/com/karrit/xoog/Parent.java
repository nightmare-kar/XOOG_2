package com.karrit.xoog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;


import java.util.zip.GZIPInputStream;

public class Parent extends AppCompatActivity {
    CollectionReference db;
    LinearLayout linearLayout_fact;
    ListenerRegistration listenerRegistration;
   private String TAG="parent";
   TextView xcash,xcore;
   TextView progress;
   shared share;
   TextView change_kid;
   account_details accountDetails;
   TextView book;
   Group work;
   TextView work_sport,work_health, work_rubik;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        db=FirebaseFirestore.getInstance().collection("fact");
        linearLayout_fact=findViewById(R.id.linear_fact);
        ImageView close=findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final ImageView activity_log=findViewById(R.id.activity_im);
        activity_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Parent.this,activity_log.class);
                startActivity(intent);
            }
        });
        share=new shared(this);
        accountDetails=new account_details(this,share.getCurrent_kid());
        xcash=findViewById(R.id.xcash);
        xcore=findViewById(R.id.xcore);
        xcash.setText(Integer.toString(accountDetails.getXcash()));
        xcore.setText(Integer.toString(accountDetails.getXcore()));
        progress=findViewById(R.id.kid_progress);

        String name=accountDetails.getKid_name()+"'s Progress";
        progress.setText(name.toUpperCase());

        change_kid=findViewById(R.id.change_kid);
        if(share.getKids_number()==2){
            change_kid.setVisibility(View.VISIBLE);
        }else {
            change_kid.setVisibility(View.INVISIBLE);
        }
        change_kid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (share.getKids_number() == 2) {
                    if (share.getCurrent_kid() == share.getKid1_id()) {
                        share.setCurrent_kid(share.getKids_id(2));
                        share.apply();
                        finish();
                        startActivity(getIntent());

                    } else if (share.getCurrent_kid() == share.getKid2_id()) {
                        share.setCurrent_kid(share.getKids_id(1));
                        share.apply();
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        });
        //-------------book------------------------
        book=findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Parent.this,choose_plan.class);
                startActivity(intent);
            }
        });

       //------------------------set work------------------------
        work=findViewById(R.id.group_work);
        work_health=findViewById(R.id.work_sub_health);
        work_sport=findViewById(R.id.work_sub_sport);
        boolean show_also=false;
        work_rubik=findViewById(R.id.work_sub_rubik);
        task_details taskDetails_rubik=new task_details(this,share.getCurrent_kid(),getString(R.string.rubik_type));
        task_details taskDetails_health=new task_details(this,share.getCurrent_kid(),getString(R.string.health_type));
        task_details taskDetails_sport=new task_details(this,share.getCurrent_kid(),getString(R.string.sport_type));
        if(taskDetails_health.getCourse_id()==getString(R.string.health_course_id)){
            work_health.setVisibility(View.GONE);
        }else {
            show_also=true;
        }
        Log.i(TAG,"sport type "+taskDetails_sport.getCourse_id());
        if(taskDetails_sport.getCourse_id()==getString(R.string.sport_course_id)){
            work_sport.setVisibility(View.GONE);
        }else {
            show_also=true;
        }
        if(taskDetails_rubik.getCourse_id()==getString(R.string.rubik_course_id)){
            work_rubik.setVisibility(View.GONE);
        }else {
            show_also=true;
        }
        if(!show_also){
            work.setVisibility(View.GONE);
        }



    }

    @Override
    protected void onStart() {
      listenerRegistration=  db.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                Log.i(TAG,"on event");
                linearLayout_fact.removeAllViews();
                loadData(querySnapshot);
            }
        });
        super.onStart();
    }
    public void loadData(QuerySnapshot querySnapshot){

        LayoutInflater inflater=getLayoutInflater();

        for (QueryDocumentSnapshot doc : querySnapshot) {
            TextView view =(TextView) inflater.inflate(R.layout.text_parent_fact, null);
            String des="- "+doc.getString("des").toUpperCase();
                view.setText(des);
            linearLayout_fact.addView(view);

        }

    }

    @Override
    protected void onPause() {
        listenerRegistration.remove();
        super.onPause();
    }
}
