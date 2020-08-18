package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class pose_day extends AppCompatActivity {
    ImageView image;
    private static final int Image_Capture_Code = 101;
    Button confirm;
    Dialog dialogSports;
    shared share;
    StorageReference storageReference;
    task_details taskDetails;
    boolean clicked;
    private String TAG="pose_day";
    int level,task;
    boolean registered;
    CollectionReference db;
    NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_day);
        image=findViewById(R.id.image);
        clicked=false;
        networkCheck=new NetworkCheck();
        registered=false;
        confirm=findViewById(R.id.confirm);
        ImageView close=findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        share=new shared(this);
        taskDetails=new task_details(this,share.getCurrent_kid(),share.getCurrent_course_type());
        level=taskDetails.getCurrent_level();
        task=taskDetails.getCurrent_task();



        storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("users").child(share.getCurrent_kid()).child(getString(R.string.sport_type)).child(level+"_"+task);
        db= FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()+"-uploads");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(networkCheck.internetConnectionAvailable(5000)) {
                       UploadPhoto();
                    }else {
                        NoInternetConnection();
                        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        pose_day.this.registerReceiver(pose_day.this.mReceiver, filter);
                        registered=true;
                    }



            }
        });
    }
    public void UploadPhoto(){
        if(clicked){
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final String imageName = UUID.randomUUID().toString() + ".jpg";
        UploadTask uploadTask = storageReference.child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("upload", "failure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String url = String.valueOf(uri);
                        SendLink(url);
                    }
                });
            }
        });
        Intent intent=new Intent(pose_day.this,Task_finish.class);
        intent.putExtra("course_type",share.getCurrent_kid());
        startActivity(intent);
        }else{
            Toast.makeText(pose_day.this,"Take a picture",Toast.LENGTH_SHORT).show();
        }
    }
    public void SendLink(String url){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("link", url);
        hashMap.put("level",level);
        hashMap.put("task",task);
        hashMap.put("course_type",getString(R.string.sport_type));
        hashMap.put("status","new");
        db.add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                Log.i("uploads","url uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("uploads","upload Failed");
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                Log.i("current", "index");

                clicked=true;
                image.setImageBitmap(bp);
                //ImageView im=new ImageView(getActivity());
                //im.setImageBitmap(bp);
                //imageFrame.addView(im,current_index);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(pose_day.this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void NoInternetConnection(){

        dialogSports = new Dialog(pose_day.this);

        dialogSports.setCancelable(false);
        dialogSports.setContentView(R.layout.no_internet_connection);
        Window window = dialogSports.getWindow();
        Log.i("main","pop_up_rubik");
        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSports.show();
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
                    UploadPhoto();
                    dialogSports.dismiss();
                    pose_day.this.unregisterReceiver(this);
                    registered=false;
                    // Resume handling requests.
                    Log.i(TAG,"broadcast received");
                }
            }
        }

    };

    @Override
    protected void onPause() {
        if(registered){
            pose_day.this.unregisterReceiver(mReceiver);
        }
        super.onPause();
    }
}
