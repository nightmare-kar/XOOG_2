package com.karrit.xoog;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class shared {
    private String user_id,kid1_id,kid2_id,current_kid,Current_course_type;
    private int kids_number,process,courses;
    private String course_id_1,course_id_2;
    private static SharedPreferences settings;
    private static String name;
    private static SharedPreferences.Editor editor;
    private String referedby;
    private String token;
    private String TAG="shared";
    private String tokenId="token_id";
    private String referedBYID="referedy";
    private String academyId_TAG="sports_id";
    private String schoolId_TAG="school_id";
    private String academyId;
    private String schoolId;

    /**
     * Constructor takes an android.content.Context argument
     */
    public shared(Context ctx) {
        name = "general";

        settings = ctx.getSharedPreferences(name,
                Context.MODE_PRIVATE);

        /*
         * Get a SharedPreferences editor instance.
         * SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        editor = settings.edit();
    }
    public void clearShared(){
        Log.i(TAG,"share shared delete"+ editor.clear().commit());
    }
    public String getSchoolId() {
        schoolId=settings.getString(schoolId_TAG,"none");
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        editor.putString(schoolId_TAG,schoolId);
        this.schoolId = schoolId;
    }

    public String getAcademyId() {
        academyId=settings.getString(academyId_TAG,"none");
        return academyId;
    }

    public void setAcademyId(String academyId) {
        editor.putString(academyId_TAG,academyId);
        this.academyId = academyId;
    }

    public String getToken() {

        this.token=settings.getString(tokenId,"none");
        return this.token;
    }

    public void setToken(String token) {
        editor.putString(tokenId,token);
        this.token = token;
    }

    public String getReferedby() {
        this.referedby=settings.getString(referedBYID,"none");
        return this.referedby;
    }

    public void setReferedby(String referedby) {
        editor.putString(referedBYID,referedby);
        this.referedby = referedby;
    }

    public void setCurrent_course_type(String current_course_type){
        editor.putString("current_course_type",current_course_type);
        this.Current_course_type=current_course_type;
    }
    public String getCurrent_course_type(){
        this.Current_course_type=settings.getString("current_course_type","");
        return this.Current_course_type;
    }

    public void setUser_id(String user_id) {
        editor.putString("user_id",user_id);
        this.user_id = user_id;
    }

    public void setKid1_id(String kid1_id) {
        editor.putString("kid1_id",kid1_id);
        this.kid1_id = kid1_id;
    }

    public void setCurrent_kid(String current_kid) {
        editor.putString("current_kid",current_kid);
        this.current_kid = current_kid;
    }

    public String getCurrent_kid() {
       current_kid=settings.getString("current_kid","");
        return current_kid;
    }

    public void setCourses(int courses, int number) {
        editor.putInt("courses"+getKids_id(number),courses);
        this.courses = courses;
    }

    public void setCourse_id_1(String course_id_1, int number) {
        editor.putString("course_id_1"+getKids_id(number),course_id_1);
        this.course_id_1 = course_id_1;
    }

    public void setCourse_id_2(String course_id_2,int number) {
        editor.putString("course_id_2"+getKids_id(number),course_id_2);
        this.course_id_2 = course_id_2;
    }

    public void setKid2_id(String kid2_id) {
        editor.putString("kid2_id",kid2_id);
        this.kid2_id = kid2_id;
    }

    public void setKids_number(int kids_number) {
        editor.putInt("kids_number",kids_number);
        this.kids_number = kids_number;
    }

    public void setProcess(int process) {
        editor.putInt("process",process);
        this.process = process;
    }
    public void apply(){
        editor.apply();
    }


    public String getUser_id() {
        user_id=settings.getString("user_id","");
        return user_id;
    }

    public int getKids_number() {
        kids_number=settings.getInt("kids_number",0);
        return kids_number;
    }

    public String getKid1_id() {
        kid1_id=settings.getString("kid1_id","");
        return kid1_id;
    }

    public String getKid2_id() {
        kid2_id=settings.getString("kid2_id","");
        return kid2_id;
    }

    public int getCourses(int number) {
        courses=settings.getInt("courses"+getKids_id(number),0);
        return courses;
    }

    public String getCourse_id_1(int number) {
        course_id_1=settings.getString("course_id_1"+getKids_id(number),"");
        return course_id_1;
    }
    public String getCourse_id_2(int number) {
        course_id_2=settings.getString("course_id_2"+getKids_id(number),"");
        return course_id_2;
    }


    public int getProcess() {
        process=settings.getInt("process",0);
        return process;
    }
    public String getKids_id(int i){
        if(i==2){
            return settings.getString("kid2_id","");
        }
        else{
            return settings.getString("kid1_id","");
        }
    }
}
