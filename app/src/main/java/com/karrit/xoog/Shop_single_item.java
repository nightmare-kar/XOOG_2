package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Shop_single_item extends AppCompatActivity {
    ImageView imageView;
    TextView title;
    int position;
    Dialog dialogSports;
    TextView description;
    ArrayList<Address> addresses;
    TextView credits;
    CollectionReference collectionReference;
    TextView your_credits;
    boolean registered;
    int xcashItem;
private String TAG="shop_single_item";
    StorageReference storageReference;
    shared share;
    int xcash;
    int id;
Button button;
account_details accountDetails;

    @Override
    protected void onPause() {
        if(registered){
        unregisterReceiver(mReceiver);}
        Log.i(TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_single_item);
        Intent intent=getIntent();
        ImageView close=findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("shop");
        imageView=findViewById(R.id.item);
        title=findViewById(R.id.textView);
        share=new shared(this);
        description=findViewById(R.id.description);
        credits=findViewById(R.id.product_credit);
        your_credits=findViewById(R.id.credits);

        id=intent.getIntExtra("shop_id",0);
        Glide.with(this).load(storageReference.child(id + ".jpg")).into(imageView);
        description.setText(intent.getStringExtra("description"));
        title.setText(intent.getStringExtra("name"));
        credits.setText("CREDITS: "+intent.getIntExtra("credits",0));
        button=findViewById(R.id.button);
        xcashItem=intent.getIntExtra("credits",0);
        accountDetails=new account_details(this,share.getCurrent_kid());
        Log.i(TAG,"xcash "+accountDetails.getXcash());
        your_credits.setText(Integer.toString(accountDetails.getXcash()));
        xcash=accountDetails.getXcash();
collectionReference= FirebaseFirestore.getInstance().collection("orders");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xcash>=xcashItem){
               showAddressDialog();}else{
                      NoXcash(xcashItem-xcash);
                }
            }
        });
    }
    public void showAddressDialog(){
        final Dialog dialogSubs = new Dialog(this);
        dialogSubs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSubs.setCancelable(true);
        dialogSubs.setContentView(R.layout.address_dialog);
        Window window = dialogSubs.getWindow();
        final Button button=dialogSubs.findViewById(R.id.button);
registered=false;
        Log.i("main","pop_up_subs");
      sql_address sql_address=new sql_address(Shop_single_item.this);
      addresses=sql_address.getAddresses();
        AddressListAdapter adapter = new AddressListAdapter(this, R.layout.pop_up_address,addresses);
        final ListView listview=dialogSubs.findViewById(R.id.listView);
        listview.setAdapter(adapter);
        position=-10;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //view.setBackground(R.drawable.address_list_background);
               button.setText("Confirm Order");
               view.setSelected(true);
               position=i;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSubs.dismiss();
                if(position==-10){
                     //add Address
                    Intent intent=new Intent(Shop_single_item.this,Add_Address.class);
                    startActivity(intent);
                }else{
                    NetworkCheck networkCheck=new NetworkCheck();
                    if(networkCheck.internetConnectionAvailable(5000)) {
                        TransactFn(xcashItem);
                    }else{
                        LoadingDialog();
                        NoInternetConnection();
                        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        Shop_single_item.this.registerReceiver(Shop_single_item.this.mReceiver, filter);
                        registered=true;
                        //----------------------------------Reduce current credits-----------------------------------
                    }
                    //confirm order
                }

            }
        });

        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSubs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogSubs.show();
    }
    public void confirmOrder(){
        Log.i(TAG,"order Confirmed");
        Address address=addresses.get(position);
        HashMap<String,Object> map=new HashMap<>();
        map.put("address",address.getAddress_full());
        map.put("shop_id",id);
        map.put("kid_id",share.getCurrent_kid());
        map.put("Date of Ordering", Timestamp.now());
        map.put("status","order just received");
        collectionReference.add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.i(TAG,"on complete Order upload");
            }
        });
        Intent intent=new Intent(Shop_single_item.this,Order_confirmed.class);
        startActivity(intent);
        finish();
    }
    public void NoXcash(int xcash){
        Log.i(TAG,"remain "+xcash);
        final Dialog dialog = new Dialog(Shop_single_item.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.no_xcash);
        Window window = dialog.getWindow();

        TextView xcashText=dialog.findViewById(R.id.xcash);
        xcashText.setText(Integer.toString(xcash));
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialog.show();
    }
    public void TransactFn(final int credits){
        Log.i(TAG,"inside transaction");
        final DocumentReference documentReference= FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details");
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Log.i(TAG,"apply transaction");
                DocumentSnapshot snapshot = transaction.get(documentReference);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()

                    Long newXcash = snapshot.getLong("xcash") - credits;
                accountDetails.setXcash(newXcash.intValue());
                accountDetails.apply();
                Log.i(TAG,"new cash"+newXcash);
                    transaction.update(documentReference, "xcash", newXcash);

                // Success
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                confirmOrder();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                HashMap<String,Object> user_refer=new HashMap<>();

                user_refer.put("kid_id",share.getCurrent_kid());
                user_refer.put("credits",credits);
                user_refer.put("error_type","transaction failure update credits");
                db.collection("errors").add(user_refer).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.i(TAG,"on Complete of error upload");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,"on failure error upload");
                    }
                });
            }
        });
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
                    TransactFn(xcash);
                    dialogSports.dismiss();
                    Shop_single_item.this.unregisterReceiver(this);
                    registered=false;
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");
                }
            }
        }

    };
    public void NoInternetConnection(){

        dialogSports = new Dialog(Shop_single_item.this);
        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.no_internet_connection);
        Window window = dialogSports.getWindow();
        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSports.show();
    }
    public void LoadingDialog(){

        Dialog dialogLoad = new Dialog(Shop_single_item.this);
        dialogLoad.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoad.setCancelable(false);
        dialogLoad.setContentView(R.layout.uploading_dialog);
        Window window = dialogLoad.getWindow();
       TextView close=dialogLoad.findViewById(R.id.upload);
        close.setText("Confirming Order");

        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogLoad.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLoad.show();
    }



}
