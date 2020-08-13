package com.karrit.xoog;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class GameFragment extends Fragment {
    Button b;
    sql_rubik sql;
    task_details task_rubik;
    one_one_class oneOneClass;
FirebaseFirestore db;
    private String commonTag="common";
    private static final int PERMISSION_CODE=1001;
    View view;
    ListenerRegistration registration;
    int level,task;
    TextView sports_text,rubik_text,health_text;
  shared share;
    String TAG="gameFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.game_fragment,container,false);
       ImageView rubik_image=view.findViewById(R.id.white_sqr2);
        ImageView sport_image=view.findViewById(R.id.white_sqr1);
        ImageView health_image=view.findViewById(R.id.white_sqr3);
        db=FirebaseFirestore.getInstance();
        share=new shared(getActivity());
      final shared share=new shared(getActivity());
      Log.i(TAG,share.getCurrent_kid());
      askPermission();


      //--------------------------------setting size of Text View----------------------------------------------
        sports_text=view.findViewById(R.id.sports);
        rubik_text=view.findViewById(R.id.rubik_text);
        health_text=view.findViewById(R.id.health);
        size(sports_text,0,7,"Sport's\nFitness",1.5f);
        size(rubik_text,0,7,"Rubik's\nPuzzle",1.5f);
        size(health_text,0,8,"Health &\nEnergy",1.5f);

     //   int refIds[] = sports.getReferencedIds();
       /* for (int id : refIds) {
            view.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // your code here.
                    Log.i("fragment","onclicked");
                }
            });
        }*/
       health_image.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.i("fragment_health","onclicked");
               share.setCurrent_course_type(getString(R.string.health_type));
               share.apply();
               task_details task_sports=new task_details(getActivity(),share.getCurrent_kid(),getString(R.string.health_type));
               String course_id=task_sports.getCourse_id();
             Log.i(TAG,"course id" + "health "+course_id);
               if(course_id.equals(getString(R.string.health_course_id))){
                   if(task_sports.checkEXP()){
                       Intent i = new Intent(getActivity(),animation_activity.class);
                       Log.i(course_id,"enter");
                       startActivity(i);
                   }else{
                       task_sports.setCourse_id(getString(R.string.over));
                       task_sports.apply();
                       task_sports.createdb();
                       task_sports.setCourse_id_Cloud(getString(R.string.over));
                       Log.i(course_id,"over with exp");
                       Intent intent=new Intent(getActivity(),School_program.class);
                       intent.putExtra("flag_start",1);
                       startActivity(intent);
                       //show subs over dialog box
                   }
               }else if(course_id.equals(getString(R.string.over))){
                   Log.i(course_id,"over");
                   Intent intent=new Intent(getActivity(),School_program.class);
                   intent.putExtra("flag_start",1);
                   startActivity(intent);
                   //show subs over dialog box
               } else if(course_id.equals(getString(R.string.new_user))){
                   //show trial dialog

                   Intent intent=new Intent(getActivity(),School_program.class);
                   intent.putExtra("flag_start",1);
                   startActivity(intent);
                   Log.i(course_id,"new user");
               }
           }
       });


        sport_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("fragment_sport","onclicked");
                share.setCurrent_course_type(getString(R.string.sport_type));
                share.apply();
                task_details task_sports=new task_details(getActivity(),share.getCurrent_kid(),getString(R.string.sport_type));
                String course_id=task_sports.getCourse_id();
                Log.i("sport_type",getString(R.string.sport_type));
                Log.i(TAG,"exp date"+task_sports.getExp());
                if(course_id.equals(getString(R.string.sport_course_id))){
                    if(task_sports.checkEXP()){
                        Intent i = new Intent(getActivity(),animation_activity.class);
                        Log.i(course_id,"enter");
                        startActivity(i);
                    }else{
                        task_sports.setCourse_id(getString(R.string.over));
                        task_sports.apply();
                        task_sports.createdb();
                        task_sports.setCourse_id_Cloud(getString(R.string.over));
                        Log.i(course_id,"over with exp");
                        showSubscribeDialog();
                        //show subs over dialog box
                    }
                }else if(course_id.equals(getString(R.string.over))){
                    Log.i(course_id,"over");
                    showSubscribeDialog();
                    //show subs over dialog box
                } else if (course_id.equals(getString(R.string.trial))) {

                        Intent i = new Intent(getActivity(),animation_activity.class);
                        Log.i(course_id,"enter");
                        startActivity(i);

                    Log.i(course_id,"trial");

                }else if(course_id.equals(getString(R.string.new_user))){
                    //show trial dialog
                    showTrial_Dialog_sports();
                    Log.i(course_id,"new user");
                }
            }
        });
        rubik_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final task_details task_rubik=new task_details(getActivity(),share.getCurrent_kid(),getString(R.string.rubik_type));
               level=task_rubik.getCurrent_level();
               task=task_rubik.getCurrent_task();
                sql=new sql_rubik(getActivity(),share.getCurrent_kid());
             String task_type=sql.getTasktype(level,task);
           Log.i(TAG,"task type"+task_type);

                Log.i(TAG,"level"+level);
                Log.i(TAG,"task"+task);
                Log.i(TAG,"link"+task_rubik.getOne_one_link(level,task));
                Log.i(TAG,"date"+task_rubik.getOne_one_Date(level,task));
                Log.i(TAG,"time"+task_rubik.getOne_one_Time(level,task));

                Log.i("fragment","onclicked");
                share.setCurrent_course_type(getString(R.string.rubik_type));
                share.apply();
                Log.i(TAG,"on click"+share.getCurrent_kid());


                String course_id=task_rubik.getCourse_id();
                Log.i("string",getString(R.string.new_user));
                Log.i("course_id",course_id);
                Log.i("rubik_type",getString(R.string.rubik_type));
                if(course_id.equals(getString(R.string.rubik_course_id))){
                    if(task_type.equals(getString(R.string.one_one_type))){
                      OneOneFN();
                    }else {

                        Intent i = new Intent(getActivity(), animation_activity.class);
                        Log.i(course_id, "enter");
                        startActivity(i);
                    }

                }else if(course_id.equals(getString(R.string.over))){
                    Log.i(course_id,"over");
                    showSubscribeDialog();
                    //show subs over dialog box
                } else if (course_id.equals(getString(R.string.trial))) {
                    if(task_rubik.checkEXP()){
                        if(task_type.equals(getString(R.string.one_one_type))){
                            OneOneFN();
                        }else {
                            Intent i = new Intent(getActivity(), animation_activity.class);
                            Log.i(course_id, "enter");
                            startActivity(i);
                        }
                    }else{
                        task_rubik.setCourse_id(getString(R.string.over));
                        task_rubik.createdb();
                        task_rubik.setCourse_id_Cloud(getString(R.string.over));
                        task_rubik.apply();
                        Log.i(course_id,"over with exp");
                        //show subs over dialog box
                        showSubscribeDialog();
                    }
                    Log.i(course_id,"trial");

                }else if(course_id.equals(getString(R.string.new_user))){
                    //show trial dialog box
                    showTrial_Dialog_rubik();
                    Log.i(course_id,"new user");
                }else if(course_id.equals(getString(R.string.rubik_done))){
                    //show course completed dialog
                }
            }
        });
        return view;
    }
    public void OneOneFN(){
        task_rubik=new task_details(getActivity(),share.getCurrent_kid(),getString(R.string.rubik_type));

        Log.i(TAG,"if entered");
        if(task_rubik.getOne_one_link(level,task).equals(getString(R.string.empty))){
            Log.i(TAG,"if entered");
            db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()+"-link").whereEqualTo("level",level).whereEqualTo("task",task).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> Task) {

                    if(Task.isSuccessful()){
                        Log.i(TAG,Task.getResult().toString());
                        if(Task.getResult().isEmpty()){
                            Intent intent=new Intent(getActivity(),slot_book.class);
                            one_one_class one=sql.get_one_one(level,task);
                            intent.putExtra("topic",one.getDesciption());
                            intent.putExtra("time",one.getTime());
                            startActivity(intent);
                            Log.i(TAG,"if entered");
                        }else{
                            for (QueryDocumentSnapshot document : Task.getResult()) {

                                    task_rubik.setOne_one_link(level,task,document.getString("link"));
                                    task_rubik.setOne_one_Date(level,task,document.getString("date"));
                                    task_rubik.setOne_one_Time(level,task,document.getString("time_string"));
                                    task_rubik.apply();
                                    Log.i(TAG,"for if entered");

                                Log.i(TAG,"for  entered");
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            Log.i(TAG,"one_one"+task_rubik.getOne_one_link(level,task));
                            Log.i(TAG,"one_one"+task_rubik.getOne_one_Date(level,task));
                            Log.i(TAG,"one_one"+task_rubik.getOne_one_Time(level,task));

                                Intent intent = new Intent(getActivity(), animation_activity.class);
                                startActivity(intent);

                        }
                    }
                }
            });

        }else {


                Intent intent = new Intent(getActivity(), animation_activity.class);
                startActivity(intent);
        }

    }
    public void showTrial_Dialog_rubik(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.trial_rubik);
        Window window = dialog.getWindow();
        ImageView close=dialog.findViewById(R.id.close);
        Log.i("main","pop_up_rubik");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.dismiss();
            }
        });
        Button rubik_trial=dialog.findViewById(R.id.rubik_trial_button);
        sql_rubik sqlRubik=new sql_rubik(getActivity(),share.getCurrent_kid());
        oneOneClass=null;
        try {
          oneOneClass=  sqlRubik.get_one_one(1,1);
        }catch (Exception e){

        }


        rubik_trial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(),slot_book.class);
                if(oneOneClass!=null){
                    intent.putExtra("time", oneOneClass.getTime());
                    intent.putExtra("topic",oneOneClass.getDesciption());
                    Log.i(TAG,"one one is not null");
                }else {
                    intent.putExtra("topic","Cube Notations and Properties");
                    intent.putExtra("time", 20);
                }
                startActivity(intent);
                dialog.dismiss();
                Log.i("tag","on click rubik continue");
            }
        });
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialog.show();
    }
