package com.karrit.xoog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.TimeZone;

public class ShopFragment extends Fragment {
    private String TAG="shop Fragment";
    private String commonTag="common";
    CollectionReference collectionReference;
    ArrayList<shopItems> lstBook;
    ListenerRegistration registration;
    StorageReference storageReference;
    RecyclerView myrv;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shop_fragment,container,false);

storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("shop");
        lstBook = new ArrayList<>();
        shared share=new shared(getActivity());
        account_details accountDetails=new account_details(getActivity(),share.getCurrent_kid());
        TextView xcashText=view.findViewById(R.id.xcashFragment);
        xcashText.setText(Integer.toString(accountDetails.getXcash()));

        Log.i(TAG,"xcash "+accountDetails.getXcash());

        myrv = (RecyclerView)view.findViewById(R.id.recyclerView);





        return view;
    }

    @Override
    public void onStart() {
        Log.i(commonTag,TAG+" on start");

        Log.i(TAG,"time_zone"+TimeZone.getTimeZone("Asia/Dubai").getRawOffset());
        Log.i(TAG,"time_zone"+TimeZone.getTimeZone("Asia/Calcutta").getRawOffset());
        Log.i(TAG,"time_zone"+TimeZone.getDefault().getRawOffset());

        if((TimeZone.getDefault().getRawOffset())==(TimeZone.getTimeZone("Asia/Dubai").getRawOffset())){
            Log.i(TAG,"utc");
            collectionReference=FirebaseFirestore.getInstance().collection("shop_UAE");
        }else {
            Log.i(TAG,"pther");
            collectionReference = FirebaseFirestore.getInstance().collection("shop");
        }
        registration=collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                Log.i(TAG,"on event");
                loadData(querySnapshot);
            }
        });
        super.onStart();
    }
    public void loadData(QuerySnapshot querySnapshot){

        lstBook = new ArrayList<>();
        if(lstBook.size()!=0){
            lstBook.clear();
        }
        for (QueryDocumentSnapshot doc : querySnapshot) {
            if (doc.get("name") != null) {
                shopItems shopItems=new shopItems();
                shopItems.setCredits(doc.getLong("credits").intValue());
                String data=doc.getString("name");
                shopItems.setTitle(data.toUpperCase());
                shopItems.setDescription(doc.getString("description"));
               shopItems.setId(doc.getLong("shop_id").intValue());

             lstBook.add(shopItems);


            }
        }
        RecyclerAdapter myAdapter = new RecyclerAdapter(getContext(),lstBook);
        myrv.setLayoutManager(new GridLayoutManager(getContext(),2));
        myrv.setAdapter(myAdapter);
    }

    @Override
    public void onPause() {
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

}
