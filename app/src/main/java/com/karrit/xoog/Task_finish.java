package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import java.util.HashMap;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class Task_finish extends AppCompatActivity {
String course_type;
task_details task_details;
TextView credits;
    Dialog dialogSports;
NetworkCheck networkCheck;
shared share;
int creditNum;
    sql_sports sqlSports;
    sql_health sqlHealth;
    sql_rubik sql_rubik;
    boolean upgrade;
private String TAG="Task_Finish";
int task,level,task_number;
Button bye;
boolean skip;
account_details accountDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_finish);
        Intent intent=getIntent();
        skip=intent.getBooleanExtra("skip",false);
        networkCheck=new NetworkCheck();
        share=new shared(this);
        upgrade=false;
        accountDetails=new account_details(this,share.getCurrent_kid());
        course_type=share.getCurrent_course_type();
        task_details=new task_details(this,share.getCurrent_kid(),course_type);

        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
        credits=findViewById(R.id.credit);
        bye=findViewById(R.id.bye);

        //////////////////go to next activity----------------------------------------------------
        bye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (course_type.equals(getString(R.string.rubik_type))) {
                    sql_rubik sql=new sql_rubik(Task_finish.this,share.getCurrent_kid());
                    if(sql.getTasktype(task_details.getCurrent_level(),task_details.getCurrent_task()).equals(getString(R.string.one_one_type))){
                        Intent intent=new Intent(Task_finish.this,slot_book.class);
                        one_one_class oneOneClass=sql.get_one_one(task_details.getCurrent_level(),task_details.getCurrent_task());
                        intent.putExtra("topic",oneOneClass.getDesciption());
                        intent.putExtra("time",oneOneClass.getTime());
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(Task_finish.this, animation_activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("upgrade", upgrade);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Intent intent = new Intent(Task_finish.this, animation_activity.class);
                    intent.putExtra("upgrade", upgrade);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        bye.setClickable(false);
        //------------------------------Setting Creduts------------------------------------------
        if(course_type.equals(getString(R.string.rubik_type))){
            sql_rubik=new sql_rubik(this,share.getCurrent_kid());

            Log.i(TAG,"sqlTypeRubik");
            Rubik_task_table_class task_table_class=sql_rubik.get_task_table(level,task);
            creditNum=task_table_class.getCredits();
            if(skip){
                Log.i(TAG,"skip enabled");
                creditNum=0;
            }else {
                task_details.setTaskUnlock(task_table_class.getRest());
                sql_rubik.updateDate(level,task);
                task_details.apply();
            }

            sql_rubik.updateCreditsEarned(level,task,creditNum);



        }else if(course_type.equals(getString(R.string.sport_type))){
            if(level==1&&task==2&&(task_details.getCourse_id().equals(getString(R.string.trial)))){
                DocumentReference db = FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_" + getString(R.string.sport_type));
                db.update("course_id",getString(R.string.over)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG,"onComplete");
                    }
                });
                task_details.setCourse_id(getString(R.string.over));
                task_details.apply();
            }

            Log.i(TAG,"sqlTypeSports");
            sqlSports=new sql_sports(this,share.getCurrent_kid());
            sport_details_class details_class=sqlSports.readDetails(level,task);

            sqlSports.updateDate(level,task);
            task_details.setTaskUnlock(details_class.getRest());
            task_details.apply();
            creditNum=details_class.getCredits();
            sqlSports.updateCreditsEarned(level,task,creditNum);

        }else if(course_type.equals(getString(R.string.health_type))){
            Log.i(TAG,"sqlHealth");
            sqlHealth=new sql_health(this,share.getCurrent_kid());
            sport_details_class details_class=sqlHealth.readDetails(level,task);
            sqlHealth.updateDate(level,task);
            task_details.setTaskUnlock(details_class.getRest());
            task_details.apply();
            creditNum=details_class.getCredits();
            sqlHealth.updateCreditsEarned(level,task,creditNum);

        }
        //---------------------------update level and task-----------------------------------------
        updatelevelAndTak();
        Log.i(TAG,"credit Number "+creditNum);
        credits.setText(Integer.toString(creditNum));
        TextView textView = findViewById(R.id.we);
        size(textView,6,12,"We are\nDone!",2f);
        //------------------------upload Transaction points-----------------------------------------------------------------

           if (networkCheck.internetConnectionAvailable(5000)) {
               TransactFn(creditNum);
           } else {
               Log.i(TAG, "no network");
               showDialog_start();
               IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
               Task_finish.this.registerReceiver(Task_finish.this.mReceiver, filter);
           }

    }
    public void TransactFn(final int credits){
        Log.i(TAG,"inside transaction");
        final DocumentReference documentReference= FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details");
       final DocumentReference leaderBoard=FirebaseFirestore.getInstance().collection("leaderboard").document(share.getCurrent_kid());
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Log.i(TAG,"apply transaction");
                DocumentSnapshot snapshot = transaction.get(documentReference);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                if(!course_type.equals(getString(R.string.health_type))){
                Long newXcash = snapshot.getLong("xcash") + credits;
                transaction.update(documentReference, "xcash", newXcash);
                accountDetails.setXcash(newXcash.intValue());
                accountDetails.apply();}
                Long newXcore = snapshot.getLong("xcore") + credits;
                transaction.update(documentReference, "xcore", newXcore);
                transaction.update(leaderBoard,"xcore",newXcore);
                accountDetails.setXcore(newXcore.intValue());
                accountDetails.apply();
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                HashMap<String,Object> user_refer=new HashMap<>();

                user_refer.put("kid_id",share.getCurrent_kid());
                user_refer.put("credits",credits);
                user_refer.put("error_type","transaction failure update credits");
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
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG,"transactions complete");
                bye.setClickable(true);
            }
        });
    }

    public void size(TextView textView, int start, int end, String s, float size){
        SpannableString span = new SpannableString(s);
        span.setSpan(new RelativeSizeSpan(size),start,end,SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(span);
    }

    public void updatelevelAndTak(){
        if(course_type.equals(getString(R.string.rubik_type))){
           task_number= sql_rubik.getTaskNumber(level);
           if(task>=task_number){
               Log.i(TAG,"task>task_number");
               if(level>=task_details.getTotal_level()){
                   task_details.setCourse_id(getString(R.string.rubik_done));
                   task_details.createdb();
                   task_details.setCourse_id_Cloud(getString(R.string.rubik_done));
                   task_details.apply();
               }else {
                   creditNum+=sql_rubik.getBonusCredits(level);
                   level++;
                   task=1;
                   task_details.setCurrent_level(level);
                   task_details.setCurrent_task(task);
                   upgrade=true;
                   task_details.apply();
               }

           }else {
               task++;
               task_details.setCurrent_task(task);
               task_details.apply();
           }
           task_details.createdb();
           task_details.setCurrent_level_cloud(task_details.getCurrent_level());
           task_details.setCurrent_task_cloud(task_details.getCurrent_task());

        }else {
            task_number=task_details.getCurrent_task_number();
            if (task == task_number) {
                creditNum+=task_details.getCurrent_bonus_credits();
                level++;
                task_details.setCurrent_level(level);
                task = 1;
                upgrade = true;
                task_details.setCurrent_task(task);
                task_details.apply();


            } else {
                task++;
                task_details.setCurrent_task(task);
                task_details.apply();
            }
            if (course_type.equals(getString(R.string.sport_type))) {
                sqlSports.download_next_task(level, task);
            } else if (course_type.equals(getString(R.string.health_type))) {
                sqlHealth.download_next_task(level, task);
            }
            task_details.createdb();
            task_details.setCurrent_level_cloud(task_details.getCurrent_level());
            task_details.setCurrent_task_cloud(task_details.getCurrent_task());
        }
        Log.i(TAG, "updated level " + level);
        Log.i(TAG, "updated level " + task);
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
                    TransactFn(creditNum);
                    dialogSports.dismiss();
                    Task_finish.this.unregisterReceiver(this);
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");
                }
            }
        }

    };
    public void showDialog_start(){

        dialogSports = new Dialog(Task_finish.this);
        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.no_internet_connection);
        Window window = dialogSports.getWindow();
        TextView close=dialogSports.findViewById(R.id.text);
        close.setText("Connect to Internet to Upgrade Scores");

        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSports.show();
    }

}
