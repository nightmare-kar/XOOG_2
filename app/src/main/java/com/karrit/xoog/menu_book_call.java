package com.karrit.xoog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class menu_book_call extends AppCompatActivity {
    shared share;
    private String TAG="menu_reason";
    NetworkCheck networkCheck;
    Dialog dialogSports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_book_call);
        ImageView close=findViewById(R.id.back);
        share=new shared(this);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final EditText text=findViewById(R.id.reason);
        networkCheck=new NetworkCheck();
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(networkCheck.internetConnectionAvailable(5000)){
                String edit=text.getText().toString();
                if(edit.isEmpty()){
                    Toast.makeText(menu_book_call.this,"Enter Something",Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, Object> reason_map = new HashMap<>();
                    reason_map.put("Request", edit);
                    reason_map.put("timeOfRequest", Timestamp.now());
                    reason_map.put("User_id", share.getCurrent_kid());
                    reason_map.put("statue", "new");
                    FirebaseFirestore.getInstance().collection("customer_support").add(reason_map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "is success");
                            } else {
                                Log.i(TAG, "no success");
                            }
                        }
                    });
                    showDialogRequestPlaced();
                }
                }else {
                    Toast.makeText(menu_book_call.this,"Poor Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void showDialogRequestPlaced(){
        dialogSports = new Dialog(menu_book_call.this);
        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.dialog_request_booked);
        Window window = dialogSports.getWindow();
        ImageView close=dialogSports.findViewById(R.id.close);
        Log.i("main","pop_up_rubik");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSports.dismiss();
            }
        });
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSports.show();
    }
}
