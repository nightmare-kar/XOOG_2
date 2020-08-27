package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class School_program extends AppCompatActivity  {
    shared share;
    EditText schoolCode_Edit;
   String api_key;
   Dialog dialogSubs;
    Dialog dialogUpload;
    Dialog dialogSports;
    boolean Mrec,Mrec_Payment;
    String refId;
    Boolean permissionToPay;
    private String TAG="school_program";
    TextView error;
    int Payment;
    int month;
    String course_type;
    account_details accountDetails;
    int flag_start;

    int rate_1_health,rate_3_health;
    FirebaseFirestore db;
    boolean updated;
    NetworkCheck networkCheck;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private AppPreference mAppPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_program);
        mAppPreference = new AppPreference();
        schoolCode_Edit=findViewById(R.id.schoolCode);
        db=FirebaseFirestore.getInstance();
        share=new shared(this);
        permissionToPay=false;
        final Intent intent=getIntent();
        month=0;
        updated=false;
        api_key="";
        Mrec=false;
        Mrec_Payment=false;
        networkCheck=new NetworkCheck();

        TextView explore=findViewById(R.id.explore);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(School_program.this,choose_plan.class);
                startActivity(intent);
            }
        });




        course_type=getString(R.string.health_type);
        rate_1_health=0;
        rate_3_health=0;
        dialogSubs=new Dialog(School_program.this);
        dialogUpload = new Dialog(School_program.this);
        dialogSports = new Dialog(School_program.this);


        //-----------------------checks the start of activity---------------------------------
        flag_start=intent.getIntExtra("flag_start",-1);
        error=findViewById(R.id.error);
        Log.i(TAG,"current_kid "+share.getCurrent_kid());
        accountDetails=new account_details(this,share.getCurrent_kid());
        String schoolId=accountDetails.getSchoolId();
        Log.i(TAG,"schoolId"+schoolId);
        if(schoolId.equals(getString(R.string.empty))){

        }else{
            error.setVisibility(View.GONE);
            schoolCode_Edit.setVisibility(View.GONE);
            permissionToPay=true;
        }
        ImageView close=findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag_start==1){
                    //from Game Fragment

                    finish();
                }else{
                    //from account details
                    Intent intent1=new Intent(School_program.this,Parent_kid.class);
                    startActivity(intent1);
                    finish();
                }
            }
        });
        TextView buyNow=findViewById(R.id.buyNow);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionToPay){

                  BuyNowClicked();
                }else {
                    error.setText("Loading.....");
                    error.setVisibility(View.VISIBLE);
                    String code = schoolCode_Edit.getText().toString();
                    if (code.isEmpty()) {
                        error.setText("Enter School code");
                        error.setVisibility(View.VISIBLE);
                    } else {
                        Checkschool(code);
                    }
                }
            }
        });

    }
    public void Checkschool(final String Schoolid){

        Log.i(TAG,"refeera"+Schoolid);

        db.collection("institutes").document("school").collection(Schoolid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        error.setText("Incorrect School ID");
                        error.setVisibility(View.VISIBLE);

                    }else{
                        Log.i(TAG,"succesful"+Schoolid);
                        accountDetails.setSchoolId(Schoolid);
                        accountDetails.apply();

                        //update in leaderboard
                        addSchool(Schoolid);
                      BuyNowClicked();

                    }
                }else {

                    error.setText("Error:Try again Later");
                    error.setVisibility(View.VISIBLE);

                }
            }
        });
    }
    public void addSchool(String schoolId){
        HashMap<String,Object> map_school=new HashMap<>();
        map_school.put("kid_id",share.getCurrent_kid());
        map_school.put("DOT", Timestamp.now());
        db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details").update("schoolId",schoolId,"referredBy",getString(R.string.empty)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.i(TAG, "task upload successful");
                }else {
                    Log.i(TAG,"task not success");
                }
            }
        });
        db.collection("institutes").document("school").collection(schoolId).add(map_school).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.i(TAG,"on complete");
            }
        });
        db.collection("leaderboard").document(share.getCurrent_kid()).update("school_code",schoolId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG,"leaderboard uploaded");
            }
        });
    }
    public void UploadTask(){
        task_details taskSports = new task_details(School_program.this, share.getCurrent_kid(), getString(R.string.health_type));
        String course_id = taskSports.getCourse_id();
        Log.i(TAG, "course id " + course_id);
        if (course_id.equals(getString(R.string.new_user))) {
            DocumentReference db = FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_" + getString(R.string.health_type

            ));
            HashMap<String, Object> task = new HashMap<String, Object>();
            task.put("current_level", 1);
            task.put("current_task", 1);
            task.put("DOJ", Timestamp.now());
            task.put("current_task_number", 2);
            task.put("course_id", getString(R.string.health_course_id));
            db.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG, "task_details uploaded");
                }
            });
            taskSports.newTask(getString(R.string.health_course_id), 0);
            taskSports.setCurrent_task_number(2);
            if(month==1) {
                taskSports.setExp(30);
            }
            if(month==3){
                taskSports.setExp(90);
            }
            taskSports.apply();
            sql_health sqlHealth=new sql_health(this,share.getCurrent_kid());
        }else{
            if(month==1) {
                Log.i(TAG,"month is 1");
                taskSports.setSpecialExp(30);
            }
            if(month==3){
                Log.i(TAG,"month is 3");
                taskSports.setSpecialExp(90);
            }
            taskSports.setCourse_id(getString(R.string.health_course_id));
            taskSports.createdb();
            taskSports.setCourse_id_Cloud(getString(R.string.health_course_id));
            taskSports.apply();

        }
        Intent intent=new Intent(School_program.this,Home_page.class);
        startActivity(intent);
    }

    public void showMonth(final String course_type){
        dialogSubs=new Dialog(School_program.this);

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

            month_1.setText(Integer.toString(rate_1_health));
            month_3.setText(Integer.toString(rate_3_health));

        LinearLayout linear_1=dialogSubs.findViewById(R.id.linear_1_months);
        LinearLayout linear_3=dialogSubs.findViewById(R.id.linear_3_months);
        linear_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    month=1;
                    startPayment(Integer.toString(rate_1_health*100));


            }
        });
        linear_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    month=3;
                    startPayment(Integer.toString(rate_3_health*100));

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
                    rate_1_health=task.getResult().getLong("rate_1_health").intValue();
                    rate_3_health=task.getResult().getLong("rate_3_health").intValue();
                    api_key=task.getResult().getString("razor_api_key");
                    updated=true;
                   showMonth(course_type);
                }else{
                    Log.i(TAG,"rates failed");
                }
            }
        });
    }
    public void startPayment(String payment) {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Result");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Payment");

        payUmoneyConfig.disableExitConfirmation(false);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {
            amount = Double.parseDouble(payment)/100;

        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        String phone = accountDetails.getPhone_number ();
        String productName = course_type;
        String firstName = accountDetails.getKid_name ();
        String email = accountDetails.getEmail ();
        String udf1 = accountDetails.getDOB ();
        String udf2 = accountDetails.getGender ();
        String udf3 = accountDetails.getGrade ();
        String udf4 = accountDetails.getUserId ();
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,School_program.this, AppPreference.selectedTheme,false);
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,School_program.this, R.style.AppTheme_default, false);
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get( PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    if(networkCheck.internetConnectionAvailable(5000)) {
                        if(dialogSubs.isShowing()) {
                            dialogSubs.dismiss();
                        }
                        showDialog_DontClose();

                        Log.i(TAG, "course type" + course_type);
                        updatePayment( );
                        Log.i(TAG, "payment sucess");


                    }else {
                        showDialog_start();
                        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        School_program.this.registerReceiver(School_program.this.mReceiver_Payment, filter);
                        Mrec_Payment=true;
                    }
                } else {

                    Log.i(TAG,"payment error");
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();


            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    public void updatePayment(){
        HashMap<String, Object> payMap=new HashMap<>();
        payMap.put("course_type",course_type);
        payMap.put("date", Timestamp.now());
        payMap.put("pay",Payment);
        Log.i(TAG,"update payment supported");
        task_details taskDetails=new task_details(this,share.getCurrent_kid(),course_type);
        Log.i(TAG,taskDetails.getCourse_id());
        Log.i(TAG,taskDetails.getEXP_String());


        DocumentReference doc= FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("payments");
        doc.collection("basic").add(payMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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
    public void BuyNowClicked(){
        if(networkCheck.internetConnectionAvailable(5000)){
            getRatesFromCloud();
        }else {
            showDialog_start();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            School_program.this.registerReceiver(School_program.this.mReceiver, filter);
            Mrec=true;
        }
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
                   School_program.this.unregisterReceiver(this);
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
                        updatePayment( );
                    }




                    School_program.this.unregisterReceiver(this);
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");


                }
            }
        }

    };
    public void showDialog_start(){

        dialogSports=new Dialog(School_program.this);

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

        dialogUpload=new Dialog(School_program.this);

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
            School_program.this.unregisterReceiver(mReceiver);
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
            School_program.this.unregisterReceiver(mReceiver_Payment);
        }
        super.onDestroy();
        }

        }

