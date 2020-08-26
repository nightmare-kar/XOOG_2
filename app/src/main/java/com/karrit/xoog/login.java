package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hbb20.CountryCodePicker;

import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity implements TextWatcher {
    static Map<String, Object> user_data;
    static String user_id;
    private String TAG="login";
    TextView textViewPhone;
    ProgressBar progressBar;
    ImageView go_button;
    Button verifyNow;
    EditText referral;
    int count;
    ImageView imageView;
    private FirebaseAuth mAuth;
    boolean schoolBool;
    private FirebaseUser mCurrentUser;
    String complete_phone_number;
    TextView error;
    TextView resendText,resendTimer;
    boolean phonrNum;
    String Schoolid;
    private EditText mCountryCode;
    Group phone, otp;
    private EditText mPhoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private String mAuthVerificationId;
    CountryCodePicker ccp;
    CountDownTimer countDownTimer;
    FirebaseFirestore db;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    ImageView close;
    boolean b;
    shared share;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        schoolBool=false;
        b = false;
        phonrNum=true;
        count=0;
        Intent intent=getIntent();


        referral=findViewById(R.id.referral);
        phone = findViewById(R.id.group1);
        error=findViewById(R.id.error_verification);
        resendText=findViewById(R.id.ResendOtp);
        resendTimer=findViewById(R.id.ResendOtpTimer);
        db = FirebaseFirestore.getInstance();
        otp = findViewById(R.id.group2);
        textViewPhone=findViewById(R.id.textVerificationPhone);
        imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.login_up);
        int newHeight = (int) (getWindowManager().getDefaultDisplay().getHeight() * (1.1));
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        progressBar=findViewById(R.id.progress_bar);
        go_button=findViewById(R.id.go_button);
        verifyNow=findViewById(R.id.verify_now);
        otp1.addTextChangedListener(this);
        otp2.addTextChangedListener(this);
        otp3.addTextChangedListener(this);
        otp4.addTextChangedListener(this);
        otp5.addTextChangedListener(this);
        otp6.addTextChangedListener(this);
        close=findViewById(R.id.imageView4);
        Log.i("onCreate", "done");
        ccp = (CountryCodePicker) findViewById(R.id.Country);

        share = new shared(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard();
            }
        });


