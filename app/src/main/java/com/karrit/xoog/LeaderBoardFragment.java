package com.karrit.xoog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LeaderBoardFragment extends Fragment implements View.OnClickListener {
    private String TAG="leaderBoard_fragment";
    TextView leaderposition,xcore;
    CollectionReference collectionReference;
    private String positionTag="position";
    private String positionOverall_TAG="positionOverall";
    private String positionSchool_TAG="positionSchool";
    private String positionClass_TAG="positionClass";
    LinearLayout listView;
    TextView overall,schoolWise,classwise;
    String schoolCode,grade;
    private String table_name_Overall="overall_leaderBoard";
    shared share;
    private String table_name_School;
    private String table_name_Class;
    boolean r1,r2,r3;
    private String commonTag="common";
View view;
sql_leaderBoard leaderBoard;
    ListenerRegistration registration1;
    ListenerRegistration registration2;

    ListenerRegistration registration3;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.leaderboard_fragment,container,false);
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_list_leaderboard, listView, false);
        listView=view.findViewById(R.id.listView);
        leaderposition=view.findViewById(R.id.leader_num);
        xcore=view.findViewById(R.id.credit_num);

        share=new shared(getActivity());
        leaderBoard=new sql_leaderBoard(getActivity(),share.getCurrent_kid());


        table_name_Class="class_"+share.getCurrent_kid();
        table_name_School="school_"+share.getCurrent_kid();

        account_details accountDetails=new account_details(getActivity(),share.getCurrent_kid());
        schoolCode=accountDetails.getSchoolId();
        xcore.setText(Integer.toString(accountDetails.getXcore()));
        leaderBoard.showPosition(leaderposition,positionOverall_TAG);

        r1=false;
        r2=false;
        r3=false;

        leaderBoard.showTable(listView,table_name_Overall,getLayoutInflater());
        Log.i(TAG,"school code"+schoolCode);
        grade=Integer.toString(4);
        overall=view.findViewById(R.id.overall);
        schoolWise=view.findViewById(R.id.SchoolLeader);
        classwise=view.findViewById(R.id.classWise);
        overall.setOnClickListener(this);
        schoolWise.setOnClickListener(this);
        classwise.setOnClickListener(this);
        if(schoolCode.equals(getString(R.string.empty))){
                overall.setVisibility(View.GONE);
                schoolWise.setVisibility(View.GONE);
                classwise.setVisibility(View.GONE);
        }
       // listView.addHeaderView(header, null, false);
        collectionReference= FirebaseFirestore.getInstance().collection("leaderboard");
        return view;
    }
    public void loadPosition(){

    }




    @Override
    public void onPause() {
        Log.i(commonTag,TAG+ " onPause");
        if(r1){
        registration1.remove();
        r1=false;}
        if(r2){
            registration2.remove();
            r1=false;
        }
        if(r3){
            registration3.remove();
            r1=false;
        }

            super.onPause();
    }

    @Override
    public void onStart() {
        Log.i(commonTag,TAG+" on start");
   //   overallLeaderLoad();
        super.onStart();
    }
    public void overallLeaderLoad(){
        r1=true;
        Query query=collectionReference.orderBy("xcore", Query.Direction.DESCENDING).limit(10);
        registration1=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // loadList(queryDocumentSnapshots);
                loadLayout(queryDocumentSnapshots);
                Log.i(TAG,"on event");
            }
        });
    }
    public void SchoolLeaderLoad(){
        r2=true;
        Query query=collectionReference.whereEqualTo("school_code",schoolCode).orderBy("xcore", Query.Direction.DESCENDING).limit(10);
       registration2=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());
                }
                else {
                    loadLayout(queryDocumentSnapshots);
                }
            }
        });
    }
    public void ClassLeaderLoad(){
        r3=true;
        Log.i(TAG,"class_leaderboard");
        Query query=collectionReference.whereEqualTo("school_code",schoolCode).whereEqualTo("grade",grade).orderBy("xcore", Query.Direction.DESCENDING).limit(10);
        registration3=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());
                }
                else {
                    loadLayout(queryDocumentSnapshots);
                }
            }
        });
    }
    public void loadLayout(QuerySnapshot querySnapshot){
        int count=1;
        listView.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout view =(LinearLayout)inflater.inflate(R.layout.header_list_leaderboard, null);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams1.setMargins(30, 0, 30, 0);
        view.setLayoutParams(layoutParams1);
        listView.addView(view);
        for (QueryDocumentSnapshot doc : querySnapshot) {
            if (doc.get("name") != null) {

                LinearLayout vi =(LinearLayout)inflater.inflate(R.layout.activity_list_leaderboard, null);
                TextView rank=vi.findViewById(R.id.rank);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(30, 20, 30, 0);
                rank.setText(Integer.toString(count));
                TextView name=vi.findViewById(R.id.name);
                name.setText(doc.getString("name"));
                vi.setLayoutParams(layoutParams);
                TextView xcore=vi.findViewById(R.id.credit);
                xcore.setText(doc.getLong("xcore").toString());
                listView.addView(vi);
                count++;
            }
        }
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

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.overall:
               /* if(r2){
                    registration2.remove();
                    r2=false;
                }
                if(r3)
                {
                    registration3.remove();
                    r3=false;
                }

                overallLeaderLoad();*/
                leaderBoard.showPosition(leaderposition,positionOverall_TAG);
                overall.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.white_3dp));
                schoolWise.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.yellow_rect_rounded));
                classwise.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.yellow_rect_rounded));
               leaderBoard.showTable(listView,table_name_Overall,getLayoutInflater());
                break;
            case R.id.SchoolLeader:
               /* if(r1){
                    registration1.remove();
                    r1=false;
                }
                if(r3)
                {
                    registration3.remove();
                    r3=false;
                }
                SchoolLeaderLoad();*/
                leaderBoard.showPosition(leaderposition,positionSchool_TAG);
                overall.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.yellow_rect_rounded));
                schoolWise.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.white_3dp));
                classwise.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.yellow_rect_rounded));
                leaderBoard.showTable(listView,table_name_School,getLayoutInflater());
                break;
            case R.id.classWise:
              /*  if(r2){
                    registration2.remove();
                    r2=false;
                }
                if(r1)
                {
                    registration1.remove();
                    r1=false;
                }
                ClassLeaderLoad();*/
                leaderBoard.showPosition(leaderposition,positionClass_TAG);
                overall.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.yellow_rect_rounded));
                schoolWise.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.yellow_rect_rounded));
                classwise.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.white_3dp));
                leaderBoard.showTable(listView,table_name_Class,getLayoutInflater());

                break;
        }
    }
}
