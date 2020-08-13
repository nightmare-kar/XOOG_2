package com.karrit.xoog;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.LastOwnerException;

public class account_details {
    private String PREFS_NAME="";
    private final String Key_NAME = "com.our.package.userid";
    private String id,kid_name,DOB,DOJ,phone_number,email,gender,user_id,referral_link,schoolId,academyId,refferedBy,grade;
   private String profImageurl;
   private String TAG="account_details";
    private int process,xcash,xcore;
    FirebaseFirestore instance;
    DocumentReference db;
    private String empty="none";
    DocumentSnapshot document;

    String referral_link_id="refferal_link";
    String prof_id="image_url";
    String xcore_id="xcore_id";
    String xcash_id="xcash_id";
    String refferedBy_TAG="refferalBY_tag";
    String School_id_TAG="schooli_id_tag";
    String institute_id_TAG="institute_tag";
    String gradeTag="grade_tag";

    /**
     * This application's preferences
     */

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    /**
     * Constructor takes an android.content.Context argument
     */
    public account_details(Context ctx, String id) {

            this.PREFS_NAME="com.package.user"+id;
            this.id=id;
            settings = ctx.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
        shared share = new shared(ctx);
        user_id=share.getUser_id();
        Log.i(TAG,"account id "+id);
        /*
         * Get a SharedPreferences editor instance.
         * SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        editor = settings.edit();
    }
    public void clearShared(){
        Log.i(TAG,"share account delete"+ editor.clear().commit());
    }

    public String getProfImageurl() {
        profImageurl=settings.getString(getFieldKey(id,prof_id),empty);
        return profImageurl;
    }

    public void setProfImageurl(String profImageurl) {
        editor.putString(getFieldKey(id,prof_id),profImageurl);
        this.profImageurl = profImageurl;
    }

    private String getFieldKey(String id, String fieldKey) {

        return  Key_NAME + id + "_" + fieldKey;
    }
    public String getSchoolId() {
        schoolId=settings.getString(getFieldKey(id,School_id_TAG),empty);
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        Log.i(TAG,"set school id "+id);
        editor.putString(getFieldKey(id,School_id_TAG),schoolId);
        this.schoolId = schoolId;
    }

    public String getGrade() {
        grade=settings.getString(getFieldKey(id,gradeTag),empty);
        return grade;
    }

    public void setGrade(String grade) {
        Log.i(TAG,"set grade id "+id);
        editor.putString(getFieldKey(id,gradeTag),grade);
        this.grade = grade;
    }

    public int getXcash() {
        xcash=settings.getInt(getFieldKey(id,xcash_id),0);
        return xcash;
    }

    public int getXcore() {
        xcore=settings.getInt(getFieldKey(id,xcore_id),0);

        return xcore;
    }

    public void setXcash(int xcash) {
        Log.i(TAG,"set xcash id "+id);
        editor.putInt(getFieldKey(id,xcash_id),xcash);
        this.xcash = xcash;
    }

    public void setXcore(int xcore) {

        editor.putInt(getFieldKey(id,xcore_id),xcore);
        this.xcore = xcore;
    }

    public String getAcademyId() {
        academyId=settings.getString(getFieldKey(id,institute_id_TAG),empty);
        return academyId;
    }

    public void setAcademyId(String academyId) {
        editor.putString(getFieldKey(id,institute_id_TAG),academyId);
        this.academyId = academyId;
    }

    public void setRefferedBy(String refferedBy) {
        editor.putString(getFieldKey(id,refferedBy_TAG),refferedBy);
        this.refferedBy = refferedBy;
    }

    public String getRefferedBy() {
        refferedBy=settings.getString(getFieldKey(id,refferedBy_TAG),empty);
        return refferedBy;
    }

    public void setGender(String gender) {
        editor.putString(
                getFieldKey(this.id, "gender"),
                gender);
        this.gender = gender;
        Log.i(TAG,"set gender"+id+" "+gender);
    }

    public void setDOB(String DOB) {
        editor.putString(
                getFieldKey(this.id, "DOB"),
                DOB);
        this.DOB = DOB;
    }

    public void setDOJ(String DOJ) {
        editor.putString(
                getFieldKey(this.id, "DOJ"),
                DOJ);
        this.DOJ = DOJ;
        Log.i(TAG,"set DOJ"+id+" "+DOJ);
    }

    public void setEmail(String email) {

        editor.putString(
                getFieldKey(this.id, "email"),
                email);
        this.email = email;
        Log.i(TAG,"set email"+id+" "+email);
    }

    public void setReferral_link(String referral_link) {
        editor.putString(getFieldKey(this.id,referral_link_id),referral_link);
        this.referral_link = referral_link;
    }

    public String getReferral_link() {
        this.referral_link=settings.getString(getFieldKey(this.id,referral_link_id),empty);
        return this.referral_link;
    }



    public void setKid_name(String kid_name) {
        editor.putString(
                getFieldKey(this.id, "kid_name"),
                kid_name);
        this.kid_name = kid_name;
        Log.i(TAG,"set name"+id+" "+kid_name);
    }


    public void setPhone_number(String phone_number) {

        editor.putString(
                getFieldKey(this.id, "phone_number"),
                phone_number);
        Log.i("phone_num","setted");
        this.phone_number = phone_number;
    }

    public void setProcess(int process) {
        editor.putInt("process",process);
        this.process = process;
    }

    public void apply(){
        editor.apply();
    }

    public int getProcess() {
        process=settings.getInt("process",0);
        return process;
    }



    public String getGender() {
        gender = settings.getString(getFieldKey(this.id,"gender"),"");
        Log.i(TAG,"get gender"+id+" "+gender);
        return gender;
    }

    public String getDOB() {
        DOB = settings.getString(getFieldKey(this.id, "DOB"), "" );
        return DOB;
    }

    public String getDOJ() {
        Log.i(TAG,"get DOJ"+id);
        DOJ = settings.getString(getFieldKey(this.id, "DOJ"), "" );
        return DOJ;
    }

    public String getKid_name() {
        kid_name = settings.getString(getFieldKey(this.id, "kid_name"), "" );
        Log.i(TAG,"get kids name"+id+" "+kid_name);
        return kid_name;
    }

    public String getEmail() {

        email = settings.getString(getFieldKey(this.id, "email"), "" );
        Log.i(TAG,"get email"+id+" "+email);
        return email;
    }


    public String getUserId() {

        return user_id;
    }

    public String getPhone_number() {
        phone_number = settings.getString(getFieldKey(this.id, "phone_number"), "" );
        Log.i("phone_num",phone_number);
        return phone_number;
    }

    public void createdb(){
        instance=FirebaseFirestore.getInstance();
        db=instance.collection("users").document(getUserId()).collection(this.id).document("account_details");
    }
    public void setKid_name_Cloud(String kid_name){
        db.update("kid_name",kid_name);
    }
    public void setemail_Cloud(String email){
        db.update("email",email);
    }
    public void setWeight_Cloud(int weight){
        db.update("weight",weight);
    }
    public void setHeight_Cloud(int height){
        db.update("height",height);
    }
    public void setDOB_Cloud(Timestamp DOB){
        db.update("DOB",DOB);
    }
    public void setDOJ_Cloud(Timestamp DOJ){
        db.update("DOJ",DOJ);
    }
    public void setPhone_num_Cloud(String phone_num){
        db.update("phone_number",phone_num);
    }
    public void setCourse_id_Cloud(String course_id){
        db.update("course_id",course_id);
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failure","listener");
            }
        });
        Log.d("tag", "DocumentSnapshot data: " + document.getData());

        return document;
    }




}