//double check my math, this should be right, though
        int newWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() * (1.75));
        ConstraintLayout constraintLayout = findViewById(R.id.cons);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//Use RelativeLayout.LayoutParams if your parent is a RelativeLayout
        imageView.getLayoutParams().width=newWidth;
        imageView.getLayoutParams().height=newHeight;
      /*  ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                newWidth, newHeight);
        imageView.setLayoutParams(params);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.setHorizontalBias(R.id.imageView2, 0.45f);
     /*   constraintSet.connect(R.id.imageView2, ConstraintSet.BOTTOM, R.id.guideline8, ConstraintSet.TOP, 50);

        constraintSet.connect(R.id.imageView2, ConstraintSet.START, R.id.cons, ConstraintSet.START, 0);
        constraintSet.connect(R.id.imageView2, ConstraintSet.END, R.id.cons, ConstraintSet.END, 0);


        constraintSet.applyTo(constraintLayout);

      */

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCountryCode = findViewById(R.id.countrycode);

        mPhoneNumber = findViewById(R.id.phonenumber);
        ccp.registerCarrierNumberEditText(mPhoneNumber);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Log.i(TAG, "verification comepleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Verification Failed, please try again.", Toast.LENGTH_LONG);

                Log.i(TAG, "verification failed");
                e.printStackTrace();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    error.setText("Invalid Phone Number");
                    error.setVisibility(View.VISIBLE);
                    go_button.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Invalid credential: "
                            + e.getLocalizedMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Log.d(TAG, "SMS Quota exceeded.");
                }

            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                phone.setVisibility(View.GONE);
                otp.setVisibility(View.VISIBLE);
                mAuthVerificationId = s;
                resendToken=forceResendingToken;
                Log.i(TAG, "code_sent");
                String phone_short=ccp.getFormattedFullNumber().substring(0,8);
                textViewPhone.setText(phone_short+"******");
                phone.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                otp.setVisibility(View.VISIBLE);
                phonrNum=false;
                if(count<1) {
                    startTimer();
                }
            }
        };
   close.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           if(phonrNum){
               finish();
           }else{
               go_button.setClickable(true);
               phone.setVisibility(View.VISIBLE);
               otp.setVisibility(View.GONE);
               countDownTimer.cancel();
               error.setVisibility(View.GONE);
               Log.i(TAG,"no phone");
               phonrNum=true;
           }
       }
   });
   int height=ccp.getHeight();
   Log.i(TAG,"cpp height "+height);
 //  mPhoneNumber.setHeight(height);

    }

    // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    public void send_otp(View view) {
        hideKeyBoard();
        Log.i("send_otp", "done");
        String phone_number = mPhoneNumber.getText().toString();
        NetworkCheck networkCheck=new NetworkCheck();
        error.setVisibility(View.INVISIBLE);
        complete_phone_number = ccp.getFullNumberWithPlus();
        Log.i(TAG,"complete phone ccp"+complete_phone_number);
        if (phone_number.isEmpty()) {
            error.setText("Enter Phone Number");
            error.setVisibility(View.VISIBLE);
             Log.i(TAG, "send_otp empty");

        } else {
            progressBar.setVisibility(View.VISIBLE);
           check_Institute();
        }

    }
    public void sentOTPPHONE(){


        NetworkCheck networkCheck=new NetworkCheck();
        if(networkCheck.internetConnectionAvailable(5000)) {
            Log.i(TAG, "verifying phone num");
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    complete_phone_number,
                    60,
                    TimeUnit.SECONDS,
                    login.this,
                    mCallbacks);

            hideKeyBoard();


            //phone.setVisibility(View.GONE);
            // otp.setVisibility(View.VISIBLE);
        }else {

            progressBar.setVisibility(View.GONE);
            error.setText("Poor Internet Connection");
            error.setVisibility(View.VISIBLE);
        }

    }


    public void check_Institute(){
       Schoolid= referral.getText().toString();
       if(Schoolid.isEmpty()){
           Log.i(TAG,"No School code");
           sentOTPPHONE();//go to same login
       }else{
           go_button.setClickable(false);
           Log.i(TAG,"refeera"+Schoolid);
           db.collection("institutes").document("school").collection(Schoolid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   if(task.isSuccessful()){
                       if(task.getResult().isEmpty()){
                           error.setText("Incorrect School ID");
                           error.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.INVISIBLE);
                           go_button.setClickable(true);
                       }else{
                           Log.i(TAG,"succesful"+Schoolid);
                          shareStoreSchool();
                          sentOTPPHONE();
                       }
                   }else {
                       error.setText("Error:Try again Later");
                       error.setVisibility(View.VISIBLE);
                       progressBar.setVisibility(View.INVISIBLE);
                       go_button.setClickable(true);
                   }
               }
           });
       }

    }
    public void shareStoreSchool(){
        schoolBool=true;
        share.setSchoolId(Schoolid);
        share.setAcademyId(getString(R.string.empty));
        share.setReferedby(getString(R.string.empty));
        share.apply();

    }
    public void resendOtp(){
        complete_phone_number = ccp.getFullNumberWithPlus();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                complete_phone_number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                resendToken);

    }
