package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class choose_plan extends AppCompatActivity implements PaymentResultListener {

   private String TAG="choose_plan";
   Dialog dialogUpload;
   String refId;
    TextView[] titleList = new TextView[3];
    int rate_rubik,rate_1_sport,rate_3_sport,rate_1_special,rate_3_special;
    ImageView[] imageViews = new ImageView[3];

    LinearLayout[] linearLayouts = new LinearLayout[3];
    Group[] groups= new Group[3];
    account_details accountDetails;
    shared share;
    int kid_number;
    int Payment;
Dialog dialogSubs;
boolean Mrec,Mrec_Payment;
    int month;
    String course_type;
    Button sports,rubiks,special;
    NetworkCheck networkCheck;
    Dialog dialogSports;
    String api_key;
    boolean updated;
    boolean showRubik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);
        rate_rubik=0;
        rate_1_special=0;
        ImageView close=findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rate_3_special=0;

        rate_1_sport=0;
        rate_3_sport=0;
        month=0;
        updated=false;
        api_key="";
        Mrec=false;
        Mrec_Payment=false;
        dialogUpload = new Dialog(choose_plan.this);
        dialogSports = new Dialog(choose_plan.this);
        dialogSubs = new Dialog(choose_plan.this);
        networkCheck=new NetworkCheck();
        if(networkCheck.internetConnectionAvailable(5000)){
            getRatesFromCloud();
        }else {
            showDialog_start();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            choose_plan.this.registerReceiver(choose_plan.this.mReceiver, filter);
            Mrec=true;
        }


        TextView title = findViewById(R.id.title);
        Intent intent = getIntent();
        kid_number=intent.getIntExtra("kid_number",0);
        share=new shared(this);
       accountDetails=new account_details(this,share.getCurrent_kid());
       sports=findViewById(R.id.sports_button2);
       rubiks=findViewById(R.id.rubik_button2);
       special=findViewById(R.id.special_button2);

        titleList[0]=((TextView)findViewById(R.id.sportsText));
        titleList[1]=((TextView)findViewById(R.id.rubikText));
        titleList[2]=((TextView)findViewById(R.id.specialText));
        groups[0]= findViewById(R.id.group1);
        groups[1]= findViewById(R.id.group2);
        groups[2]= findViewById(R.id.group3);

        showRubik=true;
        task_details taskDetails=new task_details(this,share.getCurrent_kid(),getString(R.string.rubik_type));
        if(taskDetails.getCourse_id().equals(getString(R.string.rubik_course_id))||taskDetails.getCourse_id().equals(getString(R.string.rubik_done))){
            showRubik=false;
            groups[1].setVisibility(View.GONE);
            groups[2].setVisibility(View.GONE);

        }
        String y1 = "ChooseYour\nPlan";
        SpannableString span1 = new SpannableString(y1);
        span1.setSpan(new RelativeSizeSpan(2.7f), 11, y1.length(), SPAN_INCLUSIVE_INCLUSIVE);
        title.setText(span1);
        String y2 = "Sports\nFitness";
        SpannableString span2 = new SpannableString(y2);
        span2.setSpan(new RelativeSizeSpan(1.1f), 0, 6, SPAN_INCLUSIVE_INCLUSIVE);
        titleList[0].setText(span2);
        String y3 = "Rubiks\nPuzzles";
        SpannableString span3 = new SpannableString(y3);
        span3.setSpan(new RelativeSizeSpan(1.1f), 0, 6, SPAN_INCLUSIVE_INCLUSIVE);
        titleList[1].setText(span3);
        String y4 = "Xoog\nSpecial";
        SpannableString span4 = new SpannableString(y4);
        span4.setSpan(new RelativeSizeSpan(1.4f), 0, 5, SPAN_INCLUSIVE_INCLUSIVE);
        titleList[2].setText(span4);
        imageViews[0] = findViewById(R.id.sports);
        imageViews[1] = findViewById(R.id.rubik);
        imageViews[2] = findViewById(R.id.special);

        linearLayouts[0] = findViewById(R.id.linear_sports);
        linearLayouts[1] = findViewById(R.id.linear_rubik);
        linearLayouts[2] = findViewById(R.id.linear_special);






