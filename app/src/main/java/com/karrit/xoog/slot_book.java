package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
public class slot_book extends AppCompatActivity {
    HashMap<Integer, String> start;
    public boolean used_1[];
    public boolean used_2[];
    public boolean used_3[];
    public boolean used_4[];
    shared share;
    String topic;
    int slot_start, slot_end;
    String date_delete;
    Dialog dialogSubs;
    NetworkCheck networkCheck;
    int count;
    Dialog dialogSports;
    ProgressBar progressBar;
    HashMap<Integer, String> end;
    HashMap<String, Integer> start_reverse;
    HashMap<String, Integer> end_reverse;
    private HorizontalCalendar horizontalCalendar;
    Calendar selectedDate;
    FirebaseFirestore db;
    String TAG = "schedule";
    ArrayList<ArrayList<String>> timeSlot;
    ArrayList<String> timeslot_2;
    task_details task_details;
    int change;
    SimpleDateFormat dateFormat;
    String mentor_id;
    Boolean selected;
    int level,Task;
    String start_date_string, end_date_string, second_date_string, third_date_string;
    int time;
    ListView listView_1, listView_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_book);

        selected=false;
        share = new shared(slot_book.this);
        task_details=new task_details(this,share.getCurrent_kid(),getString(R.string.rubik_type));

        //---------------------time slot init---------------------------
        timeSlot = new ArrayList<ArrayList<String>>(4);


        timeslot_2 = new ArrayList<String>();

        progressBar=findViewById(R.id.progress);
        //-------------------------get topic Intent-----------------------------------
        Intent i=getIntent();
       topic=i.getStringExtra("topic");
        TextView topic_text_view=findViewById(R.id.topic);
        time = i.getIntExtra("time", 0);
        Log.i(TAG,"time"+time);
        change=i.getIntExtra("change_slot",0);
        Log.i(TAG,"time " +time);
        topic_text_view.setText(topic);

        //--------------------------contact us-------------------------------------
        TextView contact=findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(slot_book.this,menu_book_call.class);
                startActivity(intent);
            }
        });

        //---------------------------------------------------------------------------
        if(time==60){
            count=3;
        }else if(time==40){
            count=2;
        }else{
            count=1;
        }







        start = new HashMap<Integer, String>();
        end = new HashMap<Integer, String>();
        start_reverse = new HashMap<String, Integer>();
        end_reverse = new HashMap<String, Integer>();


        final Calendar startDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd_MM_yyyy");


        startDate.add(Calendar.DATE, 1);
        listView_1 = findViewById(R.id.listview);
        // listView_2=findViewById(R.id.listView_2);
        used_1 = new boolean[39];

        Arrays.fill(used_1, true);
        used_2 = new boolean[39];
        Arrays.fill(used_2, true);
        used_3 = new boolean[39];

        Arrays.fill(used_3, true);
        used_4 = new boolean[39];

        Arrays.fill(used_4, true);
        setString();

        db = FirebaseFirestore.getInstance();


        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 4);
        Calendar secondDate = Calendar.getInstance();
        secondDate.add(Calendar.DATE, 2);
        Calendar thirdDate = Calendar.getInstance();
        thirdDate.add(Calendar.DATE, 3);
        start_date_string = dateFormat.format(startDate.getTime());
        end_date_string = dateFormat.format(endDate.getTime());
        second_date_string = dateFormat.format(secondDate.getTime());
        third_date_string = dateFormat.format(thirdDate.getTime());
        selectedDate = startDate;

//check internet connection------------------------------------------
        networkCheck=new NetworkCheck();
        if(networkCheck.internetConnectionAvailable(5000)) {
            getMentorId();
        }else {

                Log.i(TAG, "no network");
                showDialog_start();
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                slot_book.this.registerReceiver(slot_book.this.mReceiver, filter);

        }
       // loadTimeSlot(start_date_stri
            // ng);
        task_details=new task_details(this,share.getCurrent_kid(),getString(R.string.rubik_type));
        level=task_details.getCurrent_level();
        Task=task_details.getCurrent_task();


        //  listView_2.setAdapter(arrayAdapter_2);


        Log.i(TAG, start_date_string + "   " + end_date_string);


        horizontalCalendar = new HorizontalCalendar.Builder(slot_book.this, R.id.calendarView).range(startDate, endDate).datesNumberOnScreen(3).mode(HorizontalCalendar.Mode.DAYS).build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (selectedDate.getTimeInMillis() != date.getTimeInMillis()) {
                    selectedDate = date;
                    selected=false;
                    if(mentor_id!=null){
                    loadTimeSlot(dateFormat.format(date.getTime()));}
                    Log.i(TAG,"date"+dateFormat.format(date.getTime()));
                    progressBar.setVisibility(View.VISIBLE);
                    listView_1.setVisibility(View.INVISIBLE);
                }
            }
        });