public void showTrial_Dialog_sports(){
    final Dialog dialogSports = new Dialog(getActivity());
    dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialogSports.setCancelable(false);
    dialogSports.setContentView(R.layout.trail_sports);
    Window window = dialogSports.getWindow();
    ImageView close=dialogSports.findViewById(R.id.close);
    Log.i("main","pop_up_rubik");
    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogSports.dismiss();
        }
    });
    Button sports_trial=dialogSports.findViewById(R.id.sports_trial_button);
    sports_trial.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             createNewFnSports();
             dialogSports.dismiss();
            Intent intent=new Intent(getActivity(),animation_activity.class);
            startActivity(intent);
            Log.i("tag","on click rubik continue");
        }
    });
    window.setGravity(Gravity.CENTER);
    window.setGravity(Gravity.CENTER_HORIZONTAL);
    dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


    dialogSports.show();
}
public void createNewFnSports(){
        task_details taskSports=new task_details(getActivity(),share.getCurrent_kid(),getString(R.string.sport_type));
    DocumentReference db = FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("task_" + getString(R.string.sport_type));
    HashMap<String,Object> task= new HashMap<String, Object>();
    task.put("EXP","");
    task.put("current_level",1);
    task.put("current_task",1);
    task.put("DOJ", Timestamp.now());
    task.put("current_task_number",2);
    task.put("course_id",getString(R.string.trial));
    db.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            Log.i(TAG,"task_details uploaded");
        }
    });
    taskSports.newTask(getString(R.string.trial),0);
    taskSports.setCurrent_task_number(2);
    taskSports.apply();

}
public void showSubscribeDialog(){
    final Dialog dialogSubs = new Dialog(getActivity());
    dialogSubs.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialogSubs.setCancelable(false);
    dialogSubs.setContentView(R.layout.subs_over);
    Window window = dialogSubs.getWindow();
    ImageView close=dialogSubs.findViewById(R.id.close);
    Log.i("main","pop_up_subs");
    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogSubs.dismiss();
        }
    });
    Button subs_now=dialogSubs.findViewById(R.id.subscribe_now);
   subs_now.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogSubs.dismiss();

            Intent intent=new Intent(getActivity(),choose_plan.class);

            startActivity(intent);
            Log.i("tag","on click subs over");
        }
    });
    window.setGravity(Gravity.CENTER);
    window.setGravity(Gravity.CENTER_HORIZONTAL);
    dialogSubs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


    dialogSubs.show();
}
public void XcoreXcashSnapShotListener(){
        DocumentReference documentReference=FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details");
    registration=documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
          if(e==null){
              UpdateXcoreandXcash(documentSnapshot);
          }
        }
    });
}
public void UpdateXcoreandXcash(DocumentSnapshot documentSnapshot){
        boolean updateXcore=false;
        boolean updateXcash=false;
        int xcore=0;
        int xcash=0;
        account_details accountDetails=new account_details(getActivity(),share.getCurrent_kid());
        if(documentSnapshot!=null){
            if(documentSnapshot.getBoolean("update")) {
                Log.i(TAG, "document Refernce" + documentSnapshot.getData());
                if (documentSnapshot.getLong("xcash").intValue() != accountDetails.getXcash()) {
                    xcash = documentSnapshot.getLong("xcash").intValue() - accountDetails.getXcash();
                    if (xcash > 0) {
                        updateXcash = true;
                    }
                    accountDetails.setXcash(documentSnapshot.getLong("xcash").intValue());
                    accountDetails.apply();
                    FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details").update("update",false);


                    Log.i(TAG, "xcash updated" + accountDetails.getXcash());

                }
                if (documentSnapshot.getLong("xcore").intValue() != accountDetails.getXcore()) {
                    xcore = documentSnapshot.getLong("xcore").intValue() - accountDetails.getXcore();
                    if (xcore > 0) {
                        updateXcore = true;
                    }
                    FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details").update("update",false);

                    accountDetails.setXcore(documentSnapshot.getLong("xcore").intValue());
                    accountDetails.apply();

                    Log.i(TAG, "xcash updated" + accountDetails.getXcore());
                }
                if (updateXcash && updateXcore) {
                    showPointUpdate(xcore, xcash);
                    //show dialog
                } else if (updateXcash) {
                    showPointUpdate(0, xcash);

                } else if (updateXcore) {
                    showPointUpdate(xcore, 0);

                }
            }
        }
}

    @Override
    public void onStart() {
        XcoreXcashSnapShotListener();
        Log.i(commonTag,TAG+" on start");
        super.onStart();
    }

    @Override
    public void onPause() {
        registration.remove();
        Log.i(commonTag,TAG+ " onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(commonTag,TAG+" on destroy");
        super.onDestroy();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(commonTag,TAG+" on start");
        super.onCreate(savedInstanceState);
    }
    public void size(TextView textView,int start, int end,String s,float size){
        SpannableString span = new SpannableString(s);
        span.setSpan(new RelativeSizeSpan(size),start,end,SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(span);
    }
    public void showPointUpdate(int xcore,int xcash){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_credit_update);
        Window window = dialog.getWindow();
        ImageView close=dialog.findViewById(R.id.close);
        Log.i("main","pop_up_rubik");
        TextView xcoreText=dialog.findViewById(R.id.xcore_text);
        TextView xcashText=dialog.findViewById(R.id.xcash_text);
        TextView xcoreNUM=dialog.findViewById(R.id.xcore);
        TextView xcashNUM=dialog.findViewById(R.id.xcash);
        if(xcore!=0){
            xcoreNUM.setText(Integer.toString(xcore));
        }else {
            xcoreNUM.setVisibility(View.GONE);
            xcoreText.setVisibility(View.GONE);
        }
        if(xcash!=0){
            xcashNUM.setText(Integer.toString(xcash));
        }else {
            xcashNUM.setVisibility(View.GONE);
            xcashText.setVisibility(View.GONE);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialog.show();
    }
    public void askPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)||(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)||(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)||
                    (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_DENIED)||(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED)) {
                //show pop_up

                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE};


                requestPermissions(permission, PERMISSION_CODE);
            }
            }
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permission","granted");
                      //granted
                }
            }else{
                Log.i(TAG,"permision denied");
            }
        }
    }
}