//double check my math, this should be right, though

        int newWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() * (0.76));
        int newHeigth = (int)(0.38*newWidth);

        imageViews[0].getLayoutParams().height = newHeigth;
        imageViews[0].getLayoutParams().width = newWidth;
        imageViews[1].getLayoutParams().height = newHeigth;
        imageViews[1].getLayoutParams().width = newWidth;
        imageViews[2].getLayoutParams().height = newHeigth;
        imageViews[2].getLayoutParams().width = newWidth;


//Use RelativeLayout.LayoutParams if your parent is a RelativeLayout
     /*   ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                newWidth, newHeigth);
        params.setMargins(0,40,0,0);
        sports.setLayoutParams(params);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(R.id.sports, ConstraintSet.TOP, R.id.textView, ConstraintSet.BOTTOM, 0);

        constraintSet.connect(R.id.sports, ConstraintSet.START, R.id.cons, ConstraintSet.START, 0);
        constraintSet.connect(R.id.sports, ConstraintSet.END, R.id.cons, ConstraintSet.END, 0);


        constraintSet.applyTo(constraintLayout);*/


    }
    public void imageClick(View view){
        String tag= (String)view.getTag();
        Log.i("fn",tag);
        if(tag.equals("1")){
            Log.i("if","clicked");
            groups[0].setVisibility(View.GONE);
            linearLayouts[0].setVisibility(View.VISIBLE);
            if(showRubik){
                groups[1].setVisibility(View.VISIBLE);
                groups[2].setVisibility(View.VISIBLE);
            }

            linearLayouts[1].setVisibility(View.GONE);
            linearLayouts[2].setVisibility(View.GONE);
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout layout;

            layout = (ConstraintLayout) findViewById(R.id.cons);
            set.clone(layout);

            set.clear(R.id.rubik, ConstraintSet.TOP);
            set.clear(R.id.special, ConstraintSet.TOP);
            set.connect(R.id.rubik,ConstraintSet.TOP,R.id.linear_sports,ConstraintSet.BOTTOM);
            set.connect(R.id.special,ConstraintSet.TOP,R.id.rubik,ConstraintSet.BOTTOM);
            float dpRatio = this.getResources().getDisplayMetrics().density;
            float dpValue = 40;
            int pixelForDp = (int)(dpValue * dpRatio);
            set.setMargin(R.id.rubik,ConstraintSet.TOP,pixelForDp);
            set.setMargin(R.id.special,ConstraintSet.TOP,pixelForDp);
            set.applyTo(layout);


        }
        else if(tag.equals("2")){
            Log.i("if","clicked");
            groups[1].setVisibility(View.GONE);
            linearLayouts[1].setVisibility(View.VISIBLE);
            groups[0].setVisibility(View.VISIBLE);
            groups[2].setVisibility(View.VISIBLE);
            linearLayouts[0].setVisibility(View.GONE);
            linearLayouts[2].setVisibility(View.GONE);
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout layout;

            layout = (ConstraintLayout) findViewById(R.id.cons);
            set.clone(layout);

            set.clear(R.id.linear_rubik, ConstraintSet.TOP);
            set.clear(R.id.special, ConstraintSet.TOP);
            set.connect(R.id.linear_rubik,ConstraintSet.TOP,R.id.sports,ConstraintSet.BOTTOM);
            set.connect(R.id.special,ConstraintSet.TOP,R.id.linear_rubik,ConstraintSet.BOTTOM);
            float dpRatio = this.getResources().getDisplayMetrics().density;
            float dpValue = 40;
            int pixelForDp = (int)(dpValue * dpRatio);
            set.setMargin(R.id.linear_rubik,ConstraintSet.TOP,pixelForDp);
            set.setMargin(R.id.special,ConstraintSet.TOP,pixelForDp);
            set.applyTo(layout);

        }
        else if(tag.equals("3")){
            Log.i("if","clicked");
            groups[2].setVisibility(View.GONE);
            linearLayouts[2].setVisibility(View.VISIBLE);
            groups[0].setVisibility(View.VISIBLE);
            groups[1].setVisibility(View.VISIBLE);
            linearLayouts[0].setVisibility(View.GONE);
            linearLayouts[1].setVisibility(View.GONE);
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout layout;

            layout = (ConstraintLayout) findViewById(R.id.cons);
            set.clone(layout);

            set.clear(R.id.rubik, ConstraintSet.TOP);
            set.clear(R.id.linear_special, ConstraintSet.TOP);
            set.connect(R.id.rubik,ConstraintSet.TOP,R.id.sports,ConstraintSet.BOTTOM);
            set.connect(R.id.linear_special,ConstraintSet.TOP,R.id.rubik,ConstraintSet.BOTTOM);
            float dpRatio = this.getResources().getDisplayMetrics().density;
            float dpValue = 40;
            int pixelForDp = (int)(dpValue * dpRatio);
            set.setMargin(R.id.rubik,ConstraintSet.TOP,pixelForDp);
            set.setMargin(R.id.linear_special,ConstraintSet.TOP,pixelForDp);
            set.applyTo(layout);

        }
    }


    public void pay_now(View view) {
        if(updated) {
            switch (view.getId()) {
                case R.id.sports_button2:
                    course_type = getString(R.string.sport_type);
                    showMonth(course_type);
                    break;
                case R.id.rubik_button2:

                    course_type = getString(R.string.rubik_type);
                    startPayment(Integer.toString(rate_rubik * 100));
                    break;
                case R.id.special_button2:
                    course_type = getString(R.string.special_type);
                    showMonth(course_type);
                    break;
                default:
                    course_type = "";
                    break;

            }
        }


        Intent i = new Intent(this,setting_up.class);
        i.putExtra("course_type",course_type);


       // startActivity(i);
    }
    public void getApiKey(){


    }
    public void startPayment(String payment) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */

        /**
         * Instantiate Checkout
         */
        Payment=Integer.parseInt(payment);
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setKeyID(getString(R.string.apiKey));

        try {
            JSONObject options = new JSONObject();
            options.put("name", "XOOG");
            options.put("description", "Subscription Charges");
            //You can omit the image option to fetch the image from dashboard

            options.put("currency", "INR");
            options.put("amount", payment);

            JSONObject preFill = new JSONObject();
            preFill.put("email", accountDetails.getEmail());
            preFill.put("contact", accountDetails.getPhone_number());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }



    @Override
    public void onPaymentSuccess(String s) {
        refId=s;
        if(networkCheck.internetConnectionAvailable(5000)) {
            dialogSubs.dismiss();
            showDialog_DontClose();

            Log.i(TAG, "course type" + course_type);

            Log.i(TAG, "payment sucess");
            if (!accountDetails.getRefferedBy().equals(getString(R.string.empty))) {
                checkReferral(s);
                Log.i(TAG, "referral");
            }else {
                updatePayment(s);
            }


        }else {
            showDialog_start();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            choose_plan.this.registerReceiver(choose_plan.this.mReceiver_Payment, filter);
            Mrec_Payment=true;
        }

    }
    public void UploadTask(){
        if(course_type.equals(getString(R.string.rubik_type))){

            Log.i(TAG,"curent kid"+share.getCurrent_kid());
            task_details task_rubik=new task_details(choose_plan.this,share.getCurrent_kid(),getString(R.string.rubik_type));
            Log.i(TAG,course_type);
            sql_rubik sql_rubik=new sql_rubik(this,share.getCurrent_kid());
            if(task_rubik.getCourse_id().equals(getString(R.string.new_user))){
                Log.i(TAG,"rubik new");
               createNewFnRubiks();
            }else {
                Log.i(TAG,"rubik over");
                task_rubik.setCourse_id(getString(R.string.rubik_course_id));
                task_rubik.apply();
                task_rubik.createdb();
                task_rubik.setCourse_id_Cloud(getString(R.string.rubik_course_id));
            }

        }else if(course_type.equals(getString(R.string.sport_type))){
            Log.i(TAG,"curent kid"+share.getCurrent_kid());

            task_details taskSports=new task_details(choose_plan.this,share.getCurrent_kid(),getString(R.string.sport_type));
            if(taskSports.getCourse_id().equals(getString(R.string.new_user))){
                Log.i(TAG,"sport new");
                createNewFnSports();
            }else {
                Log.i(TAG,"sport over");
                taskSports.setCourse_id(getString(R.string.sport_course_id));
                Log.i(TAG, course_type);
                taskSports.createdb();
                taskSports.setCourse_id_Cloud(getString(R.string.sport_course_id));
                if (month == 1) {
                    Log.i(TAG, "month is" + month);
                    taskSports.setSpecialExp(30);
                } else if (month == 3) {
                    Log.i(TAG, "month is" + month);
                    taskSports.setSpecialExp(90);
                }
                taskSports.apply();
            }
        }else if(course_type.equals(getString(R.string.special_type))){
            Log.i(TAG,"curent kid"+share.getCurrent_kid());
            task_details taskSports=new task_details(choose_plan.this,share.getCurrent_kid(),getString(R.string.sport_type));
           if(taskSports.getCourse_id().equals(getString(R.string.new_user))){
               Log.i(TAG,"sport new");
               createNewFnSports();
           }else {
               Log.i(TAG,"sport old");
               taskSports.setCourse_id(getString(R.string.sport_course_id));
               Log.i(TAG, course_type);
               taskSports.createdb();
               taskSports.setCourse_id_Cloud(getString(R.string.sport_course_id));
               if (month == 1) {
                   taskSports.setSpecialExp(30);
               } else if (month == 3) {
                   taskSports.setSpecialExp(90);
               }
               taskSports.apply();
           }
            Log.i(TAG,"curent kid"+share.getCurrent_kid());
            task_details task_rubik=new task_details(choose_plan.this,share.getCurrent_kid(),getString(R.string.rubik_type));
            Log.i(TAG,course_type);
            sql_rubik sql_rubik=new sql_rubik(this,share.getCurrent_kid());
            if(task_rubik.getCourse_id().equals(getString(R.string.new_user))){
                Log.i(TAG,"rubik new");
                createNewFnRubiks();

            }else {
                Log.i(TAG,"rubik over");
                task_rubik.setCourse_id(getString(R.string.rubik_course_id));
                task_rubik.apply();
                task_rubik.createdb();
                task_rubik.setCourse_id_Cloud(getString(R.string.rubik_course_id));
            }

        }
        Intent intent=new Intent(choose_plan.this,Home_page.class);
        startActivity(intent);
    }
    public void createNewFnRubiks(){
        task_details task_rubik=new task_details(choose_plan.this,share.getCurrent_kid(),getString(R.string.rubik_type));
        Log.i(TAG,course_type);
        sql_rubik sql_rubik=new sql_rubik(this,share.getCurrent_kid());

            Log.i(TAG,"entered into if");


            task_rubik.newTask(getString(R.string.rubik_course_id),0);
            task_rubik.apply();
            FirebaseFirestore instance = FirebaseFirestore.getInstance();
            DocumentReference db = instance.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_" + getString(R.string.rubik_type));
            HashMap<String,Object> task= new HashMap<String, Object>();
            task.put("EXP",task_rubik.getExp());
            task.put("current_level",1);
            task.put("current_task",1);
            task.put("DOJ", Timestamp.now());
            task.put("mentor_id",getString(R.string.empty));
            task.put("current_task_number",sql_rubik.getTaskNumber(1));
            task.put("course_id",getString(R.string.rubik_course_id));
            db.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG,"task_details uploaded");
                }
            });
            Log.i(TAG,task_rubik.getEXP_String());
            Log.i(TAG,task_rubik.getCourse_id());
            Log.i(TAG,"current level "+ task_rubik.getCurrent_level());
            Log.i(TAG,"current level "+ task_rubik.getCurrent_task());
            Log.i(TAG,"current level "+ task_rubik.getCurrent_credits());
            Log.i(TAG,"current level "+ task_rubik.getCurrent_task_number());

        task_rubik.setCourse_id(getString(R.string.rubik_course_id));
        task_rubik.apply();

    }
    public void createNewFnSports(){
        task_details taskSports=new task_details(choose_plan.this,share.getCurrent_kid(),getString(R.string.sport_type));
        DocumentReference db = FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_" + getString(R.string.sport_type));
        HashMap<String,Object> task= new HashMap<String, Object>();
        task.put("current_level",1);
        task.put("current_task",1);
        task.put("DOJ", Timestamp.now());
        task.put("current_task_number",2);
        task.put("course_id",getString(R.string.sport_course_id));
        db.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG,"task_details uploaded");
            }
        });
        taskSports.newTask(getString(R.string.sport_course_id),0);
        taskSports.setCurrent_task_number(2);
        taskSports.apply();
        taskSports.setCourse_id(getString(R.string.sport_course_id));
        Log.i(TAG,course_type);
        taskSports.createdb();
        taskSports.setCourse_id_Cloud(getString(R.string.sport_course_id));
        if(month==1){
            Log.i(TAG,"month is"+month);
            taskSports.setSpecialExp(30);
        }
        else if(month==3){
            Log.i(TAG,"month is"+month);
            taskSports.setSpecialExp(90);
        }
        taskSports.apply();

    }

    @Override
    public void onPaymentError(int i, String s){
        dialogSubs.dismiss();
        Log.i(TAG,"payment error");
    }

    public void showMonth(final String course_type){
        dialogSubs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSubs.setCancelable(true);
        dialogSubs.setContentView(R.layout.dialog_choose_package);
        Window window = dialogSubs.getWindow();
        ImageView close=dialogSubs.findViewById(R.id.close);
        Log.i("main","pop_up_subs");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSubs.dismiss();
            }
        });
        final TextView month_1=dialogSubs.findViewById(R.id.month_1);
        TextView month_3=dialogSubs.findViewById(R.id.month_3);
        if(course_type.equals(getString(R.string.sport_type))){
            month_1.setText(Integer.toString(rate_1_sport));
            month_3.setText(Integer.toString(rate_3_sport));
        }else if(course_type.equals(getString(R.string.special_type))){
            month_1.setText(Integer.toString(rate_1_special));
            month_3.setText(Integer.toString(rate_3_special));
        }
        LinearLayout linear_1=dialogSubs.findViewById(R.id.linear_1_months);
        LinearLayout linear_3=dialogSubs.findViewById(R.id.linear_3_months);
        linear_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(course_type.equals(getString(R.string.sport_type))){
                    month=1;
                    startPayment(Integer.toString(rate_1_sport*100));
                }
                if(course_type.equals(getString(R.string.special_type))){
                    month=1;
                    startPayment(Integer.toString(rate_1_special*100));
                }

            }
        });
        linear_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(course_type.equals(getString(R.string.sport_type))){
                    month=3;
                    startPayment(Integer.toString(rate_3_sport*100));
                }
                if(course_type.equals(getString(R.string.special_type))){
                    month=3;
                    startPayment(Integer.toString(rate_3_special*100));
                }

            }
        });


        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSubs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogSubs.show();
    }
    public void getRatesFromCloud(){
        FirebaseFirestore.getInstance().collection("data").document("rates").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"got rates");
                     rate_rubik=task.getResult().getLong("rate_rubik").intValue();
                    rate_1_special=task.getResult().getLong("rate_1_special").intValue();
                    rate_3_special=task.getResult().getLong("rate_3_special").intValue();
                    rate_1_sport=task.getResult().getLong("rate_1_sport").intValue();
                    rate_3_sport=task.getResult().getLong("rate_3_sport").intValue();
                    api_key=task.getResult().getString("razor_api_key");
                    updated=true;
                }else{
                    Log.i(TAG,"rates failed");
                }
            }
        });
    }
    public void grantRewards(){

        final String referId=accountDetails.getRefferedBy();
        if(referId.equals(getString(R.string.empty))){
            Log.i(TAG,"no String");
        }else{
            String ReferUserId = referId.substring(0,referId.length() - 1);
            final DocumentReference documentReference=FirebaseFirestore.getInstance().collection("users").document(ReferUserId).collection(referId).document("account_details");
            final FirebaseFirestore db=FirebaseFirestore.getInstance();
            final DocumentReference documentReferenceCurrent=FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details");
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(documentReference);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    Long newXcash = snapshot.getLong("xcash") + 2000;
                    transaction.update(documentReference, "xcash", newXcash);
                    Long newXcore = snapshot.getLong("xcore") + 2000;
                    transaction.update(documentReference, "xcore", newXcore);
                    transaction.update(documentReferenceCurrent,"referredBy",getString(R.string.empty));

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");
                    accountDetails.setRefferedBy(getString(R.string.empty));
                    accountDetails.apply();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                    HashMap<String,Object> user_refer=new HashMap<>();
                    user_refer.put("referedFrom",share.getCurrent_kid());
                    user_refer.put("referd_by",referId);
                    user_refer.put("error_type","transaction failure of referral reward");
                    db.collection("errors").add(user_refer).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.i(TAG,"on Complete of error upload");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG,"on failure error upload");
                        }
                    });
                }
            });
        }
    }
    public void updatePayment(String s){
        HashMap<String, Object> payMap=new HashMap<>();
        payMap.put("RazorString",s);
        payMap.put("course_type",course_type);
        payMap.put("date", Timestamp.now());
        payMap.put("pay",Payment);
        Log.i(TAG,"update payment supported");
        task_details taskDetails=new task_details(this,share.getCurrent_kid(),course_type);
        Log.i(TAG,taskDetails.getCourse_id());
        Log.i(TAG,taskDetails.getEXP_String());


        DocumentReference doc= FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("payments");
        doc.collection("premium").add(payMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    UploadTask();
                    Log.i(TAG,"task is succesful");
                }else {
                    Log.i(TAG,"payment update failure");
                }

            }
        });
    }
    public void checkReferral(final String s){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference doc= db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("payments");
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
                    updatePayment(s);
                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                    updatePayment(s);
                }
            }
        });
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkCheck networkCheck=new NetworkCheck();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){

                boolean connected = networkCheck.internetConnectionAvailable(5000);
                // Use extras to verify that connection has been re-established...
                if (connected) {
                    // Unregister until we lose network connectivity again.
                   dialogSports.dismiss();
                   getRatesFromCloud();
                   Mrec=false;
                    choose_plan.this.unregisterReceiver(this);
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");
                }
            }
        }

    };
    private final BroadcastReceiver mReceiver_Payment = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkCheck networkCheck=new NetworkCheck();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){

                boolean connected = networkCheck.internetConnectionAvailable(5000);
                // Use extras to verify that connection has been re-established...
                if (connected) {
                    Mrec_Payment=false;
                    // Unregister until we lose network connectivity again.

                    dialogSports.dismiss();
                    showDialog_DontClose();
                    showDialog_DontClose();
                    Log.i(TAG, "course type" + course_type);
                    if(refId!=null) {
                        if (!accountDetails.getRefferedBy().equals(getString(R.string.empty))) {
                            checkReferral(refId);
                            Log.i(TAG, "referral");
                        }else {
                            updatePayment(refId);
                        }
                    }


                    Log.i(TAG, "payment sucess");


                    choose_plan.this.unregisterReceiver(this);
                    // Resume handling requests.

                }
            }
        }

    };
    public void showDialog_start(){


        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.no_internet_connection);
        Window window = dialogSports.getWindow();
        TextView close=dialogSports.findViewById(R.id.text);
        close.setText("Poor Internet Connection");

        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSports.show();
    }
    public void showDialog_DontClose(){


        dialogUpload.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpload.setCancelable(false);
        dialogUpload.setContentView(R.layout.uploading_dialog);
        Window window = dialogUpload.getWindow();
        TextView close=dialogUpload.findViewById(R.id.upload);
        close.setText("Setting up");
        close.setTextColor(getResources().getColor(R.color.blue));
        TextView dont=dialogUpload.findViewById(R.id.closeText);
        dont.setTextColor(getResources().getColor(R.color.blue));
        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogUpload.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogUpload.show();
    }

    @Override
    protected void onPause() {
        if(Mrec){
            choose_plan.this.unregisterReceiver(mReceiver);
        }
        if(dialogSports.isShowing()) {
            dialogSports.dismiss();
        }
        if(dialogSubs.isShowing()) {
            dialogSubs.dismiss();
        }
        if(dialogUpload.isShowing()) {
            dialogUpload.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(Mrec_Payment){
            choose_plan.this.unregisterReceiver(mReceiver_Payment);
        }
        super.onDestroy();
    }
}
