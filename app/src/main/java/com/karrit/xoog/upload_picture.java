package com.karrit.xoog;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class upload_picture extends Fragment {
    public static final String TAG="upload_photo";
    ViewFlipper imageFrame;
    Boolean[] setImage;
    TextView insScramble;
    String[] scramble_array;
    TextView scrambleText;
    Random rand;
    private ImageView btnCapture;
    Button confirm;
upload_class upload_class;
    private ImageView imgCapture;
    StorageReference storageReference;
    CollectionReference db;
    CardView slideShowBtn;
    sql_rubik sql_rubik;
    int current_index;
    LinearLayout dots;
    View view;
    int url_count=0;
    int level,task;
    private ImageView[] dot_image;

    private int count;
task_details task_details;
shared share;
String kid_id,user_id;
    private static final int Image_Capture_Code = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.upload_picture_layout, container, false);
        imageFrame = view.findViewById(R.id.imageFrames);

        ImageView close=view.findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showBack_Dialog();
            }
        });
        //----------------------------------get class from sql and set----------------------------------
        share=new shared(getActivity());
        rand = new Random();
        kid_id=share.getCurrent_kid();
        user_id=share.getUser_id();
        task_details=new task_details(getActivity(),kid_id,getActivity().getString(R.string.rubik_type));
        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
         sql_rubik= new sql_rubik(getActivity(),kid_id);
         upload_class=sql_rubik.get_upload_photo(level,task);
         count=upload_class.getNumber_pictures();
         TextView topic=view.findViewById(R.id.topic);
         topic.setText(upload_class.getDescription());
         scramble_array=getActivity().getResources().getStringArray(R.array.scramble_array);

       scrambleText=view.findViewById(R.id.scramble);



        //-----------------------------------------------------------------------------------------------
        scrambleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upload_class.getShow_scramble()){
                    int rand_int1 = rand.nextInt(scramble_array.length);
                    scrambleText.setText(scramble_array[rand_int1]);
                }
            }
        });
        if(upload_class.getShow_scramble()){
            int rand_int1 = rand.nextInt(scramble_array.length);
            scrambleText.setText(scramble_array[rand_int1]);

        }else{
            insScramble.setVisibility(View.INVISIBLE);
            scrambleText.setVisibility(View.INVISIBLE);
            scrambleText.setOnClickListener(null);
        }
        setImage=new Boolean[count];
        Arrays.fill(setImage, false);
        confirm=view.findViewById(R.id.confirm);
        dots=view.findViewById(R.id.linearDots);
        dot_image=new ImageView[count];
        addFlipperImages(imageFrame,count);

        storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("users").child(share.getCurrent_kid()).child(getActivity().getString(R.string.rubik_type)).child(level+"_"+task);

        db= FirebaseFirestore.getInstance().collection("users").document(user_id).collection(kid_id+"-upload_rubik");
        //------------add dots---------------------------------------------------------------------------------
        for(int i=0;i<count;i++){
            dot_image[i]=new ImageView(getActivity());
            if(i==0){
                Log.i("active","dots");
                dot_image[i].setImageResource(R.drawable.active_dots);
            }else{
                Log.i("non active","dots");
                dot_image[i].setImageResource(R.drawable.non_active_dots);
            }
            int dp=(int)getResources().getDimension(R.dimen.value_dots);
            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(dp,dp);
            params.setMargins(8,0,8,0);
            dots.addView(dot_image[i],params);
        }


       // btnCapture = view.findViewById(R.id.upload);
      //  imgCapture = view.findViewById(R.id.image);
       /* btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
            }
        });*/
        slideShowBtn =view.findViewById(R.id.cardView);
        //slideShowBtn.setOnTouchListener(gestureListener);
        slideShowBtn.setOnTouchListener(new slideshow(getActivity()){
            @Override
            public void upload() {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
                current_index=imageFrame.indexOfChild(imageFrame.getCurrentView());
            }

            public void onSwipeRight() {

                int i= imageFrame.indexOfChild(imageFrame.getCurrentView());
                if(i>0) {
                    for(int j=0;j<count;j++){
                        dot_image[j].setImageResource(R.drawable.non_active_dots);
                    }
                    dot_image[i-1].setImageResource(R.drawable.active_dots);
                    imageFrame.setInAnimation(inFromLeftAnimation());
                    imageFrame.setOutAnimation(outToRightAnimation());

                    imageFrame.showPrevious();
                }

            }
            public void onSwipeLeft() {


                int i= imageFrame.indexOfChild(imageFrame.getCurrentView());
                if(i<(count-1)) {
                    for(int j=0;j<count;j++){
                        dot_image[j].setImageResource(R.drawable.non_active_dots);
                    }
                    dot_image[i+1].setImageResource(R.drawable.active_dots);

                    imageFrame.setInAnimation(inFromRightAnimation());
                    imageFrame.setOutAnimation(outToLeftAnimation());


                    imageFrame.showNext();

                }
                }
            }
        );
        //------------------------confirm upload-------------------------------------

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b=false;
                 for(int i=0;i<count;i++){
                     if(!setImage[i]){
                         b=true;
                     }
                }
                 if(b){
                     Toast.makeText(getActivity(),"Upload all photos",Toast.LENGTH_SHORT).show();
                     Log.i("upload","all photos");
                 }else {
                   //  -----------------intent to next activity-------------------------


                    //------------------------------------------------------------------
                     for(int i=0;i<count;i++){
                         ImageView imageView = (ImageView)imageFrame.getChildAt(i);
                         imageView.setDrawingCacheEnabled(true);
                         imageView.buildDrawingCache();
                         Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                         ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                         byte[] data = baos.toByteArray();
                         final String imageName= UUID.randomUUID().toString() + ".jpg";
                         UploadTask uploadTask = storageReference.child(imageName).putBytes(data);
                         uploadTask.addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception exception) {
                                 // Handle unsuccessful uploads
                                 Log.i("upload","failure");
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

                     }
                     Intent intent=new Intent(getActivity(),Task_finish.class);
                     startActivity(intent);
                     getActivity().finish();
                 }
            }
        });


        return view;
    }


    public void SendLink(String url){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("link", url);
        hashMap.put("level",level);
        hashMap.put("course_type","rubik_type");
        hashMap.put("task",task);
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
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                Log.i("current","index");
                int i=imageFrame.indexOfChild(imageFrame.getCurrentView());
                setImage[i]=true;
                ImageView imageView = (ImageView) imageFrame.getCurrentView();
                imageView.setImageBitmap(bp);
                //ImageView im=new ImageView(getActivity());
                //im.setImageBitmap(bp);
              //imageFrame.addView(im,current_index);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addFlipperImages(ViewFlipper flipper, int n) {
        int imageCount = n;

        for (int count = 0; count < imageCount; count++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.ic_photo_camera_white);
            int dp=(int)getResources().getDimension(R.dimen.camera);
imageView.setMaxHeight(dp);
imageView.setMaxWidth(dp);
            //ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
            //params.horizontalBias=0.5f;
            Log.i("count", Integer.toString(count));
            flipper.addView(imageView);
        }
    }
    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.2f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
    public void showBack_Dialog(){
        final Dialog dialogSports = new Dialog(getActivity());
        dialogSports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSports.setCancelable(true);
        dialogSports.setContentView(R.layout.dialog_back);
        Window window = dialogSports.getWindow();

        Log.i("main","pop_up_rubik");
        TextView leave=dialogSports.findViewById(R.id.leave);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSports.dismiss();
                getActivity().finish();
                Log.i(TAG,"leave");
            }
        });
        TextView stay=dialogSports.findViewById(R.id.stay);
        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSports.dismiss();
                Log.i(TAG,"stay");
            }
        });

        window.setGravity(Gravity.CENTER);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogSports.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialogSports.show();
    }


}








