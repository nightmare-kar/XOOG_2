package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class account_subscipe extends AppCompatActivity{
    String user_id;
    EditText name,email;
    String user_2_id;
    ImageView close;
    TextView age;
    private String TAG="account_subscripe";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String[] gender_string;
    String phoneNum;
    int selected_grade;
    FirebaseFirestore db;
    int selected_gender;
    Spinner gradeSpinner;
    LinearLayout calendar;
     EditText grade;

    account_details account;
    String selected_date;
    shared share;
    int kid_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_subscipe);
        selected_date="";
        name = findViewById(R.id.Name);

        email = findViewById(R.id.email);
        age = findViewById(R.id.age);

       gradeSpinner=findViewById(R.id.grade);
        db= FirebaseFirestore.getInstance();
        Intent intent= getIntent();
        kid_number=intent.getIntExtra("kid_number",0);
        Log.i("kid_number",Integer.toString(kid_number));

        share=new shared(this);

        if(kid_number==1){
            Log.i(TAG,"enter kid 1");
        account=new account_details(this,share.getKid1_id());}
        else {
            user_2_id=share.getUser_id()+"2";
            Log.i(TAG,"enter kid 2");
            account = new account_details(this,user_2_id);
        }
        Log.i("just","for fun");
        Log.i(TAG,"kid id"+account.getKid_name());

        //------------setting phone_num------------




        //-------------Setting Date-----------------------------------------------------
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                selected_date=date;

                age.setText(date);
            }
        };


        calendar=findViewById(R.id.l3);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

     //---------------setting spinner for Gender------------------------------
        Spinner spinner = findViewById(R.id.gender);
        selected_gender=10;
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(account_subscipe.this, R.layout.spinner_text, getResources().getStringArray(R.array.gender) );
        langAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
         spinner.setAdapter(langAdapter);

        gender_string=getResources().getStringArray(R.array.gender);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                       selected_gender=0;
                        break;
                    case 1:

                        selected_gender=1;
                        break;
                    case 2:
                       selected_gender=2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
        //Spinner for grade-----------------------------------------------------
        selected_grade=100;
        ArrayAdapter<CharSequence> gradeAdapter = new ArrayAdapter<CharSequence>(account_subscipe.this, R.layout.spinner_text, getResources().getStringArray(R.array.grade) );
        langAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        gradeSpinner.setAdapter(gradeAdapter);

        gender_string=getResources().getStringArray(R.array.gender);
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_grade=position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
        //clickable terms and conditions-----------------
        SpannableString ss = new SpannableString("By creating an account or logging in, you agree to XOOG's Terms and Conditions");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(account_subscipe.this, TermsAndConditions.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

            }
        };
        ss.setSpan(clickableSpan, 58, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) findViewById(R.id.clickable_text);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

        //------------------------------------setting on back arrow-------------------------------------
        close=findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }





    public void choose_plan(View view) {
        Button choose=findViewById(R.id.choose);
        NetworkCheck networkCheck=new NetworkCheck();
       String[] gradeString=getResources().getStringArray(R.array.grade);
        Log.i(TAG,"choose plan clicked");
       String name_string = name.getText().toString();

        String email_string = email.getText().toString();
        hideKeyBoard();
        if(name_string.isEmpty()||email_string.isEmpty()||(selected_gender==10)||selected_date.isEmpty()||selected_grade==100){
            Toast.makeText(getApplicationContext(),"Enter all Details",Toast.LENGTH_LONG).show();

        }
        else {
            if(networkCheck.internetConnectionAvailable(5000)) {
                Log.i(TAG,"network available inside");
                choose.setClickable(false);
                Log.i(TAG, "inside else");
                HashMap<String, Object> account_data = new HashMap<String, Object>();
                if (kid_number == 2) {
                    Log.i(TAG, "kidnumber 2");
                    share.setKid2_id(user_2_id);
                    share.setKids_number(2);
                    share.apply();

                    account_details kid1 = new account_details(this, share.getKid1_id());
                    phoneNum=kid1.getPhone_number();
                    account_data.put("kid_name", name_string);
                    account_data.put("phone_num", phoneNum);
                    account_data.put("email", email_string);
                    account_data.put("gender", gender_string[selected_gender]);
                    account_data.put("DOB", selected_date);
                    account_data.put("xcore", 500);
                    account_data.put("xcash", 500);
                    account_data.put("grade", gradeString[selected_grade]);
                    account_data.put("referredBy", getString(R.string.empty));
                    account_data.put("schoolId", getString(R.string.empty));
                    account_data.put("AcademyId", getString(R.string.empty));
                    account_data.put("DOJ", Timestamp.now());
                    account_data.put("update",false);

                    account=new account_details(this,share.getKid2_id());
                    account.setPhone_number(phoneNum);
                    account.setRefferedBy(getString(R.string.empty));
                    account.setAcademyId(getString(R.string.empty));
                    account.setSchoolId(getString(R.string.empty));
                    Calendar c=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yy");
                    String format=simpleDateFormat.format(c.getTime());
                    account.setDOJ(format);
                    account.apply();


                    db.collection("users").document(share.getUser_id()).collection(share.getKids_id(kid_number)).document("account_details").set(account_data).addOnCompleteListener(new OnCompleteListener<Void>() {

                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("account_details", "updated");
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                    db.collection("phone_num").document(share.getUser_id()).update("kids_number", 2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("phone_data", "uploaded");
                        }
                    });
                } else {

                    String referId = share.getReferedby();
                    if(referId.equals(share.getKid1_id())){
                        referId=getString(R.string.empty);
                    }
                    MyFirebaseInstanceIdService myFirebaseInstanceIdService=new MyFirebaseInstanceIdService();
                    myFirebaseInstanceIdService.sendTokenToServer(account_subscipe.this,share.getToken());
                    String schoolId=share.getSchoolId();
                    String AcademyId=share.getAcademyId();
                    Log.i(TAG, "referId" + referId);
                    Log.i(TAG, "kidnumber 1");
                    Log.i(TAG,"school id"+schoolId);
                    Log.i(TAG,"academy id"+AcademyId);
                    account_data.put("kid_name", name_string);
                    account_data.put("phone_num", account.getPhone_number());
                    account_data.put("email", email_string);
                    account_data.put("gender", gender_string[selected_gender]);
                    account_data.put("DOB", selected_date);
                    account_data.put("referredBy", referId);
                    account_data.put("grade", gradeString[selected_grade]);
                    account_data.put("schoolId", schoolId);
                    account_data.put("AcademyId", AcademyId);
                    account_data.put("xcore", 500);
                    account_data.put("xcash", 500);
                    account_data.put("update",false);
                    account_data.put("DOJ", Timestamp.now());
                    Log.i(TAG, "reffere id");
                    if(!schoolId.equals(getString(R.string.empty))){
                        HashMap<String,Object> map_school=new HashMap<>();
                        map_school.put("kid_id",share.getKid1_id());
                        map_school.put("DOT",Timestamp.now());
                        db.collection("institutes").document("school").collection(schoolId).add(map_school).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.i(TAG,"on complete");
                            }
                        });
                    }
                    if(!AcademyId.equals(getString(R.string.empty))){
                        HashMap<String,Object> map_school=new HashMap<>();
                        map_school.put("kid_id",share.getKid1_id());
                        map_school.put("DOT",Timestamp.now());
                        db.collection("institutes").document("academy").collection(schoolId).add(map_school).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.i(TAG,"on complete");
                            }
                        });
                    }

                    db.collection("users").document(share.getUser_id()).collection(share.getKids_id(kid_number)).document("account_details").set(account_data).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("account_details", "updated");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                    account.setRefferedBy(referId);
                    account.setAcademyId(AcademyId);
                    Calendar c=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yy");
                    String format=simpleDateFormat.format(c.getTime());
                    account.setDOJ(format);
                    account.setSchoolId(schoolId);
                    account.apply();

                    HashMap<String, Object> phone_data = new HashMap<>();
                    phone_data.put("phone_number", account.getPhone_number());
                    phone_data.put("user_id", share.getUser_id());

                    phone_data.put("kids_number", kid_number);
                    db.collection("phone_num").document(share.getUser_id()).set(phone_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("phone_data", "uploaded");
                        }
                    });
                    HashSet<String> set=new HashSet<>();
                    for(int i=1;i<43;i++){
                        set.add(Integer.toString(i));
                    }
                    set.add(getString(R.string.music));
                    // sql.deleteVideo(set);
                    Intent intent=new Intent(this,VideoService.class);
                    intent.putExtra("mySet",set);
                    startService(intent);
                    share.setCurrent_kid(share.getKid1_id());
                    share.setProcess(2);
                    share.apply();
                }
                account.setDOB(selected_date);
               account.setGrade(gradeString[selected_grade]);
                account.setEmail(email_string);
                account.setKid_name(name_string);
                account.setXcash(500);
                account.setXcore(500);
                account.setGender(gender_string[selected_gender]);
                account.apply();
                Log.i(TAG,"acount name"+account.getKid_name());
                Log.i(TAG,"acount name"+account.getEmail());
                Log.i(TAG,"acount name"+account.getGender());
                Log.i(TAG,"acount name"+account.getDOJ());
                Log.i(TAG,"account anme"+account.getPhone_number());

                account_details accountKid2=new account_details(this,share.getKid2_id());
                Log.i(TAG,"acountkids 2 name"+accountKid2.getKid_name());
                Log.i(TAG,"acountkids 2 email"+accountKid2.getEmail());
                Log.i(TAG,"acountkids 2 gender"+accountKid2.getGender());
                Log.i(TAG,"acountkids 2 DOJ"+accountKid2.getDOJ());
                Log.i(TAG,"acountkids 2 grade"+accountKid2.getGrade());


                HashMap<String,Object> leader_map=new HashMap<>();
                leader_map.put("grade",gradeString[selected_grade]);
                leader_map.put("name",name_string);
                leader_map.put("xcore",0);
                leader_map.put("school_code",account.getSchoolId());
                FirebaseFirestore.getInstance().collection("leaderboard").document(share.getKids_id(kid_number)).set(leader_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG,"leaderboard uploaded");
                    }
                });

                sql_rubik sql=new sql_rubik(account_subscipe.this,share.getKids_id(kid_number));
                sql.Download_all_tasks();
                sql_sports sql_sports=new sql_sports(account_subscipe.this,share.getKids_id(kid_number));
                sql_sports.download_next_task(1,1);
                sql_health sql_health=new sql_health(account_subscipe.this,share.getKids_id(kid_number));
                sql_health.download_next_task(1,1);
                Log.i(TAG,"account schoolid" +account.getSchoolId());
                if(account.getSchoolId().equals(getString(R.string.empty))){
                Intent intent = new Intent(this, Parent_kid.class);
                Log.i(TAG,"account school empty");
                finish();
                    intent.putExtra("kid_number", kid_number);
                    startActivity(intent);}
                else {
                    Log.i(TAG,"account school not empty");
                    Intent intent = new Intent(this, School_program.class);
                    finish();
                    startActivity(intent);

                }
            }else {
                Log.i(TAG,"no internet");
                Toast.makeText(account_subscipe.this,"Poor Internet Connection",Toast.LENGTH_LONG).show();
            }

        }


    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                mDateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    public void hideKeyBoard(){
        if(this.getCurrentFocus()!=null) {
            InputMethodManager inputManager = (InputMethodManager) account_subscipe.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }


}
//get emailnCATlpR7MyTjES7mhx0qhhdbawx22 fuk
//get emailnCATlpR7MyTjES7mhx0qhhdbawx22 mail
