package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;


import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;




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


        group_2=findViewById(R.id.group_kid_2);



       add=findViewById(R.id.add_button);
        kid_1=findViewById(R.id.kid1Text);
        kid_2=findViewById(R.id.kid2Text);
       /* Intent intent1=new Intent(this,Task_finish.class);
        share.setCurrent_course_type(getString(R.string.rubik_type));
        share.apply();
        intent1.putExtra("course_type",getString(R.string.rubik_type));
        intent1.putExtra("upgrade",true);
       // startActivity(intent1);*/


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



//------------------------------------------------------------------------------------------------
//---------------------------------reward system--------------------------------------------------




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
