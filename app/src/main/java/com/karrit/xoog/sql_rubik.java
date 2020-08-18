package com.karrit.xoog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class sql_rubik {
    private String course_id,kids_id;
    private task_details task_details;
    private String TAG="sql_rubik";
    private String table_upload_video="table_upload_video_rubik";
    private String table_upload_photo="table_upload_photo_rubik";
    private String table_one_one="table_one_one_rubik";
    private String table_show_content="table_show_content_rubik";
private String pic_num_id="picture_number";
private  String link_id="link";
    private  String time_id="Time";
private String credits_earned_id="credits_earned";
private String table_task_number="task_number_table";
private String date_id="date_id";
    private String table_show_video="table_show_video_rubik";
    private String table_task="table_task_rubik";
    private String task_type_id="task_type";

    private String level_id="level";
    private String task_id="task";
    private String credits_id="credits";
    private String rest_id="rest";
    private String des_id="description";
    private String show_scramble_id="show_scramble";
    private String bonus_credits_id="bonus_credits";
    private String task_number_id="task_number";

private Context context;

    public SQLiteDatabase sqLiteDatabase;

    public sql_rubik(Context ctx, String kids_id){
        this.course_id="rubik_type";
        sqLiteDatabase = ctx.openOrCreateDatabase(kids_id+"_"+course_id, MODE_PRIVATE, null);
this.context=ctx;
         task_details=new task_details(ctx,kids_id,course_id);

         this.kids_id=kids_id;
     }
     public void download_next_task(final int level){
         FirebaseFirestore db= FirebaseFirestore.getInstance();
         Log.i(TAG,"level"+level);
         db.collection("rubik").document("rubik").collection(Integer.toString(level)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()) {
                 store(task,level);
                     Log.i(TAG,"successful");
                     for (QueryDocumentSnapshot document : task.getResult()) {
                         Log.i(TAG, document.getId() + " => " + document.getData());
                     }
                 } else {
                     Log.i(TAG, "Error getting documents: ", task.getException());
                 }
             }
         });
     }
     public void dbClose(){
        sqLiteDatabase.close();
     }


     public void store(Task<QuerySnapshot> taskFirestore,int level){

        try{ Log.i(TAG,"begin_transaction");


         for (QueryDocumentSnapshot document : taskFirestore.getResult()) {
             Log.i(TAG,"inside_firestore");

             if (document.getId().equals("details")) {

             }else {
                 String course_type = document.getString("course_type");
                 int rest = document.getLong("rest").intValue();
                 Log.i("course_type&&rest", course_type + rest);
                 Log.i(TAG,"course type in"+course_type);
                 int task = Integer.parseInt(document.getId());
                 String empty="empty";
                 int credits=document.getLong("credits").intValue();

                 sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_task+"' ('"+level_id+"'INTEGER,'"+task_id+"'INTEGER,'"+task_type_id+"' VARCHAR,'"+rest_id+"' INTEGER,'"+credits_id+"' INTEGER,'"+credits_earned_id+"' INTEGER,'"+date_id+"' VARCHAR)");
                 sqLiteDatabase.execSQL("INSERT INTO '"+table_task+"' VALUES('"+level+"','"+task+"','"+course_type+"','"+rest+"','"+credits+"',0,'"+empty+"')");
                 if (course_type.equals("upload_photo")) {
                     upload_photo(document, level, task);
                     Log.i(TAG, "upload_photo");
                 } else if (course_type.equals("upload_video")) {
                     upload_video(document, level, task);
                     Log.i(TAG, "upload_video");
                 } else if (course_type.equals("one-one")) {
                     one_one(document, level, task);
                     Log.i(TAG, "one_one_class");
                 } else if (course_type.equals("show_video")) {
                     show_video(document, level, task);
                     Log.i(TAG, "show_video_class");
                 } else if (course_type.equals("show_content")) {
                     show_content(document, level, task);
                     Log.i(TAG, "show_content_class");
                 } else {
                     Log.i(TAG, "error in course_type");
                 }

             }
         }
         }catch (Exception e){
            e.printStackTrace();
        }

         read_task_table();
     }

     public void upload_photo(DocumentSnapshot document,int level,int task){
        String description=document.getString(des_id);
        int picture_number=document.getLong(pic_num_id).intValue();
         Boolean a=document.getBoolean(show_scramble_id);
         int show_scramble;
         if(a){
             show_scramble=1;
         }else {
             show_scramble=0;
         }
       sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_upload_photo+"' ('"+level_id+"' INTEGER,'"+task_id+"' INTEGER,'"+des_id+"' VARCHAR,'"+show_scramble_id+"' INTEGER,'"+pic_num_id+"' INTEGER)");
      //   sqLiteDatabase.execSQL("INSERT INTO table_upload_photo VALUES('"+level+"','"+task+"','"+description+"','"+show_scramble+"')");
         ContentValues con= new ContentValues();
         con.put(level_id,level);
         con.put(task_id,task);
         con.put(des_id,description);
         con.put(pic_num_id,picture_number);
         con.put(show_scramble_id,show_scramble);
         long red=sqLiteDatabase.insert(table_upload_photo,null,con);
         Log.i(TAG,"Result"+red);

     }
    public void upload_video(DocumentSnapshot document,int level,int task){
        String description=document.getString(des_id);
        Boolean a=document.getBoolean(show_scramble_id);
        int show_scramble;
        if(a){
            show_scramble=1;
        }else {
            show_scramble=0;
        }
        int picture_number=document.getLong(pic_num_id).intValue();
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_upload_video+"' ('"+level_id+"' INTEGER,'"+task_id+"' INTEGER,'"+des_id+"' VARCHAR,'"+show_scramble_id+"' INTEGER,'"+pic_num_id+"' INTEGER)");
        sqLiteDatabase.execSQL("INSERT INTO '"+table_upload_video+"' VALUES('"+level+"','"+task+"','"+description+"','"+show_scramble+"','"+picture_number+"')");

    }
    public void one_one(DocumentSnapshot document,int level,int task){
        String description=document.getString(des_id);
        int time=document.getLong(time_id).intValue();
        Log.i(description,Integer.toString(time));
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_one_one+"' ('"+level_id+"' INTEGER,'"+task_id+"' INTEGER,'"+des_id+"' VARCHAR,'"+time_id+"' INTEGER)");
        sqLiteDatabase.execSQL("INSERT INTO '"+table_one_one+"'VALUES('"+level+"','"+task+"','"+description+"','"+time+"')");

    }
    public void show_content(DocumentSnapshot document,int level,int task){
        String description=document.getString(des_id);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_show_content+"' ('"+level_id+"' INTEGER,'"+task_id+"' INTEGER,'"+des_id+"' VARCHAR)");
        sqLiteDatabase.execSQL("INSERT INTO '"+table_show_content+"' VALUES('"+level+"','"+task+"','"+description+"')");

    }
    public void show_video(DocumentSnapshot document,int level,int task){
        String description=document.getString("description");
        String link=document.getString("link");
        Log.i(TAG,"SHOW VIDEO INSIDE");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_show_video+"' ('"+level_id+"' INTEGER,'"+task_id+"' INTEGER,'"+des_id+"' VARCHAR,'"+link_id+"'VARCHAR)");
        sqLiteDatabase.execSQL("INSERT INTO '"+table_show_video+"' VALUES('"+level+"','"+task+"','"+description+"','"+link+"')");

    }

    public void read_task_table(){
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table_task+"'", null);
            int nameIndex = c.getColumnIndex(level_id);
            int ageIndex = c.getColumnIndex(task_id);
            int idIndex = c.getColumnIndex(task_type_id);
            int rest=c.getColumnIndex(rest_id);
            int credits=c.getColumnIndex(credits_id);
            int date=c.getColumnIndex(date_id);
            int earned=c.getColumnIndex(credits_earned_id);
            c.moveToFirst();

            do {
                Log.i(TAG,"task_type"+ c.getString(idIndex));
                Log.i(TAG, "task"+Integer.toString(c.getInt(ageIndex)));
                Log.i(TAG, "level"+Integer.toString(c.getInt(nameIndex)));
                Log.i(TAG,"rest"+ Integer.toString(c.getInt(rest)));
                Log.i(TAG,"credits" +Integer.toString(c.getInt(credits)));
                Log.i(TAG,"credits earned" +Integer.toString(c.getInt(earned)));
                Log.i(TAG,"date"+ c.getString(date));


            }while (c.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void read_upload_photo(){

        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table_upload_photo+"'", null);
            int levelIndex = c.getColumnIndex(level_id);
            int taskIndex = c.getColumnIndex(task_id);
            int desIndex = c.getColumnIndex(des_id);
            int picIndex=c.getColumnIndex(pic_num_id);
            int show_id=c.getColumnIndex(show_scramble_id);
            c.moveToFirst();

            do {
                Log.i(TAG+table_upload_photo, Integer.toString(c.getInt(levelIndex)));
                Log.i(TAG+table_upload_photo, Integer.toString(c.getInt(taskIndex)));
                Log.i(TAG+table_upload_photo,c.getString(desIndex));
                Log.i(TAG+table_upload_photo, Integer.toString(c.getInt(picIndex)));
                Log.i(TAG+table_upload_photo, Integer.toString(c.getInt(show_id)));


            }while (c.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }




    }
    public void read_upload_video(){
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table_upload_video+"'", null);
            int levelIndex = c.getColumnIndex(level_id);
            int taskIndex = c.getColumnIndex(task_id);
            int desIndex = c.getColumnIndex(des_id);
            int picIndex=c.getColumnIndex(pic_num_id);
            int show_id=c.getColumnIndex(show_scramble_id);
            c.moveToFirst();

            do {
                Log.i(TAG+table_upload_video,  Integer.toString(c.getInt(levelIndex)));
                Log.i(TAG+table_upload_video, Integer.toString(c.getInt(taskIndex)));
                Log.i(TAG+table_upload_video, c.getString(desIndex));
                Log.i(TAG+table_upload_video, Integer.toString(c.getInt(picIndex)));
                Log.i(TAG+table_upload_video, Integer.toString(c.getInt(show_id)));



            }while (c.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void read_show_content(){
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table_show_content+"'", null);
            int levelIndex = c.getColumnIndex(level_id);
            int taskIndex = c.getColumnIndex(task_id);
            int desIndex = c.getColumnIndex(des_id);

            c.moveToFirst();

            do {
                Log.i(TAG+table_show_content,  Integer.toString(c.getInt(levelIndex)));
                Log.i(TAG+table_show_content, Integer.toString(c.getInt(taskIndex)));
                Log.i(TAG+table_show_content, c.getString(desIndex));


            }while (c.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void read_show_video(){
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table_show_video+"'", null);
            int levelIndex = c.getColumnIndex(level_id);
            int taskIndex = c.getColumnIndex(task_id);
            int desIndex = c.getColumnIndex(des_id);
            int linkIndex=c.getColumnIndex(link_id);

            c.moveToFirst();

            do {
                Log.i(TAG+table_show_video,Integer.toString(c.getInt(levelIndex)));
                Log.i(TAG+table_show_video, Integer.toString(c.getInt(taskIndex)));
                Log.i(TAG+table_show_video, c.getString(desIndex));
                Log.i(TAG+table_show_video, c.getString(linkIndex));


            }while (c.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void Download_all_tasks(){
        FirebaseFirestore.getInstance().collection("rubik").document("total_level").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    StoreTaskDetails(documentSnapshot);
                    Log.i(TAG,"document"+documentSnapshot);
                }

            }
        });
    }
    public void StoreTaskDetails(DocumentSnapshot documentSnapshot){
        int total_level=documentSnapshot.getLong("total_level").intValue();
        task_details.setTotal_level(total_level);
        task_details.apply();
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_task_number+"' ('"+level_id+"' INTEGER,'"+task_number_id+"' INTEGER,'"+bonus_credits_id+"' INTEGER)");

        for(int i=1;i<=total_level;i++){
            ContentValues con= new ContentValues();
            con.put(level_id,i);
            con.put(task_number_id,documentSnapshot.getLong(Integer.toString(i)).intValue());
            con.put(bonus_credits_id,documentSnapshot.getLong(i+"_x"));
            long red=sqLiteDatabase.insert(table_task_number,null,con);
            Log.i(TAG,"Result "+i+" "+red);
            download_next_task(i);
        }

    }
    public int getTaskNumber(int level){
        int task_number=-1;
        try {
        String q="SELECT " + task_number_id + " FROM " + table_task_number +" WHERE level= '"+level+"'";
        Log.i(TAG,q);
        Cursor c=sqLiteDatabase.rawQuery(q,null);
        int idx = c.getColumnIndex(task_number_id);

        c.moveToFirst();

        Log.i(TAG,"column count"+c.getColumnCount()+c.getColumnName(0)+c.getCount());
        Log.i(TAG,"result from read "+ c.getString(idx));
// since we have a named column we can do
        task_number=c.getInt(idx);}catch (Exception e){
        e.printStackTrace();
      }
       return task_number;
    }
    public int getBonusCredits(int level){
        int bonus_credits=-1;
        try {
            String q="SELECT " + bonus_credits_id + " FROM " + table_task_number +" WHERE level= '"+level+"'";
            Log.i(TAG,q);
            Cursor c=sqLiteDatabase.rawQuery(q,null);
            int idx = c.getColumnIndex(bonus_credits_id);

            c.moveToFirst();

            Log.i(TAG,"column count"+c.getColumnCount()+c.getColumnName(0)+c.getCount());
            Log.i(TAG,"result from read "+ c.getString(idx));
// since we have a named column we can do
            bonus_credits=c.getInt(idx);}catch (Exception e){
            e.printStackTrace();
        }
        return bonus_credits;
    }
    public void readTableTaskNumber(){
        try {


            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '" + table_task_number + "'", null);
            int levelIndex = c.getColumnIndex(level_id);
            int bonusIndex = c.getColumnIndex(bonus_credits_id);
            int task_number_index = c.getColumnIndex(task_number_id);
            c.moveToFirst();

            do {
                Log.i(TAG + level_id, Integer.toString(c.getInt(levelIndex)));
                Log.i(TAG + bonus_credits_id, Integer.toString(c.getInt(bonusIndex)));
                Log.i(TAG + task_number_id, c.getString(task_number_index));



            } while (c.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void read_one_one(){
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table_one_one+"'", null);
            int levelIndex = c.getColumnIndex(level_id);
            int taskIndex = c.getColumnIndex(task_id);
            int desIndex = c.getColumnIndex(des_id);
            int timeIndex=c.getColumnIndex(time_id);

            c.moveToFirst();

            do {
                Log.i(TAG+table_one_one,Integer.toString(c.getInt(levelIndex)));
                Log.i(TAG+table_one_one, Integer.toString(c.getInt(taskIndex)));
                Log.i(TAG+table_one_one, c.getString(desIndex));
                Log.i(TAG+table_one_one, Integer.toString(c.getInt(timeIndex)));


            }while (c.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public String getTasktype(int level, int task){


String task_type="";
           try{
            String q="SELECT " + task_type_id + " FROM " + table_task +" WHERE level= '"+level+"' AND task= '"+task+"'";
            Log.i(TAG,q);
           Cursor c=sqLiteDatabase.rawQuery(q,null);
          // Cursor c = sqLiteDatabase.query(table_task, tableColumns, whereClause, whereArgs,
           //null, null,null);

     // since we have a named column we can do
        int idx = c.getColumnIndex(task_type_id);

        c.moveToFirst();

        Log.i(TAG,"column count"+c.getColumnCount()+c.getColumnName(0)+c.getCount());
        Log.i(TAG,"result from read "+ c.getString(idx));
// since we have a named column we can do
         task_type=c.getString(idx);}catch (Exception e){
    e.printStackTrace();
         }
       return task_type;
    }
    public Rubik_task_table_class get_task_table(int level, int task){
        String task_type="";
        int rest=-1;
        int credits=-1;

try{
        String q="SELECT * FROM " + table_task +" WHERE level= '"+level+"' AND task= '"+task+"'";
        Log.i(TAG,q);
        Cursor c=sqLiteDatabase.rawQuery(q,null);
        // Cursor c = sqLiteDatabase.query(table_task, tableColumns, whereClause, whereArgs,
        //null, null,null);

// since we have a named column we can do
        int idx = c.getColumnIndex(task_type_id);
        int restIndex=c.getColumnIndex(rest_id);
        int creditIndex=c.getColumnIndex(credits_id);

        c.moveToFirst();
        task_type=c.getString(idx);
        rest=c.getInt(restIndex);
        credits=c.getInt(creditIndex);




        Log.i(TAG,"column count"+c.getColumnCount()+c.getColumnName(0)+c.getCount());
        Log.i(TAG,"result from read "+ c.getString(idx));}catch (Exception e){
    e.printStackTrace();
        }
// since we have a named column we can do
        Rubik_task_table_class rubik_task=new Rubik_task_table_class(task_type,rest,credits);

        return rubik_task;
    }
    public void drop_table_task(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_task+ "'");
    }
    public void drop_table_upload_photo(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_upload_photo+ "'");
    }
    public void drop_table_upload_video(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_upload_video+ "'");
    }
    public void drop_table_one_one(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_one_one+ "'");
    }
    public void drop_table_show_content(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_show_content + "'");
    }
    public void drop_table_show_video(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_show_video+ "'");
    }
    public void drop_all_table(){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_task+ "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_upload_photo+ "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_upload_video+ "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_one_one+ "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_show_video+ "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + table_show_content + "'");
    }
    public upload_class get_upload_photo(int level,int task){
        String des="";
        int pic=0;
        boolean show=false;
        try{
            String q="SELECT * FROM " + table_upload_photo +" WHERE level= '"+level+"' AND task= '"+task+"'";
            Cursor c=sqLiteDatabase.rawQuery(q,null);
            int desIndex=c.getColumnIndex(des_id);
            int picID=c.getColumnIndex(pic_num_id);
            int show_id=c.getColumnIndex(show_scramble_id);
            c.moveToFirst();
            des=c.getString(desIndex);
            pic=c.getInt(picID);

            if(c.getInt(show_id)==1){
                show=true;
            }else {
                show=false;
            }}catch (Exception e){
            e.printStackTrace();
        }
        upload_class upload=new upload_class(des,pic,show);
        return upload;


    }
    public upload_class get_upload_video(int level,int task){
        String des="";
        int pic=0;
        boolean show=false;
        try{
        String q="SELECT * FROM " + table_upload_video +" WHERE level= '"+level+"' AND task= '"+task+"'";
        Cursor c=sqLiteDatabase.rawQuery(q,null);
        int desIndex=c.getColumnIndex(des_id);
        int picID=c.getColumnIndex(pic_num_id);
        int show_id=c.getColumnIndex(show_scramble_id);
        c.moveToFirst();
       des=c.getString(desIndex);
        pic=c.getInt(picID);

        if(c.getInt(show_id)==1){
            show=true;
        }else {
            show=false;
        }}catch (Exception e){
            e.printStackTrace();
        }
        upload_class upload=new upload_class(des,pic,show);
        return upload;


    }

    public show_content_class get_show_content(int level, int task){
        String des="";
        try {


            String q = "SELECT * FROM " + table_show_content + " WHERE level= '" + level + "' AND task= '" + task + "'";
            Cursor c = sqLiteDatabase.rawQuery(q, null);
            int desIndex = c.getColumnIndex(des_id);

            c.moveToFirst();
            des = c.getString(desIndex);
        }catch (Exception e){e.printStackTrace();}
        show_content_class show=new show_content_class(des);
        return show;


    }
    public show_video_class get_show_video(int level, int task){
        String des="";
        String link="";
        try{
        String q="SELECT * FROM " + table_show_video +" WHERE level= '"+level+"' AND task= '"+task+"'";
        Cursor c=sqLiteDatabase.rawQuery(q,null);
        int desIndex=c.getColumnIndex(des_id);
        int linkIndex=c.getColumnIndex(link_id);
        c.moveToFirst();
        des=c.getString(desIndex);
        link=c.getString(linkIndex);}catch (Exception e){
            e.printStackTrace();
        }

        show_video_class show=new show_video_class(des,link);
        return show;


    }
    public one_one_class get_one_one(int level, int task){
        String des="";
        int time=0;
        try{
        String q="SELECT * FROM " + table_one_one +" WHERE level= '"+level+"' AND task= '"+task+"'";
        Cursor c=sqLiteDatabase.rawQuery(q,null);
        int desIndex=c.getColumnIndex(des_id);

        int timeIndex=c.getColumnIndex(time_id);
        c.moveToFirst();
        des=c.getString(desIndex);

        time=c.getInt(timeIndex);}catch (Exception e){
            e.printStackTrace();
        }

        one_one_class show=new one_one_class(des,time);
        return show;


    }
    public void updateDate(int level,int task){
        ContentValues cv = new ContentValues();
        Calendar c=Calendar.getInstance();

        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

        String date_String=format.format(c.getTimeInMillis());
        Log.i(TAG,"date "+date_String);
        cv.put(date_id,date_String); //These Fields should be your String values of actual column names

        sqLiteDatabase.update(table_task, cv, task_id+"="+task+" AND "+level_id+"="+level, null);
    }
    public void updateCreditsEarned(int level,int task,int credits){
        ContentValues cv = new ContentValues();
        Calendar c=Calendar.getInstance();

        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

        String date_String=format.format(c.getTimeInMillis());
        Log.i(TAG,"date "+date_String);
        cv.put(credits_earned_id,credits); //These Fields should be your String values of actual column names

        sqLiteDatabase.update(table_task, cv, task_id+"="+task+" AND "+level_id+"="+level, null);
    }
    public void delete(){
        Log.i(TAG,"delete"+course_id);
        context.deleteDatabase(kids_id+"_"+course_id);
    }


    public level_task getTaskLevelFromDate(String date){
        level_task level_task=new level_task(0,0);
        try{
        String q="SELECT * FROM " + table_task +" WHERE "+date_id+"= '"+date+"'";
        Log.i(TAG,q);
        Cursor c=sqLiteDatabase.rawQuery(q,null);
        c.moveToFirst();
        int levelIndex=c.getColumnIndex(level_id);
        int taskTypeIndex=c.getColumnIndex(task_type_id);
        int task_index=c.getColumnIndex(task_id);
            int credits_earnedIn=c.getColumnIndex(credits_earned_id);
        Log.i(TAG,"level from sql"+c.getInt(levelIndex));
            Log.i(TAG,"task from sql"+c.getInt(task_index));
        level_task.setLevel(c.getInt(levelIndex));
        level_task.setTask(c.getInt(task_index));
            level_task.setCredits_earned(c.getInt(credits_earnedIn));
        level_task.setTask_type(c.getString(taskTypeIndex));}
        catch (Exception e){
            e.printStackTrace();
        }
        return level_task;
    }

}