//---------------------Confirm button on click--------------------------
        final Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(selected){
                    confirm.setClickable(false);
                if(change==1){
                    Log.i(TAG,"change is 1");
                    final CollectionReference collectionReference_user=db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()+"-link");
                    collectionReference_user.whereEqualTo("level",level).whereEqualTo("task",Task).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(!task.getResult().isEmpty()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DocumentReference doc= document.getDocumentReference(document.getId());
                                        date_delete=document.getString("date");
                                        Log.i(TAG,"Date"+date_delete);
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        collectionReference_user.document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.i(TAG,"delete user link completed");
                                                delete_mentor(date_delete);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });

                }else{
                    Log.i(TAG,"no change");
                    confirm_upload();
                }

            }else{
                    Toast.makeText(slot_book.this,"Choose Time Slot",Toast.LENGTH_SHORT).show();
                }
        }});


    }

    public void delete_mentor(String Date){
        final CollectionReference Reference_mentor= db.collection("mentor").document(mentor_id).collection(Date);
        Log.i(TAG,"delete Date"+Date);
        Log.i(TAG,"mentor id "+mentor_id);
        Reference_mentor.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                Log.i(TAG,"on complete test");
            }
        });

        Reference_mentor.whereEqualTo("user_id",share.getCurrent_kid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i(TAG,"on complete");
                if(task.isSuccessful()){
                    Log.i(TAG,"delete task success");
                    if(!task.getResult().isEmpty()){
                        Log.i(TAG,"delete not empty success");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.i(TAG,"delete for success");
                            DocumentReference doc= document.getDocumentReference(document.getId());
                            Reference_mentor.document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    count--;

                                    Log.i(TAG,"mentor deleted"+count);
                                    if(count==0){
                                        confirm_upload();}
                                }
                            });
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    }
                }else{
                    Log.i(TAG,"delete task unsuccesful");
                }
            }
        });
    }
    public void getMentorId(){
        if(task_details.getMentor_id().equals(getString(R.string.empty))){
            Log.i(TAG,"get mentor if");
            db.collection("mentor").document("mentor").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        mentor_id=task.getResult().getString("current_mentor_id");
                        task_details.setMentor_id(mentor_id);
                        task_details.apply();
                        loadTimeSlot(start_date_string);
                        db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_rubik_type").update("mentor_id",mentor_id);

                    }else{
                        Toast.makeText(slot_book.this,"Error! Try Again Later",Toast.LENGTH_LONG);
                    }
                }
            });

        }else{
            Log.i(TAG,"get mentor else");
            db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_rubik_type").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG,"result "+task.getResult());
                        Log.i(TAG,"got mentor");
                        mentor_id = task.getResult().getString("mentor_id");
                        Log.i(TAG,"got mentor"+mentor_id);
                        task_details.setMentor_id(mentor_id);
                        task_details.apply();
                        if(mentor_id!=null) {
                            loadTimeSlot(start_date_string);
                        }else {
                            task_details.setMentor_id(getString(R.string.empty));
                            task_details.apply();
                            getMentorId();
                        }
                    }
                }});


        }
    }

    public void confirm_upload(){
        task_details task_rubik=new task_details(slot_book.this,share.getCurrent_kid(),getString(R.string.rubik_type));

        Log.i(TAG, slot_start + " hello" + slot_end);
        if(task_rubik.getCourse_id().equals(getString(R.string.trial))){
            if(change==1){
                Log.i(TAG,"change is 1 in trial upload");
                SimpleDateFormat simple=new SimpleDateFormat("dd/MM/yy hh:mm:ss aa");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
                String s=new String();
                try {
                    Date hour = timeFormat.parse(String.valueOf(end.get(slot_end)));
                    Calendar h = Calendar.getInstance();
                    h.setTime(hour);
                    Long date_long = selectedDate.getTimeInMillis() + h.getTimeInMillis() + (5 * 60 * 60 * 1000) + (30 * 60 * 1000);
                    Date d = new Date(date_long);
                    Log.i(TAG, simple.format(d));
                    s = simple.format(d);
                    task_rubik.setExp(s);
                    task_rubik.apply();
                    Log.i(TAG,"exp trial"+task_rubik.getExp());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }




        if(task_rubik.getCourse_id().equals(getString(R.string.new_user))){
            Log.i(TAG,"entered into if");

            SimpleDateFormat simple=new SimpleDateFormat("dd/MM/yy hh:mm:ss aa");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            String s=new String();
            try {
                Date hour = timeFormat.parse(String.valueOf(end.get(slot_end)));
                Calendar h = Calendar.getInstance();
                h.setTime(hour);
                Long date_long=selectedDate.getTimeInMillis()+h.getTimeInMillis()+(5*60*60*1000)+(30*60*1000);
                Date d=new Date(date_long);
                Log.i(TAG,simple.format(d));
                s=simple.format(d);


            }catch (Exception e){
                e.printStackTrace();
            }
            task_rubik.newTask(getString(R.string.trial),0);
            task_rubik.setCurrent_task_number(1);
            task_rubik.setExp(s);
            sql_rubik sqlRubik=new sql_rubik(this,share.getCurrent_kid());
            task_rubik.apply();
            Log.i(TAG,"course id"+task_rubik.getCourse_id());
            Log.i(TAG,"exp"+task_rubik.getEXP_String());
            FirebaseFirestore instance = FirebaseFirestore.getInstance();
            DocumentReference db = instance.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_" + getString(R.string.rubik_type));
            HashMap<String,Object> task= new HashMap<String, Object>();
            task.put("EXP",task_rubik.getExp());
            task.put("current_level",1);
            task.put("current_task",1);
            task.put("DOJ", Timestamp.now());
            task.put("mentor_id",mentor_id);
            task.put("current_task_number",sqlRubik.getTaskNumber(1));
            task.put("course_id",getString(R.string.trial));
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



        }
        HashMap<String,Object> put_user=new HashMap<String, Object>();
        String time_str=start.get(slot_start)+"-"+end.get(slot_end);
        put_user.put("time_string",time_str);
        put_user.put("date",dateFormat.format(selectedDate.getTime()));
        put_user.put("level",task_rubik.getCurrent_level());
        put_user.put("task",task_rubik.getCurrent_task());
        put_user.put("timezone", TimeZone.getDefault());
        put_user.put("topic",topic);
        put_user.put("link",getString(R.string.empty));
        db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()+"-link").add(put_user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.i(TAG,"put user uploaded");
            }
        });
        Log.i(TAG,"level"+task_rubik.getCurrent_level());
        Log.i(TAG,"task"+task_rubik.getCurrent_task());
        Log.i(TAG,"id"+share.getCurrent_kid());
        task_rubik.setOne_one_Time(task_rubik.getCurrent_level(),task_rubik.getCurrent_task(),time_str);
        task_rubik.setOne_one_Date(task_rubik.getCurrent_level(),task_rubik.getCurrent_task(),dateFormat.format(selectedDate.getTime()));
        task_rubik.setOne_one_link(task_rubik.getCurrent_level(),task_rubik.getCurrent_task(),getString(R.string.empty));
        task_rubik.apply();

        Log.i(TAG,task_rubik.getOne_one_Time(task_rubik.getCurrent_level(),task_rubik.getCurrent_task()));

        Log.i(TAG,task_rubik.getOne_one_Time(task_rubik.getCurrent_level(),task_rubik.getCurrent_task()));
        Log.i(TAG,task_rubik.getOne_one_Time(task_rubik.getCurrent_level(),task_rubik.getCurrent_task()));

        while (slot_end >= slot_start) {
            HashMap<String, Object> put_slot = new HashMap<String, Object>();
            put_slot.put("slot_id", slot_start);
            put_slot.put("booking", true);
            put_slot.put("timezone",TimeZone.getDefault());
            put_slot.put("topic",topic);

            put_slot.put("user_id",share.getCurrent_kid());
            db.collection("mentor").document(mentor_id).collection(dateFormat.format(selectedDate.getTime())).add(put_slot).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Log.i(TAG, "put slot_data");

                }
            });
            slot_start++;
        }
       showSlotBookedDialog();
    }


    public void loadTimeSlot(final String date) {
        Log.i(TAG,"date "+date);
        Log.i(TAG,"mentor id "+mentor_id);
        db.collection("mentor").document(mentor_id).collection(date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        showList(date);
                    } else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            arraySet(date, document.getLong("slot_id").intValue());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        showList(date);
                    }
                }
            }
        });
    }

    public void arrayInit() {
        Arrays.fill(used_1, true);
    }

    public void arraySet(String date, int slot) {
        Log.i(TAG, "arraySEt" + date);
        Log.i(TAG, "1" + start_date_string);
        Log.i(TAG, "2" + second_date_string);
        Log.i(TAG, "3" + third_date_string);
        Log.i(TAG, "4" + end_date_string);
        if (date.equals(start_date_string)) {
            used_1[slot] = false;
        } else if (date.equals(end_date_string)) {
            used_4[slot] = false;
        } else if (date.equals(second_date_string)) {
            used_2[slot] = false;
        } else if (date.equals(third_date_string)) {
            used_3[slot] = false;
        }
    }

    public void showList(String date) {
        Log.i(TAG, "showList" + date);
        Log.i(TAG, "1" + start_date_string);
        Log.i(TAG, "2" + second_date_string);
        Log.i(TAG, "3" + third_date_string);
        Log.i(TAG, "4" + end_date_string);
        final ArrayList<String> Time = new ArrayList<String>();
        int j = 0;
        boolean used[] = new boolean[39];
        if (date.equals(start_date_string)) {
            used = used_1.clone();
            j = 0;
        } else if (date.equals(second_date_string)) {
            j = 1;
            used = used_2.clone();
        } else if (date.equals(third_date_string)) {
            j = 2;
            used = used_3.clone();
        } else if (date.equals(end_date_string)) {
            j = 3;
            used = used_4.clone();
        } else {
            Toast.makeText(this, "Error retrieving Slots", Toast.LENGTH_SHORT).show();
        }
        String des;
        if (time == 20) {
            Log.i(TAG, "time 20");
            for (int i = 1; i < 39; i++) {
                if (used[i]) {
                    des = start.get(i) + "-" + end.get(i);
                    Log.i("time_slot_1_20", des);
                    Time.add(des);

                }
            }
        } else if (time == 45) {
            Log.i(TAG, "time 40");
            for (int i = 1; i < 38; i++) {
                if (used[i] && used[i + 1]) {
                    des = start.get(i) + "-" + end.get(i + 1);
                    Log.i("time_slot_1_40", des);
                    Time.add(des);
                    i++;

                }
            }
        } else if (time == 60) {
            Log.i(TAG, "time 60");
            for (int i = 1; i < 37; i++) {
                if (used[i] && used[i + 1] && used[i + 2]) {
                    des = start.get(i) + "-" + end.get(i + 2);
                    Log.i("time_slot_1_60", des);
                    Time.add(des);
                    i += 2;

                }

            }
        }


        ArrayAdapter<String> arrayAdapter_1 = new ArrayAdapter<String>(slot_book.this, R.layout.listview_slot, R.id.list_text, Time);
        listView_1.setAdapter(arrayAdapter_1);
        listView_1.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        listView_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG + "selected item", Time.get(i));
                String slot_time = Time.get(i);
              selected=true;
                String[] parts = slot_time.split("\\-"); // String array, each element is text between dots

                Log.i(TAG, "split" + parts[0] + " " + parts[1]);
                Log.i(TAG, start_reverse.get(parts[0]) + " " + end_reverse.get(parts[1]));
                slot_start = start_reverse.get(parts[0]);
                slot_end = end_reverse.get(parts[1]);
                view.setSelected(true);
            }
        });

    }


    public void setString() {
        try {
            Log.i(TAG, "setString inside try");
            ArrayList<String> timeSlots = new ArrayList<String>();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            Date dstart = timeFormat.parse("9:00 AM");
            Calendar c1 = Calendar.getInstance();
            c1.setTime(dstart);
            Date dend = timeFormat.parse("9:20 AM");
            for (int i = 1; i < 39; i++) {
                start.put(i, timeFormat.format(dstart));
                start_reverse.put(timeFormat.format(dstart), i);
                end.put(i, timeFormat.format(dend));
                end_reverse.put(timeFormat.format(dend), i);
                long start_sum = dstart.getTime() + (20 * 60 * 1000);
                long end_sum = dend.getTime() + (20 * 60 * 1000);

                Log.i(TAG, start.get(i) + "-" + end.get(i));

                dstart.setTime(start_sum);
                dend.setTime(end_sum);
                timeSlots.add(start.get(i) + "-" + end.get(i));

            }


        } catch (Exception e) {

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
                    getMentorId();
                    dialogSports.dismiss();
                    slot_book.this.unregisterReceiver(this);
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");
                }
            }
        }

    };
    public void showDialog_start(){

        dialogSports = new Dialog(slot_book.this);

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
    public void showSlotBookedDialog(){
      dialogSubs = new Dialog(slot_book.this);

        dialogSubs.setCancelable(false);
        dialogSubs.setContentView(R.layout.dialog_slot_booked);
        Window window = dialogSubs.getWindow();
        TextView close=dialogSubs.findViewById(R.id.cont);
        Log.i("main","pop_up_subs");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSubs.dismiss();
                Intent go=new Intent(slot_book.this,Home_page.class);
                go.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(go);
            }
        });

        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSubs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogSubs.show();
    }

    @Override
    protected void onDestroy() {
        if(dialogSubs!=null){
            if(dialogSubs.isShowing()){
                dialogSubs.dismiss();
            }
        }
        super.onDestroy();
    }
}
