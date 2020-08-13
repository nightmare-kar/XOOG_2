package com.karrit.xoog;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

public class sql_sports {
    private String course_id,kids_id;
    private task_details task_details;
    private String TAG="sql_sports";
    private String task_type_id="task_type";
    private String level_id="level";
    private String task_id="task";
    private String credits_id="credits";
    private String ex_order_id="ex_order";
    private String rest_id="rest";
   private String excercise_num_id="ex_num";
   private String ex_type_id="ex_type";
   private String relax_id="relax";
   private String Key_ex_id="ex_id";
   private String link_id="link";
   private String name_id="ex_name";
   private String time_id="time_id";
    private String task_number_id="task_number";
    private String table_excercise="table_excercise";
    private String credits_earned_id="credits_earned";
    private String date_id="date_id";
    private String table_task="table_task";
    public SQLiteDatabase sqLiteDatabase;
    Context ctx;

    public sql_sports(Context ctx, String kids_id){
        this.course_id="sport_type";
        this.ctx=ctx;
        sqLiteDatabase = ctx.openOrCreateDatabase(kids_id+"_"+course_id, MODE_PRIVATE, null);


        task_details=new task_details(ctx,kids_id,course_id);

        this.kids_id=kids_id;
    }
    public void download_next_task(final int level, final int Task){
        FirebaseFirestore db= FirebaseFirestore.getInstance();

        db.collection("sports").document(Integer.toString(level)).collection(Integer.toString(Task)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                   store(task,level,Task);
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



    public void store(Task<QuerySnapshot> taskFirestore,int level,int Task){

        try{ Log.i(TAG,"begin_transaction");


            for (QueryDocumentSnapshot document : taskFirestore.getResult()) {
                Log.i(TAG,"inside_firestore");

                if (document.getId().equals("details")) {
                    Log.i(TAG,document.getId());
                    Log.i(TAG,""+document.getData());
                    String ex_type=document.getString("ex_type");
                    Log.i(TAG,"ex_type"+ex_type);
                    Log.i(TAG,"rest value" + document.getLong("rest"));
                 int rest=document.getLong("rest").intValue();
                //  Log.i(TAG,"rest"+rest);
                    int credits=document.getLong("credits").intValue();
                    int ex_num=document.getLong("excercise_num").intValue();
                    Log.i(TAG,excercise_num_id+ex_num);
                    Log.i(TAG,""+credits+ex_num+ex_type+document.getLong("task_number"));
                   int task_number=document.getLong("task_number").intValue();
                   String empty="empty";
                    sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_task+"' ( '"+level_id+"'INTEGER,'"+task_id+"' INTEGER,'"+ex_type_id+"' VARCHAR,'"+rest_id+"' INTEGER,'"+credits_id+"' INTEGER,'"+excercise_num_id+"' INTEGER,'"+task_number_id+"' INTEGER,'"+credits_earned_id+"' INTEGER,'"+date_id+"' VARCHAR)");
                  sqLiteDatabase.execSQL("INSERT INTO table_task VALUES('"+level+"','"+Task+"','"+ex_type+"','"+rest+"','"+credits+"','"+ex_num+"','"+task_number+"',0,'"+empty+"')");
                    task_details.setCurrent_task_number(document.getLong("task_number").intValue());
                    task_details.apply();
                }else {
                    Log.i(TAG,document.getId());
                    int ex_id = document.getLong("ex_id").intValue();
                    String link=document.getString("link");
                    String name=document.getString("name");
                    int relax = document.getLong("relax").intValue();
                    int time = document.getLong("time").intValue();
                    Log.i(TAG,time_id+time);
                    sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table_excercise+"' ('"+level_id+"' INTEGER,'"+task_id+"' INTEGER,'"+Key_ex_id+"' INTEGER,'"+name_id+"' VARCHAR,'"+link_id+"' VARCHAR,'"+relax_id+"' INTEGER,'"+time_id+"' INTEGER,'"+ex_order_id+"' INTEGER)");
                    int ex_order=Integer.parseInt(document.getId());
                    sqLiteDatabase.execSQL("INSERT INTO table_excercise VALUES('"+level+"','"+Task+"','"+ex_id+"','"+name+"','"+link+"','"+relax+"','"+time+"','"+ex_order+"')");

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void read_task_table(){
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM table_task", null);
         //   Cursor d = sqLiteDatabase.rawQuery("SELECT * FROM table_excercise", null);
            int nameIndex = c.getColumnIndex(ex_type_id);
            int excerxiseIndex = c.getColumnIndex(excercise_num_id);
         int levelindex=c.getColumnIndex(level_id);
         int taski=c.getColumnIndex(task_id);
            int rest=c.getColumnIndex(rest_id);
            int earned=c.getColumnIndex(credits_earned_id);
            int dateId=c.getColumnIndex(date_id);
            int credits=c.getColumnIndex(credits_id);
            c.moveToFirst();

         /*   int ex_id=d.getColumnIndex("ex_id");
            int link=d.getColumnIndex("link");
            int name=d.getColumnIndex("name");
            int relax=d.getColumnIndex("relax");
            int time=d.getColumnIndex("time");
*/
            do {
                Log.i(TAG,"credits_earned"+c.getInt(earned));
                Log.i(TAG,"date"+c.getString(dateId));
                Log.i(TAG,"level"+c.getInt(levelindex));
                Log.i(TAG,"task"+c.getInt(taski));
                Log.i(TAG,"bonus"+task_details.getCurrent_bonus_credits());
                Log.i(TAG,"task_number"+task_details.getCurrent_task_number());
                Log.i(TAG, "ex_type"+c.getString(nameIndex));
                Log.i(TAG, "ex_num"+c.getInt(excerxiseIndex));

               Log.i(TAG, Integer.toString(c.getInt(rest)));
               Log.i(TAG, Integer.toString(c.getInt(credits)));


            }while (c.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }
        Cursor d = sqLiteDatabase.rawQuery("SELECT * FROM table_excercise", null);
        int ex_id=d.getColumnIndex(Key_ex_id);
        int link=d.getColumnIndex(link_id);
        int name=d.getColumnIndex(name_id);
        int relax=d.getColumnIndex(relax_id);
        int time=d.getColumnIndex(time_id);
        d.moveToFirst();
        do{
           Log.i(TAG,"position"+ d.getPosition());
            Log.i("ex_id","ex" + d.getInt(ex_id));
            Log.i(TAG,"link"+d.getString(link));
            Log.i(TAG,"name"+d.getString(name));
            Log.i(TAG,"relax"+d.getInt(relax));
            Log.i(TAG,"time"+d.getInt(time));
        }while (d.moveToNext());
    }

    public HashSet<String> readGroupBy(int level,int task){
        HashSet<String> setString=new HashSet<>();

        try {

String query="SELECT "+Key_ex_id+" FROM "+table_excercise+" WHERE "+task_id+"="+task+" AND "+level_id+"="+level+" GROUP BY "+Key_ex_id;
            Cursor c = sqLiteDatabase.rawQuery(query, null);
int index=c.getColumnIndex(Key_ex_id);
c.moveToFirst();
do{
    setString.add(Integer.toString(c.getInt(index)));
    Log.i(TAG,"ex_id"+c.getInt(index));
}while (c.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        return setString;
    }
    public HashSet<String> DifferenceDownload(int level_prev,int task_prev,int level_next,int task_next){
        HashSet<String> setPrev=readGroupBy(level_prev,task_prev);
        HashSet<String> setNext=readGroupBy(level_next,task_next);
        HashSet<String> deleteSet=setPrev;
        Log.i(TAG,"deleteSet "+deleteSet.toString());
        deleteSet.remove(setNext);
        deleteVideo(deleteSet);
        setNext.removeAll(setPrev);
        Log.i(TAG,setNext.toString());
        return setNext;

    }
    public void deleteVideo(HashSet<String> set){
        for(String i: set){
        ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();}
        String filename="ex_"+i+".txt";
        File localfile=new File(directory,filename);
        localfile.delete();
        Log.i(TAG,"file "+i+ " deleted");}
    }

    public sport_details_class readDetails(int level,int task){
        int task_number=0;
        int ex_num=0;
        int rest=0;
        String ex_type="";
        int credits_earned=0;
        int credits=0;
         try {
             String q = "SELECT * FROM " + table_task + " WHERE level= '" + level + "' AND task= '" + task + "'";
             Cursor c = sqLiteDatabase.rawQuery(q, null);
             int EXtypeIndex = c.getColumnIndex(ex_type_id);
             int ex_num_Index = c.getColumnIndex(excercise_num_id);

             int taskIndex=c.getColumnIndex(task_number_id);
             int restINdex=c.getColumnIndex(rest_id);
             int creditsindex=c.getColumnIndex(credits_id);
              int credits_earnedId=c.getColumnIndex(credits_earned_id);

             c.moveToFirst();
             task_number=c.getInt(taskIndex);
             ex_num=c.getInt(ex_num_Index);
             rest=c.getInt(restINdex);
             ex_type=c.getString(EXtypeIndex);
             credits=c.getInt(creditsindex);
             credits_earned=c.getInt(credits_earnedId);

         }catch (Exception e){
             e.printStackTrace();
         }
         sport_details_class details_class=new sport_details_class(credits,rest,ex_num,task_number,ex_type,credits_earned);
         return details_class;
    }
    public ArrayList<sport_exercise_class> readSports(int level,int task){
        ArrayList<sport_exercise_class> arrayList=new ArrayList<sport_exercise_class>();
        try {
            String q = "SELECT * FROM " + table_excercise + " WHERE level= " + level + " AND task= " + task + " ORDER BY "+ex_order_id+" ASC";
            Cursor d = sqLiteDatabase.rawQuery(q, null);
            int ex_id = d.getColumnIndex(Key_ex_id);
            int link = d.getColumnIndex(link_id);
            int name = d.getColumnIndex(name_id);
            int relax = d.getColumnIndex(relax_id);
            int time = d.getColumnIndex(time_id);
            int ex_orderIn=d.getColumnIndex(ex_order_id);
            d.moveToFirst();
            do {
                String ex_name=d.getString(name);
                int ex_id_class=d.getInt(ex_id);
                int relax_class=d.getInt(relax);
                int time_class=d.getInt(time);
                int ex_order=d.getInt(ex_orderIn);
                sport_exercise_class exercise_class=new sport_exercise_class(ex_id_class,relax_class,time_class,ex_name,ex_order);
                arrayList.add(exercise_class);

              Log.i(TAG,"ex order "+ex_order);
            } while (d.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        return arrayList;
    }
    public void delete(){
        Log.i(TAG,"sql sports deleted");
        ctx.deleteDatabase(kids_id+"_"+course_id);
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
    public level_task getTaskLevelFromDate(String date){
        level_task level_task=new level_task(0,0);
        try{
            String q="SELECT * FROM " + table_task +" WHERE "+date_id+"= '"+date+"'";
            Log.i(TAG,q);
            Cursor c=sqLiteDatabase.rawQuery(q,null);
            c.moveToFirst();
            int levelIndex=c.getColumnIndex(level_id);
            int taskTypeIndex=c.getColumnIndex(ex_type_id);
            int task_index=c.getColumnIndex(task_id);
            int credits_earnedIn=c.getColumnIndex(credits_earned_id);
            Log.i(TAG,"level from sql"+c.getInt(levelIndex));
            Log.i(TAG,"task from sql"+c.getInt(task_index));
            level_task.setLevel(c.getInt(levelIndex));
            level_task.setCredits_earned(c.getInt(credits_earnedIn));
            level_task.setTask(c.getInt(task_index));
            level_task.setTask_type(c.getString(taskTypeIndex));}
        catch (Exception e){
            e.printStackTrace();
        }
        return level_task;
    }

}


