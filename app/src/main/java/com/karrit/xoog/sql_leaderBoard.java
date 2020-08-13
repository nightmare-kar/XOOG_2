package com.karrit.xoog;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class sql_leaderBoard {
    private String TAG="sql_leaderboard";
    private String dbName;
    private String positionTag="position";
    private String table_name_Overall="overall_leaderBoard";
    private String table_name_School;
    private String table_name_Class;
    private String Key_NAME="leaderBoard";

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String id;
    SQLiteDatabase sqLiteDatabase;
    private Context context;
    private String PREFS_NAME="com.karrit.xoog.leaderboard";
    private String lastUpdate;
    private int positionOverall,positionSchool, positionClass;
    private String positionOverall_TAG="positionOverall";
    private String positionSchool_TAG="positionSchool";
    private String rankId="rank";
    private String nameId="name";

    private String xcoreId="xcore";
    private String positionClass_TAG="positionClass";
    private String lastUpdate_TAG="lastUpdate";
    private String empty="none";
    private int empty_int=-1;
    account_details accountDetails;
    String schoolcode;
    String grade;
    SimpleDateFormat formatter;
    CollectionReference collectionReference;



    public sql_leaderBoard(Context ctx,String id){
        this.id=id;
        this.context=ctx;
        table_name_Class="class_"+id;
        table_name_School="school_"+id;
        formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss aa");
        settings = ctx.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor=settings.edit();
        accountDetails=new account_details(ctx,id);
        schoolcode=accountDetails.getSchoolId();
       // grade=accountDetails.getGrade();
        grade=Integer.toString(4);
        sqLiteDatabase = ctx.openOrCreateDatabase("leaderBoard", MODE_PRIVATE, null);
        collectionReference=FirebaseFirestore.getInstance().collection("leaderboard");

    }
    public void delete(){
        Log.i(TAG,"delete"+"leaderboard");
        Log.i(TAG,"leaderboard shared "+editor.clear().commit());
        context.deleteDatabase("leaderBoard");
    }

    public void showPosition(TextView textView,String name){
        Log.i(TAG,name);
        Log.i(TAG,"check"+checkLastUpdate(positionTag));
        if(checkLastUpdate(positionTag)){
            StorePositionFromCloud(textView,name);
        }else {
            Log.i(TAG,"check true");
            if(name.equals(positionClass_TAG)){
                Log.i(TAG,positionClass_TAG);
                if(!(getPositionClass()==-1)) {
                    textView.setText(Integer.toString(getPositionClass()));
                }else {
                    textView.setText("");
                }
            }
            if(name.equals(positionSchool_TAG)){

                Log.i(TAG,positionSchool_TAG);
                if(!(getPositionSchool()==-1)) {
                    textView.setText(Integer.toString(getPositionSchool()));
                }else {
                    textView.setText("");
                }
            }if(name.equals(positionOverall_TAG)){
                Log.i(TAG,positionOverall_TAG);
                if(!(getPositionOverall()==-1)) {
                    textView.setText(Integer.toString(getPositionOverall()));
                }else {
                    textView.setText("");
                }
            }


        }
    }
    public void downloadOverall(final LinearLayout linearLayout, final LayoutInflater inflater){

       collectionReference.orderBy("rankOverall").limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  if(!task.getResult().isEmpty()) {
                      Log.i(TAG, "successful");
                      Store(task,table_name_Overall,linearLayout,inflater);
                }
                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void downloadSchool(final LinearLayout linearLayout, final LayoutInflater inflater){

        collectionReference.whereEqualTo("school_code",schoolcode).orderBy("rankSchool").limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.i(TAG, "successful");


                        Store(task,table_name_School,linearLayout,inflater);

                    }
                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public ArrayList<leaderBoard_class> readOverall(){
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM "+table_name_Overall, null);
        //   Cursor d = sqLiteDatabase.rawQuery("SELECT * FROM table_excercise", null);
        int nameIndex = c.getColumnIndex(nameId);
        int RankIndex = c.getColumnIndex(rankId);
        int XcoreIndec=c.getColumnIndex(xcoreId);
        ArrayList<leaderBoard_class> leaderBoardClasses=new ArrayList<>();

        c.moveToFirst();

         /*   int ex_id=d.getColumnIndex("ex_id");
            int link=d.getColumnIndex("link");
            int name=d.getColumnIndex("name");
            int relax=d.getColumnIndex("relax");
            int time=d.getColumnIndex("time");
*/
        do {
          Log.i(TAG,"overall");
            leaderBoard_class board_class=new leaderBoard_class(c.getString(nameIndex),Integer.toString(c.getInt(RankIndex)),Integer.toString(c.getInt(XcoreIndec)));

            Log.i(TAG,"rank"+c.getString(RankIndex));

            Log.i(TAG,"xcore "+c.getInt(XcoreIndec));

            Log.i(TAG, "name"+c.getString(nameIndex));


            leaderBoardClasses.add(board_class);

        }while (c.moveToNext());
        return leaderBoardClasses;
    }
    public ArrayList<leaderBoard_class> readSchool(){
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM "+table_name_School, null);
        //   Cursor d = sqLiteDatabase.rawQuery("SELECT * FROM table_excercise", null);
        int nameIndex = c.getColumnIndex(nameId);
        int RankIndex = c.getColumnIndex(rankId);

        int XcoreIndec=c.getColumnIndex(xcoreId);
        ArrayList<leaderBoard_class> leaderBoardClasses=new ArrayList<>();
        c.moveToFirst();

         /*   int ex_id=d.getColumnIndex("ex_id");
            int link=d.getColumnIndex("link");
            int name=d.getColumnIndex("name");
            int relax=d.getColumnIndex("relax");
            int time=d.getColumnIndex("time");
*/
        do {
            leaderBoard_class board_class=new leaderBoard_class(c.getString(nameIndex),Integer.toString(c.getInt(RankIndex)),Integer.toString(c.getInt(XcoreIndec)));

            Log.i(TAG,"School");
            Log.i(TAG,"rank"+c.getString(RankIndex));

            Log.i(TAG,"xcore "+c.getInt(XcoreIndec));

            Log.i(TAG, "name"+c.getString(nameIndex));
            leaderBoardClasses.add(board_class);




        }while (c.moveToNext());
        return leaderBoardClasses;
    }
    public ArrayList<leaderBoard_class> readClass(){
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM "+table_name_Class, null);
        //   Cursor d = sqLiteDatabase.rawQuery("SELECT * FROM table_excercise", null);
        int nameIndex = c.getColumnIndex(nameId);
        int RankIndex = c.getColumnIndex(rankId);

        int XcoreIndec=c.getColumnIndex(xcoreId);
        ArrayList<leaderBoard_class> leaderBoardClasses=new ArrayList<>();
        c.moveToFirst();

         /*   int ex_id=d.getColumnIndex("ex_id");
            int link=d.getColumnIndex("link");
            int name=d.getColumnIndex("name");
            int relax=d.getColumnIndex("relax");
            int time=d.getColumnIndex("time");
*/
        do {
            leaderBoard_class board_class=new leaderBoard_class(c.getString(nameIndex),Integer.toString(c.getInt(RankIndex)),Integer.toString(c.getInt(XcoreIndec)));
            Log.i(TAG,"class");
            Log.i(TAG,"rank"+c.getString(RankIndex));

            Log.i(TAG,"xcore "+c.getInt(XcoreIndec));

            Log.i(TAG, "name"+c.getString(nameIndex));
            leaderBoardClasses.add(board_class);



        }while (c.moveToNext());
      return leaderBoardClasses;
    }
    public void showLinear(String table_name,LinearLayout linearLayout,LayoutInflater inflater){
        ArrayList<leaderBoard_class> leaderBoardClasses=new ArrayList<>();
        if(table_name.equals(table_name_Overall)){
         leaderBoardClasses=readOverall();
        }else if(table_name.equals(table_name_School)){
            leaderBoardClasses=readSchool();
        }else if(table_name.equals(table_name_Class)){
            leaderBoardClasses=readClass();
        }
        linearLayout.removeAllViews();
        LinearLayout view =(LinearLayout)inflater.inflate(R.layout.header_list_leaderboard, null);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams1.setMargins(30, 0, 30, 0);
        view.setLayoutParams(layoutParams1);
        linearLayout.addView(view);
        for(int i=0;i<leaderBoardClasses.size();i++){
            LinearLayout vi =(LinearLayout)inflater.inflate(R.layout.activity_list_leaderboard, null);
            TextView rank=vi.findViewById(R.id.rank);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(30, 20, 30, 0);
            rank.setText(leaderBoardClasses.get(i).getRank());
            TextView name=vi.findViewById(R.id.name);
            name.setText(leaderBoardClasses.get(i).getName());
            vi.setLayoutParams(layoutParams);
            TextView xcore=vi.findViewById(R.id.credit);
            xcore.setText(leaderBoardClasses.get(i).getCredits());
            linearLayout.addView(vi);

        }
    }
    public void clearTAble(){
        sqLiteDatabase.execSQL("DELETE FROM "+table_name_Overall);
        sqLiteDatabase.execSQL("DELETE FROM "+table_name_School);
        sqLiteDatabase.execSQL("DELETE FROM "+table_name_Class);
    }
    public void downloadClass(final LinearLayout linearLayout, final LayoutInflater inflater){

        collectionReference.whereEqualTo("school_code",schoolcode).whereEqualTo("grade",grade).orderBy("rankClass").limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.i(TAG, "successful");

                            Store(task,table_name_Class,linearLayout,inflater);

                    }
                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void Store(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task,String tablename,LinearLayout linearLayout,LayoutInflater inflater){
        int rank=0;
        try {
            sqLiteDatabase.execSQL("DELETE FROM "+tablename);
        }catch (Exception e){
            e.printStackTrace();
        }

        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.i(TAG, document.getId() + " => " + document.getData());
            if(tablename.equals(table_name_Overall)){
                rank=document.getLong("rankOverall").intValue();
            }else if(tablename.equals(table_name_School)){
                rank=document.getLong("rankSchool").intValue();
            } else if (tablename.equals(table_name_Class)) {
                rank=document.getLong("rankClass").intValue();
            }

            String name=document.getString("name");

            int xcore=document.getLong("xcore").intValue();
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+tablename+" ( '"+rankId+"'INTEGER,'"+xcoreId+"' INTEGER,'"+nameId+"' VARCHAR)");
            sqLiteDatabase.execSQL("INSERT INTO "+tablename+" VALUES('"+rank+"','"+xcore+"','"+name+"')");


        }
        showLinear(tablename,linearLayout,inflater);
        setLastUpdate(tablename);
        apply();
    }


    private String getFieldKey(String id, String fieldKey) {

        return  Key_NAME + id +"_"+fieldKey;
    }
    public void setLastUpdate(String tablename){
        Calendar c=Calendar.getInstance();

        lastUpdate=formatter.format(c.getTime());
        Log.i(TAG,"lastUpdate"+lastUpdate);
        editor.putString(getFieldKey(id,lastUpdate_TAG+tablename),lastUpdate);

    }
    public void setLastUpdate(String tablename,int i){
        Calendar c=Calendar.getInstance();
        c.add(Calendar.DATE,i);
        lastUpdate=formatter.format(c.getTime());
        Log.i(TAG,"lastUpdate"+lastUpdate);
        editor.putString(getFieldKey(id,lastUpdate_TAG+tablename),lastUpdate);
        editor.apply();

    }
    public void showTable(LinearLayout linearLayout,String table,LayoutInflater inflater){

        if(table.equals(table_name_Class)){

            if(checkLastUpdate(table_name_Class)){
                downloadClass(linearLayout,inflater);
                Log.i(TAG,"check true"+table_name_Class);
            }else {
                Log.i(TAG,"check false"+table_name_Class);
                showLinear(table_name_Class,linearLayout,inflater);
            }
        }else if(table.equals(table_name_School)){
            if(checkLastUpdate(table_name_School)){
                Log.i(TAG,"check true"+table_name_School);
                downloadSchool(linearLayout,inflater);
            }else {
                Log.i(TAG,"check false"+table_name_School);
                showLinear(table_name_School,linearLayout,inflater);
            }
        }else if(table.equals(table_name_Overall)){
            if(checkLastUpdate(table_name_Overall)){
                Log.i(TAG,"check true"+table_name_Overall);
                downloadOverall(linearLayout,inflater);
            }else {
                Log.i(TAG,"check false"+table_name_Overall);
                showLinear(table_name_Overall,linearLayout,inflater);
            }
        }

    }
    public void StorePositionFromCloud(final TextView textView, final String name){
        Log.i(TAG,"called");
        shared share=new shared(context);
        collectionReference.document(share.getCurrent_kid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i(TAG,"on complete");
                if(task.isSuccessful()){
                    if(task.getResult().exists()) {
                        Log.i(TAG, "sql " + task.getResult());
                        if(task.getResult().getLong("rankOverall")==null){
                            setPositionOverall(-1);
                        }else {
                            setPositionOverall(task.getResult().getLong("rankOverall").intValue());

                        }
                        if(task.getResult().getLong("rankSchool")==null){
                            setPositionSchool(-1);
                        }else {
                            setPositionSchool(task.getResult().getLong("rankSchool").intValue());
                        }
                        if(task.getResult().getLong("rankClass")==null){
                            setPositionClass(-1);
                        }else {
                            setPositionClass(task.getResult().getLong("rankClass").intValue());
                        }
                        apply();
                        if(name.equals(positionClass_TAG)){
                            if(!(getPositionClass()==-1)) {
                                textView.setText(Integer.toString(getPositionClass()));
                            }else {
                                textView.setText("");
                            }
                        }
                        if(name.equals(positionSchool_TAG)){
                            if(!(getPositionSchool()==-1)) {
                                textView.setText(Integer.toString(getPositionSchool()));
                            }else {
                                textView.setText("");
                            }
                        }
                        if(name.equals(positionOverall_TAG)){
                            if(!(getPositionOverall()==-1)) {
                                textView.setText(Integer.toString(getPositionOverall()));
                            }else {
                                textView.setText("");
                            }
                        }
                        setLastUpdate(positionTag);
                        apply();
                    }}
            }
        });

    }
    public void apply(){
        editor.apply();
    }
    public boolean checkLastUpdate(String tablename){
        Calendar c=Calendar.getInstance();
        c.add(Calendar.HOUR,-2);
        boolean b=true;
        Log.i(TAG,c.toString());
        lastUpdate=settings.getString(getFieldKey(id,lastUpdate_TAG+tablename),empty);
        Log.i(TAG,"last update string"+lastUpdate);
        try{
            Date last=formatter.parse(lastUpdate);
            Calendar lastC=Calendar.getInstance();
            lastC.setTime(last);
            if(lastC.getTimeInMillis()>c.getTimeInMillis()){
                b=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }


    public int getPositionClass() {
        positionClass=settings.getInt(getFieldKey(id,positionClass_TAG),empty_int);
        return positionClass;
    }

    public int getPositionOverall() {
        positionOverall=settings.getInt(getFieldKey(id,positionOverall_TAG),empty_int);
        return positionOverall;
    }

    public int getPositionSchool() {
        positionSchool=settings.getInt(getFieldKey(id,positionSchool_TAG),empty_int);
        return positionSchool;
    }

    public void setPositionOverall(int positionOverall) {
        editor.putInt(getFieldKey(id,positionOverall_TAG),positionOverall);
        this.positionOverall = positionOverall;
    }

    public void setPositionClass(int positionClass) {
        editor.putInt(getFieldKey(id,positionClass_TAG),positionClass);
        this.positionClass = positionClass;
    }

    public void setPositionSchool(int positionSchool) {
        editor.putInt(getFieldKey(id,positionSchool_TAG),positionSchool);
        this.positionSchool = positionSchool;
    }
}
