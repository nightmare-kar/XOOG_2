package com.karrit.xoog;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class task_details {
    private String PREFS_NAME="";
    private static final String Key_NAME = "com.our.package.userid";
    private String id,course_id,DOJ,user_id,exp,exp_string,mentor_id;
    private int current_level, current_task, current_credits;
    private boolean change_program, customized_program;
    FirebaseFirestore instance;
    private String empty="none";
    DocumentReference db;
    String TaskUnlock;
    private String TAG="task_details";
   private int total_level;
   private static final String Key_total_level="total_level_id";
    DocumentSnapshot document;
    private String one_one_Time,one_one_Date,one_one_link;
    private int current_task_number,current_bonus_credits;
    private final SimpleDateFormat formatter;
    private String course_type;
    private static final String Key_one_one_time= "one_one_time";
    private static final String Key_task_unlock= "task_unlock";
    private static final String Key_one_one_date = "one_one_date";
    private static final String Key_Mentor_Id = "mentor_id";
    private static final String Key_one_one_link = "one_one_link";
    private static final String Key_course_id = "course_id";
    private static final String Key_exp = "exp";
    private static final String Key_exp_String = "exp_string";
    private static final String Key_current_level = "current_level";
    private static final String Key_current_task = "current_task";
    private static final String Key_current_credits = "current_credits";
    private static final String Key_change_program = "change_program";
    private static final String Key_customized_program = "customized_program";
    SimpleDateFormat format;
    /**
     * This application's preferences
     */

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    /**
     * Constructor takes an android.content.Context argument
     */
    public task_details(Context ctx, String id, String course_type) {

        this.PREFS_NAME="com.package.user"+id;
        this.id=id;
        format = new SimpleDateFormat("dd MMMM yyyy");
        Log.i("task","cons"+this.id);
       this.course_type=course_type;
        formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss aa");
        settings = ctx.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        shared share = new shared(ctx);
        user_id=share.getUser_id();
        Log.i(TAG,"user id "+user_id);
        Log.i(TAG,"course_type"+course_type);
        /*
         * Get a SharedPreferences editor instance.
         * SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        editor = settings.edit();
    }
    public void clearShared(){
       Log.i(TAG,"share task delete"+course_type+ editor.clear().commit());
    }

    public int getTotal_level() {
        total_level=settings.getInt(getFieldKey(id,Key_total_level),0);
        return total_level;
    }

    public void setTotal_level(int total_level) {
        editor.putInt(getFieldKey(id,Key_total_level),total_level);
        this.total_level = total_level;
    }

    private String getFieldKey(String id, String fieldKey) {

        return  Key_NAME + id + "_" + this.course_type+"_"+fieldKey;
    }

    public String getMentor_id() {
        mentor_id=settings.getString(getFieldKey(id,Key_Mentor_Id),empty);
        return mentor_id;
    }

    public void setMentor_id(String mentor_id) {
        editor.putString(getFieldKey(id,Key_Mentor_Id),mentor_id);
        this.mentor_id = mentor_id;
    }

    public void setCourse_id(String course_id) {
        Log.i(TAG,"set course id called" + course_id);
        editor.putString(
                getFieldKey(this.id, Key_course_id),
                course_id);
        this.course_id = course_id;
    }

    public void setCurrent_task_number(int current_task_number) {
        editor.putInt(getFieldKey(this.id,"current_task_number"),current_task_number);
        this.current_task_number = current_task_number;
    }

    public void setCurrent_bonus_credits(int current_bonus_credits) {
        editor.putInt(getFieldKey(this.id,"current_bonus_credits"),current_bonus_credits);
        this.current_bonus_credits = current_bonus_credits;
    }

    public int getCurrent_task_number() {
        this.current_task_number=settings.getInt(getFieldKey(this.id,"current_task_number"),0);
        return current_task_number;
    }

    public int getCurrent_bonus_credits() {
        this.current_bonus_credits=settings.getInt(getFieldKey(this.id,"current_bonus_credits"),0);
        return current_bonus_credits;
    }

    public void setDOJ(String DOJ) {
        editor.putString(
                getFieldKey(this.id, "DOJ"),
                DOJ);
        this.DOJ = DOJ;
    }

    public void setCustomized_program(Boolean customized_program) {
        editor.putBoolean(
                getFieldKey(this.id, Key_customized_program),customized_program);
        this.customized_program=customized_program;
    }

    public void setChange_program(Boolean change_program) {
        editor.putBoolean(
                getFieldKey(this.id, Key_change_program),change_program);
        this.change_program=change_program;
    }

    public void setCurrent_level(int current_level) {
        editor.putInt(
                getFieldKey(this.id, Key_current_level),
                current_level);
        this.current_level=current_level;
    }

    public void setCurrent_task(int current_task) {
        editor.putInt(
                getFieldKey(this.id, Key_current_task),
                current_task);
        this.current_task = current_task;
    }

    public void setCurrent_credits(int current_credits) {
        editor.putInt(
                getFieldKey(this.id, Key_current_credits),
                current_credits);
        this.current_credits = current_credits;
    }
    public void setExp(int i) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, i); // Adding  days
        this.exp_string = format.format(c.getTime());
        this.exp = formatter.format(c.getTime());
        editor.putString(getFieldKey(this.id, Key_exp), exp);
        editor.putString(getFieldKey(this.id, Key_exp_String), exp_string);
        apply();
        createdb();

        db.update("EXP",this.exp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("date", "uploaded");
            }
        });
    }
    public void setExp(String exp){
        Log.i(TAG,"exp"+exp);
        try{
        Date d=formatter.parse(exp);
        exp_string=format.format(d);
        this.exp=exp;
        Log.i(TAG,"exp strinf"+exp_string);
        editor.putString(getFieldKey(this.id,Key_exp),exp);
            Log.i(TAG,"exp "+exp);
        editor.putString(getFieldKey(this.id,Key_exp_String),exp_string);
        apply();
            createdb();

            db.update("EXP",this.exp).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i("date", "uploaded");
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setSpecialExp(int i){
        try{
            String current_date=settings.getString(getFieldKey(this.id,Key_exp),"");
            Log.i(TAG,"current date "+current_date + i);
            if(current_date.equals("")){
                Log.i(TAG,"set exp");
                setExp(i);
            }else {
                Log.i(TAG,"set current exp");
                Date d = formatter.parse(current_date);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                Calendar now=Calendar.getInstance();
                now.setTime(new Date());
                if(c.getTimeInMillis()>now.getTimeInMillis())
                {
                    Log.i(TAG,"exp is greater");
                c.add(Calendar.DATE, i);
                SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                this.exp_string = format.format(c.getTime());
                this.exp = formatter.format(c.getTime());
                editor.putString(getFieldKey(this.id, Key_exp), exp);
                Log.i(TAG,"exp spec "+exp);
                editor.putString(getFieldKey(this.id, Key_exp_String), exp_string);
                apply();
                createdb();

                db.update("EXP",exp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "date uploaded");
                    }
                });}else {
                    Log.i(TAG,"now is greater");
                    setExp(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void newTask(String course_id,int i){
        setCourse_id(course_id);
        setCurrent_task(1);
        setCurrent_level(1);
        if(i!=0){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, i); // Adding i days
        this.exp_string = format.format(c.getTime());
        this.exp = formatter.format(c.getTime());
        editor.putString(getFieldKey(this.id, Key_exp), exp);
        editor.putString(getFieldKey(this.id, Key_exp_String), exp_string); }
        editor.apply();


    }

    public String getTaskUnlock() {
        TaskUnlock=settings.getString(getFieldKey(id,Key_task_unlock),empty);
        return TaskUnlock;
    }
    public boolean checkTaskUnlock(){
        boolean b=true;

        TaskUnlock=settings.getString(getFieldKey(id,Key_task_unlock),empty);
        try {
            Date d = formatter.parse(TaskUnlock);
            Calendar task = Calendar.getInstance();
            task.setTime(d);
            Calendar now=Calendar.getInstance();
            if(now.getTimeInMillis()>task.getTimeInMillis()){
               b=true;
            }else{
                b=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }

    public void setTaskUnlock(int i) {
        Calendar c=Calendar.getInstance();
        c.add(Calendar.DATE,i);
        Log.i(TAG,format.format(c.getTime()));
        Log.i(TAG,formatter.format(c.getTime()));
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        TaskUnlock=formatter.format(c.getTime());
        editor.putString(getFieldKey(id,Key_task_unlock),TaskUnlock);
        Log.i(TAG,formatter.format(c.getTime()));
    }


    public String getExp(){
        this.exp=settings.getString(getFieldKey(this.id,Key_exp),"");
        Log.i("task_details",this.exp);
        Log.i("task_details",settings.getString(getFieldKey(this.id,Key_exp_String),""));
        return this.exp;
    }


    public void apply(){
        editor.apply();
    }

    public String getEXP_String(){

       this.exp_string= settings.getString(getFieldKey(this.id,Key_exp_String),"");
       return exp_string;
    }

    public int getCurrent_credits() {
        current_credits=settings.getInt(getFieldKey(this.id,Key_current_credits),-1);
        return current_credits;
    }

    public int getCurrent_level() {
        current_level=settings.getInt(getFieldKey(this.id,Key_current_level),0);
        return current_level;
    }

    public int getCurrent_task() {
        current_task=settings.getInt(getFieldKey(this.id,Key_current_task),0);
        return current_task;
    }
    public boolean isChange_program(){
        change_program=settings.getBoolean(getFieldKey(this.id,Key_change_program),false);
        return change_program;
    }

    public boolean isCustomized_program() {
        customized_program=settings.getBoolean(getFieldKey(this.id,Key_customized_program),false);
        return customized_program;
    }

    public boolean checkEXP(){

        try {
            exp=settings.getString(getFieldKey(this.id,Key_exp),"");
            Log.i(TAG,"exp"+exp);
            Date ex=(Date)formatter.parse(exp);
            Calendar c=Calendar.getInstance();
            c.setTime(ex);
            Calendar cnow =Calendar.getInstance();
            cnow.setTime(new Date());
            if(c.getTimeInMillis()>cnow.getTimeInMillis()){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void setEXPString(String exp){

        editor.putString(getFieldKey(this.id,Key_exp_String),exp);
        this.exp=exp;
    }



    public String getCourse_id() {
      course_id = settings.getString(getFieldKey(this.id, "course_id"), "new");
      Log.i("course_id_inside_fun",course_id);
        return course_id;
    }

    public String getUserId() {
       return this.user_id;
    }


    public void createdb() {
        instance = FirebaseFirestore.getInstance();
        Log.i("task","user_id"+getUserId()+" "+this.id+"  "+"task_"+this.course_type);
        db = instance.collection("users").document(getUserId()).collection(this.id).document("task_" + this.course_type);
    }
    public void setChange_program_Cloud(String change_program){
        db.update(Key_course_id, change_program);
    }
    public void setCustomized_program_cloud(String customized_program_cloud){
        db.update(Key_customized_program,customized_program_cloud);
    }

    public void setCurrent_level_cloud(int current_level){
        db.update(Key_current_level,current_level);
    }
    public void setCurrent_task_cloud(int current_task){
        db.update(Key_current_task,current_task);
    }
    public void setCurrent_credits_cloud(int current_credits){
        db.update(Key_current_credits,current_credits);
    }
    public void setCourse_id_Cloud(final String course_id){

        db.update(Key_course_id,course_id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,course_id+"updated");
                }else {
                    Log.i(TAG,course_id+"failure");
                }
            }
        });
    }

    public DocumentSnapshot getDocument(){
        Log.i("task","document_entered");
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("document","No such document");
                    }
                } else {
                    Log.d("hello", "get failed with ", task.getException());
                }

            }
        });
        return document;
    }
    public String getKey_one_one(int level,int task,String field){
        return  Key_NAME + id + "_" + this.course_type+"_"+level+"_"+task+"_"+field;
    }

    public void setOne_one_Time(int level,int task,String one_one_Time) {
        editor.putString(getKey_one_one(level, task,Key_one_one_time),one_one_Time);
        this.one_one_Time = one_one_Time;
    }

    public void setOne_one_Date(int level,int task,String one_one_Date) {
        editor.putString(getKey_one_one(level, task,Key_one_one_date),one_one_Date);
        this.one_one_Date = one_one_Date;
    }

    public void setOne_one_link(int level,int task,String one_one_link) {
        editor.putString(getKey_one_one(level, task,Key_one_one_link),one_one_link);
        this.one_one_link = one_one_link;
    }

    public String getOne_one_Date(int level,int task) {

        one_one_Date=settings.getString(getKey_one_one(level,task,Key_one_one_date),empty);

        return one_one_Date;
    }

    public String getOne_one_link(int level,int task) {
        one_one_link=settings.getString(getKey_one_one(level,task,Key_one_one_link),empty);
        return one_one_link;
    }

    public String getOne_one_Time(int level,int task) {
        one_one_Time=settings.getString(getKey_one_one(level,task,Key_one_one_time),empty);
        return one_one_Time;
    }
}