public void startTimer(){
        count++;
        Long l=Long.valueOf(60000);
        countDownTimer=new CountDownTimer(l,1000) {
            @Override
            public void onTick(long l) {
                Long time=l/1000;
                resendTimer.setText(time.toString());
            }

            @Override
            public void onFinish() {
                resendTimer.setVisibility(View.GONE);
                resendText.setText("Resent OTP");
            Log.i(TAG,"otp resent");
            resendOtp();
            }
        }.start();
}
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        Log.i("signInWithPhoneAuth", "done");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("signInWithPhoneAuth", "onComplete");
                            user_id = task.getResult().getUser().getUid();
                            check_phone_num(user_id);


                            // ...
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Log.i("signInWithPhoneAuth", "fail");
                                progressBar.setVisibility(View.GONE);
                                verifyNow.setClickable(true);
                                error.setText("Incorrect OTP");
                                error.setVisibility(View.VISIBLE);
                               // Toast.makeText(getApplicationContext(), "There was an error verifying OTP", Toast.LENGTH_LONG);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                verifyNow.setClickable(true);
                                error.setText("Error");
                                error.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
    }

    public void verify_now(View view) {
        String otp1_string = otp1.getText().toString();
        String otp2_string = otp2.getText().toString();
        String otp3_string = otp3.getText().toString();
        String otp4_string = otp4.getText().toString();
        String otp5_string = otp5.getText().toString();
        String otp6_string = otp6.getText().toString();
        NetworkCheck networkCheck = new NetworkCheck();

        TextView error = findViewById(R.id.error_verification);
        Log.i(TAG, "verify_now done");
        if (otp1_string.isEmpty() || otp2_string.isEmpty() || otp3_string.isEmpty() || otp4_string.isEmpty() || otp5_string.isEmpty() || otp6_string.isEmpty()) {
            error.setText("Incorrect OTP");
            error.setVisibility(View.VISIBLE);
            otp1.setText("");
            otp2.setText("");
            otp3.setText("");
            otp4.setText("");
            otp5.setText("");
            otp6.setText("");
            Log.i("verify_now", "empty");


        } else {
            progressBar.setVisibility(View.VISIBLE);
            hideKeyBoard();
            if (networkCheck.internetConnectionAvailable(5000)) {
                verifyNow.setClickable(false);
                error.setVisibility(View.GONE);
                Log.i("verify_now", "not_empty");
                Log.i("otp", (otp1_string + otp2_string + otp3_string + otp4_string + otp5_string + otp6_string));
                try {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, (otp1_string + otp2_string + otp3_string + otp4_string + otp5_string + otp6_string));
                    signInWithPhoneAuthCredential(credential);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                progressBar.setVisibility(View.GONE);
                error.setText("Poor Internet Connection");
                error.setVisibility(View.VISIBLE);


            }


        }
    }

    //------------------------------------------------------------------Check Phone Number-----------------------------------------------------------------//
    private void check_phone_num(final String id) {
        Log.i("check", "entered");


        db.collection("phone_num").whereEqualTo("phone_number", complete_phone_number).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Boolean login;

                    if (task.getResult().isEmpty()) {
                        create_new_user(id);
                    } else {
                        for (QueryDocumentSnapshot document : task.getResult()) {


                                login(document);
                                Log.i("user_login", "login");

                            Log.d("tag", document.getId() + " => " + document.getData());
                        }


                    }
                } else {
                    Log.d("tag", "Error getting documents: ", task.getException());
                }
            }
        });


    }


    //----------------------------------------------------------------------Create New user--------------------------------------------------------//
    private void create_new_user(String ID) {

        Intent intent = new Intent(login.this, account_subscipe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("kid_number", 1);


        //SharedPrference
        share.setUser_id(user_id);
        share.setKid1_id(user_id + "1");
        share.setKids_number(1);
        share.apply();
        FirebaseCrashlytics.getInstance().setUserId(share.getUser_id());

        account_details account_details = new account_details(this, share.getKid1_id());
        account_details.setPhone_number(complete_phone_number);
        account_details.apply();
        Log.i("account_phone",account_details.getPhone_number());
        Log.i("account","before phone");


        share.setProcess(3);

        share.apply();

        startActivity(intent);
    }

    public void login(DocumentSnapshot document) {
        Log.i("login_fn", "executed");
        String user_id = document.getString("user_id");

        share.setUser_id(user_id);
        share.setKid1_id(user_id + "1");
        share.setKid2_id(user_id + "2");





        share.setKids_number(document.getLong("kids_number").intValue());
        share.apply();
        FirebaseCrashlytics.getInstance().setUserId(share.getUser_id());
        Log.i("login_fn", Integer.toString(document.getLong("kids_number").intValue()));
        for (int i = 1; i <= share.getKids_number(); i++) {

            final int[] tapCount = {0,0};
            if(i==1){
                tapCount[0]=1;
                Log.i("one",Integer.toString(tapCount[0]));
            }
            else{
                tapCount[1]=1;
                Log.i("two",Integer.toString(tapCount[1]));
            }
            db.collection("users").document(user_id).collection(share.getKids_id(i)).document("task_"+getString(R.string.rubik_type)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            //------------------------------------program to add---------------------------------------------------
                            if(tapCount[0]==1&&tapCount[1]!=1){
                                tasks_rubik(document,1,getString(R.string.rubik_type));
                            }
                            else{
                                tasks_rubik(document,2,getString(R.string.rubik_type));
                            }

                            Log.d("tag", "DocumentSnapshot data: " + document.getData());


                        } else {
                            Log.d("tag", "No such document");
                        }

                    } else {
                        Log.i("exception", task.getException().toString());
                    }
                }
            });
            db.collection("users").document(user_id).collection(share.getKids_id(i)).document("task_"+getString(R.string.sport_type)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            //------------------------------------program to add---------------------------------------------------
                            if(tapCount[0]==1&&tapCount[1]!=1){
                                task(document,1,getString(R.string.sport_type));
                            }
                            else{
                                task(document,2,getString(R.string.sport_type));
                            }

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        } else {

                            Log.d(TAG, "No such document");
                        }

                    } else {
                        Log.i("exception", task.getException().toString());
                    }
                }
            });
            db.collection("users").document(user_id).collection(share.getKids_id(i)).document("task_"+getString(R.string.health_type)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            //------------------------------------program to add---------------------------------------------------
                            if(tapCount[0]==1&&tapCount[1]!=1){
                                task(document,1,getString(R.string.health_type));
                            }
                            else{
                                task(document,2,getString(R.string.health_type));
                            }

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        } else {
                            Log.d(TAG, "No such document");
                        }

                    } else {
                        Log.i("exception", task.getException().toString());
                    }
                }
            });


            db.collection("users").document(user_id).collection(share.getKids_id(i)).document("account_details").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            //------------------------------------program to add---------------------------------------------------

                            if(tapCount[0]==1&&tapCount[1]!=1){
                                account(document,1);
                            }
                            else{
                                account(document,2);
                            }
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        } else {
                            Log.d("tag", "No such document");
                        }

                    } else {
                        Log.i("exception", task.getException().toString());
                    }
                }
            });
            sql_rubik sqlRubik=new sql_rubik(this,share.getKids_id(i));
            sqlRubik.Download_all_tasks();

        }
        HashSet<String> set=new HashSet<>();
        for(int i=1;i<43;i++){
            set.add(Integer.toString(i));
        }
        set.add(getString(R.string.music));
        // sql.deleteVideo(set);
        Intent intent=new Intent(this,VideoService.class);
        intent.putExtra("mySet",set);
        startService(intent);



        share.setProcess(2);
        share.apply();
        Intent intent1= new Intent(getApplicationContext(),Parent_kid.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);


    }


    public void account(DocumentSnapshot documentSnapshot, int i) {

        Log.i(TAG,"account called");

     /*   if (!(trailTime(doj))) {
            course_id = "over";
            db.collection("users").document(user_id).collection(share.getKids_id(i)).document("account_details").update("course_id",course_id);

        }*/
        account_details account_details= new account_details(getApplicationContext(),share.getKids_id(i));
        account_details.setXcash(documentSnapshot.getLong("xcash").intValue());
        account_details.setXcore(documentSnapshot.getLong("xcore").intValue());
        account_details.setSchoolId(documentSnapshot.getString("schoolId"));
        if(schoolBool){
            account_details.setSchoolId(Schoolid);
            account_details.createdb();
            db.collection("users").document(user_id).collection(share.getKids_id(i)).document("account_details").update("schoolId",Schoolid).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i(TAG,"upload school sucessful"+Schoolid);
                        schoolBool=false;
                    }else {
                        Log.i(TAG,"upload school failure");
                    }
                }
            });

            HashMap<String,Object> map_school=new HashMap<>();
                map_school.put("kid_id",share.getKid1_id());
                map_school.put("DOT", Timestamp.now());
                db.collection("institutes").document("school").collection(Schoolid).add(map_school).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.i(TAG,"on complete");
                    }
                });

            db.collection("leaderboard").document(share.getKids_id(i)).update("school_code",Schoolid).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i(TAG,"upload leaderboard sucessful"+Schoolid);
                        schoolBool=false;
                    }else {
                        Log.i(TAG,"upload leaderboard failure");
                    }
                }
            });

        }

        account_details.setGrade(documentSnapshot.getString("grade"));
        account_details.setRefferedBy(documentSnapshot.getString("referredBy"));
        Calendar c=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yy");
        String format=simpleDateFormat.format(c.getTime());
        account_details.setDOJ(format);
        account_details.setAcademyId(documentSnapshot.getString("AcademyId"));
        account_details.setGender(documentSnapshot.getString("gender"));
        account_details.setEmail(documentSnapshot.getString("email"));
        account_details.setPhone_number(complete_phone_number);
        account_details.setKid_name(documentSnapshot.getString("kid_name"));
       //  account_details.setHeight((Integer)documentSnapshot.get("height"));
        account_details.setDOB(documentSnapshot.getString("DOB"));
        account_details.apply();
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG,"token"+token);
        MyFirebaseInstanceIdService myFirebaseInstanceIdService=new MyFirebaseInstanceIdService();
        myFirebaseInstanceIdService.sendTokenToServer(this,token);


    }

    public void tasks_rubik(DocumentSnapshot documentSnapshot,int i,String course_type){
        Log.i(TAG,"task rubik called"+course_type+share.getKids_id(i));
        String course_id=documentSnapshot.getString("course_id");
     String exp = documentSnapshot.getString("EXP");
     Log.i(TAG,"exp "+exp);

     Log.i(TAG,"hey hello from"+course_type);
      task_details task_details=new task_details(getApplicationContext(),share.getKids_id(i),course_type);
        task_details.setExp(exp);
        task_details.setCurrent_level(documentSnapshot.getLong("current_level").intValue());
        task_details.setCurrent_task(documentSnapshot.getLong("current_task").intValue());
        task_details.setCurrent_task_number(documentSnapshot.getLong("current_task_number").intValue());
        task_details.setMentor_id(documentSnapshot.getString("mentor_id"));
         task_details.setCourse_id(course_id);
        task_details.apply();

    }
    public void task(DocumentSnapshot documentSnapshot,int i,String course_type){
        String course_id=documentSnapshot.getString("course_id");
        Log.i(TAG,"task rubik called"+course_type+share.getKids_id(i));
        String exp = documentSnapshot.getString("EXP");

        task_details task_details=new task_details(getApplicationContext(),share.getKids_id(i),course_type);
        task_details.setExp(exp);
        task_details.setCurrent_level(documentSnapshot.getLong("current_level").intValue());
        task_details.setCurrent_task(documentSnapshot.getLong("current_task").intValue());
        task_details.setCurrent_task_number(documentSnapshot.getLong("current_task_number").intValue());
        task_details.setCourse_id(course_id);
        task_details.apply();
        if(course_type.equals(getString(R.string.sport_type))){
            sql_sports sql_sports=new sql_sports(this,share.getKids_id(i));
            sql_sports.download_next_task(task_details.getCurrent_level(),task_details.getCurrent_task());
        }
        if(course_type.equals(getString(R.string.health_type))){
            sql_health sql_health=new sql_health(this,share.getKids_id(i));
            sql_health.download_next_task(task_details.getCurrent_level(),task_details.getCurrent_task());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 1)
        {
            if(otp1.getText().toString().length()>1){
                otp1.setText(otp1.getText().toString().substring(0, 1));
            }
            if(otp2.getText().toString().length()>1){
                otp2.setText(otp2.getText().toString().substring(0, 1));
            }
            if(otp3.getText().toString().length()>1){
                otp3.setText(otp3.getText().toString().substring(0, 1));
            }
            if(otp4.getText().toString().length()>1){
                otp4.setText(otp4.getText().toString().substring(0, 1));
            }
            if(otp5.getText().toString().length()>1){
                otp5.setText(otp5.getText().toString().substring(0, 1));
            }
            if(otp6.getText().toString().length()>1){
                otp6.setText(otp6.getText().toString().substring(0, 1));
            }
        }

     if(editable.length()==1){
         if(otp1.length()==1){
             otp2.requestFocus();
         }
         if(otp2.length()==1){
             otp3.requestFocus();
         }
         if(otp3.length()==1){
             otp4.requestFocus();
         }
         if(otp4.length()==1){
             otp5.requestFocus();
         }
         if(otp5.length()==1){
             otp6.requestFocus();
         }
     }
     else if(editable.length()==0){
         if(otp6.length()==0){
             otp5.requestFocus();
         }
         if(otp5.length()==0){
             otp4.requestFocus();
         }
         if(otp4.length()==0){
             otp3.requestFocus();
         }
         if(otp3.length()==0){
             otp2.requestFocus();
         }
         if(otp2.length()==0){
             otp1.requestFocus();
         }
     }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"service destroyed");
        if(countDownTimer!=null){
        countDownTimer.cancel();}
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(phonrNum){
            finish();
            Log.i(TAG,"is phone");
        }else{
            go_button.setClickable(true);
            phone.setVisibility(View.VISIBLE);
            otp.setVisibility(View.GONE);
            countDownTimer.cancel();
            error.setVisibility(View.GONE);
            Log.i(TAG,"no phone");
            phonrNum=true;
        }

    }
    public void hideKeyBoard(){
        if(this.getCurrentFocus()!=null) {
            InputMethodManager inputManager = (InputMethodManager) login.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }
}