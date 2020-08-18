package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class Task_list extends AppCompatActivity {
    ArrayList<String> task_list;
    private String key_ex_video_download="ex_download";
    int j, n;
    Handler handler;
    int level;
    int task;
    Runnable runnable;
    Dialog dialogSports;
    boolean receiver;
    TextView skip;
    NetworkCheck networkCheck;
    TextView timerText, timerTime;
    String TAG = "Task_list";
    Dialog dialog;
    task_details taskDetails;
    shared share;
    String course_type;
    TextView levelTitle;
    HashSet<String> newSet;
    ImageView close;
    private static final int PERMISSION_CODE = 1002;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        skip = findViewById(R.id.skip);
        networkCheck=new NetworkCheck();
        close=findViewById(R.id.back);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        handler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                newSet = new HashSet<String>(preferences.getStringSet(key_ex_video_download, new HashSet<String>()));
                if(newSet.isEmpty()){
                    dialog.dismiss();
                    Log.i(TAG,"runnable");


                }else {
                    handler.postDelayed(runnable, 3000);
                }
                Log.i(TAG," not runnable "+newSet.toString());
            }
        };

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        levelTitle=findViewById(R.id.title);
        share = new shared(this);
        timerText = findViewById(R.id.taskUnlockText);
        timerTime = findViewById(R.id.taskUnlockTime);
        course_type = share.getCurrent_course_type();
        task_list = new ArrayList<String>();
        taskDetails = new task_details(this, share.getCurrent_kid(), course_type);
        level =taskDetails.getCurrent_level();
        task = taskDetails.getCurrent_task();
        levelTitle.setText("LEVEL "+level);
        Button start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "button clicked");

                if (taskDetails.checkTaskUnlock()) {
                  askPermission();
                } else {
                    showDialog_TaskUnlock();
                }

            }
        });
        if (course_type.equals(getString(R.string.rubik_type))) {
            sql_rubik sqlRubik=new sql_rubik(this,share.getCurrent_kid());
            if(!(sqlRubik.getTasktype(level,task).equals(getString(R.string.one_one_type)))) {
                skip.setVisibility(View.VISIBLE);
            }
        }
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Task_list.this, Task_finish.class);
                intent.putExtra("skip", true);
                intent.putExtra("course_type", getString(R.string.rubik_type));
                finish();
                startActivity(intent);
            }
        });
        if (taskDetails.checkTaskUnlock()) {
            timerTime.setVisibility(View.GONE);
            timerText.setVisibility(View.GONE);
        } else {
            timerTime.setText(taskDetails.getTaskUnlock());
            timerTime.setVisibility(View.VISIBLE);
            timerText.setVisibility(View.VISIBLE);
        }

        if(course_type.equals(getString(R.string.rubik_type))){
            sql_rubik sqlRubik=new sql_rubik(this,share.getCurrent_kid());
            n=sqlRubik.getTaskNumber(level);
        }else {
            n = taskDetails.getCurrent_task_number();
        }
        j = taskDetails.getCurrent_task();
        int task = j;
        Log.i(TAG, "kid number" + share.getCurrent_kid());
        Log.i(TAG, "course_type" + course_type);
        Log.i(TAG, "current task number" + n);
        Log.i(TAG, "current task " + j);
        Log.i(TAG, "current level" + taskDetails.getCurrent_level());

        for (int i = 1; i <= n; i++) {
            task_list.add("- Task " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.listview_task, R.id.list_text, task_list) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(R.id.list_text);
                ImageView imageView=(ImageView)view.findViewById(R.id.image);

                Log.i(TAG, "position" + position);
                Log.i(TAG, "hey" + (j - 1));
                if (position < j - 1) {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setAlpha(0.5f);
                    textView.setTextColor(getResources().getColor(R.color.white));
                } else if (position == j - 1) {
                    imageView.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "if task" + (j - 1));
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setAlpha(1f);
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "else");
                    textView.setTextColor(getResources().getColor(R.color.orange));
                }


                return view;
            }
        };
        if (share.getCurrent_course_type().equals(getString(R.string.rubik_type))) {

            //----------------------------------rubik--------------------------------------------------

        }


        ListView listView = findViewById(R.id.listview);

        listView.setAdapter(adapter);
    }
    public void goTONext(){
        Intent intent;
        if (course_type.equals(getString(R.string.rubik_type))) {
            sql_rubik sqlRubik=new sql_rubik(this,share.getCurrent_kid());
            if(sqlRubik.getTasktype(level,task).equals("")){
                if(networkCheck.internetConnectionAvailable(5000)) {
                    sqlRubik.Download_all_tasks();

                }else {
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    Task_list.this.registerReceiver(Task_list.this.mReceiver, filter);
                    receiver=true;
                    showDialog_No_InternetConnection();
                }
            }else {
                intent = new Intent(Task_list.this, task_activity_rubik.class);
                finish();
                startActivity(intent);
            }
        } else if (course_type.equals(getString(R.string.sport_type))) {
            newSet = new HashSet<String>(preferences.getStringSet(key_ex_video_download, new HashSet<String>()));

            if(CheckVideo()) {
                Log.i(TAG, "sport type");
                sql_sports sqlSports = new sql_sports(Task_list.this, share.getCurrent_kid());
                sport_details_class details_class = sqlSports.readDetails(level, task);
                if(details_class.ex_type.equals("")){
                    if(networkCheck.internetConnectionAvailable(5000)) {
                        sqlSports.download_next_task(level, task);

                    }else {
                        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        Task_list.this.registerReceiver(Task_list.this.mReceiver, filter);
                        receiver=true;
                        showDialog_No_InternetConnection();
                    }
                }else {
                    if (details_class.getEx_type().equals(getString(R.string.learn))) {
                        Log.i(TAG, "learn type");
                        intent = new Intent(Task_list.this, exer_learn.class);
                         finish();
                        startActivity(intent);
                    } else if (details_class.getEx_type().equals(getString(R.string.do_type))) {
                        Log.i(TAG, "do type");
                        intent = new Intent(Task_list.this, exercise_do1.class);
                        finish();
                        startActivity(intent);
                    } else if (details_class.getEx_type().equals(getString(R.string.pose))) {
                        Log.i(TAG, "pose type");
                        intent = new Intent(Task_list.this, pose_day.class);
                        finish();
                        startActivity(intent);
                    }
                }
            }
        } else if (course_type.equals(getString(R.string.health_type))) {

            Log.i(TAG, "health type");
            sql_health sqlHealth = new sql_health(Task_list.this, share.getCurrent_kid());
            sport_details_class details_class = sqlHealth.readDetails(level, task);
            newSet = new HashSet<String>(preferences.getStringSet(key_ex_video_download, new HashSet<String>()));


                Log.i(TAG, "service set is not empty");
            if(CheckVideo()){
                if(details_class.ex_type.equals("")){
                    if(networkCheck.internetConnectionAvailable(5000)) {
                        sqlHealth.download_next_task(level, task);

                    }else {
                        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        Task_list.this.registerReceiver(Task_list.this.mReceiver, filter);
                        receiver=true;
                        showDialog_No_InternetConnection();
                    }
                }else {
                    if (details_class.getEx_type().equals(getString(R.string.learn))) {
                        Log.i(TAG, "learn type");
                        intent = new Intent(Task_list.this, exer_learn.class);
                        finish();
                        startActivity(intent);
                    } else if (details_class.getEx_type().equals(getString(R.string.do_type))) {
                        Log.i(TAG, "do type");
                        intent = new Intent(Task_list.this, exer_do_basic.class);
                        finish();
                        startActivity(intent);
                    } else if (details_class.getEx_type().equals(getString(R.string.pose))) {
                        Log.i(TAG, "pose type");
                        intent = new Intent(Task_list.this, pose_day.class);
                        finish();
                        startActivity(intent);
                    }
                }
            }
        }
    }

    public void showDialog_TaskUnlock() {

        Dialog dialog = new Dialog(Task_list.this);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.task_unlock_layout);
        Window window = dialog.getWindow();
        TextView time = dialog.findViewById(R.id.time);
        time.setText(taskDetails.getTaskUnlock());

        Log.i("main", "pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
    public void showDialog_Downloading() {

        dialog = new Dialog(Task_list.this);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.uploading_dialog);
        Window window = dialog.getWindow();
        TextView time = dialog.findViewById(R.id.upload);
        time.setText("Downloading");
        TextView close = dialog.findViewById(R.id.closeText);
        close.setVisibility(View.GONE);

        Log.i("main", "pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        handler.postDelayed(runnable,3000);
    }
    public void showDialog_InsufficientStorage(){

       Dialog dialog = new Dialog(Task_list.this);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.insufficeint_storage);
        Window window = dialog.getWindow();
        Log.i(TAG, "pop_up_insufficient storage");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void askPermission() {
        String[] permission = new String[4];
        int count = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if ((ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) || (ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) || (ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) ||
                (ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) || (ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)) {
            //show pop_u

                if ((ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)) {
                    permission[count] = Manifest.permission.CAMERA;
                    count++;
                }
                if ((ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) || (ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                    permission[count] = Manifest.permission.READ_EXTERNAL_STORAGE;
                    count++;
                    permission[count] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                if ((ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED)) {
                    permission[count] = Manifest.permission.READ_PHONE_STATE;
                    count++;
                }
                if ((ActivityCompat.checkSelfPermission(Task_list.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)) {
                    permission[count] = Manifest.permission.RECORD_AUDIO;
                    count++;
                }

                requestPermissions(permission, PERMISSION_CODE);
            } else {
            goTONext();
                   //granted
            }
        }else {
            goTONext();
            //granted
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         Log.i(TAG,"permision result");
        if (requestCode == PERMISSION_CODE) {
            Log.i(TAG,"permision code");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG,"permision geant result");
                if ((ContextCompat.checkSelfPermission(Task_list.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(Task_list.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(Task_list.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(Task_list.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(Task_list.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                    Log.i("permission","granted");
                    goTONext();
                    //granted
                }
            }else{
                Toast.makeText(Task_list.this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void showDialog_No_InternetConnection(){

        dialogSports = new Dialog(Task_list.this);

        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.no_internet_connection);
        Window window = dialogSports.getWindow();
        TextView close=dialogSports.findViewById(R.id.text);
        close.setText("Connect to Internet");

        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSports.show();
    }

    public boolean CheckVideo(){
        String key_downloaded="downloaded";
        Log.i(TAG,"chech video");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
       boolean downloaded=preferences.getBoolean(key_downloaded,false);
        Log.i(TAG,"boolen downloaded "+downloaded);
        if(!isMyServiceRunning(VideoService.class)&&(!downloaded)){
            Log.i(TAG,"service not running not downloaded");
            ContextWrapper cw = new ContextWrapper(getApplication());
            File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);

            double availableSizeInBytes=new StatFs(directory.getPath()).getAvailableBytes();
            double mb=availableSizeInBytes/(1024*1024);
            double threshold=13.0;
            if(mb>threshold){
                if(networkCheck.internetConnectionAvailable(5000)) {
                    Log.i(TAG, "memory available");
                    HashSet<String> newSet = new HashSet<String>(preferences.getStringSet(key_ex_video_download, new HashSet<String>()));
                    Log.i(TAG, "new String" + newSet.toString());
                    if (!newSet.isEmpty()) {
                        Log.i(TAG, "service set is not empty");
                        Intent intent = new Intent(this, VideoService.class);
                        intent.putExtra("mySet", newSet);
                        startService(intent);
                        showDialog_Downloading();

                    } else {
                        Log.i(TAG, "service set is empty");

                        HashSet<String> set = new HashSet<>();
                        for (int i = 1; i < 43; i++) {
                            set.add(Integer.toString(i));
                        }
                        set.add(getString(R.string.music));
                        Intent intent = new Intent(this, VideoService.class);
                        intent.putExtra("mySet", set);
                        startService(intent);
                        showDialog_Downloading();
                    }
                }else {
                    showDialog_No_InternetConnection();
                    receiver=true;
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    Task_list.this.registerReceiver(Task_list.this.mReceiver, filter);
                }
            }else{
                Log.i(TAG,"memory unavailable");

                showDialog_InsufficientStorage();
                //not enough storage
            }
            return false;
        }else if(isMyServiceRunning(VideoService.class)){
            Log.i(TAG,"service running");

            showDialog_Downloading();
           return false;

        }else {
            Log.i(TAG,"start task");
            return true;
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                    goTONext();
                    receiver=false;
                    Task_list.this.unregisterReceiver(this);
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");
                }
            }
        }

    };

    @Override
    protected void onPause() {
        if(receiver){
            unregisterReceiver(mReceiver);
        }
        super.onPause();
    }
}

