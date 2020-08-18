package com.karrit.xoog;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class upload_video extends Fragment {
    public static final String EXTRA_INFO = "default";
    private String TAG="video_layout";
    ViewFlipper imageFrame;
    Boolean[] setImage;
    private ImageView btnCapture;
    Button confirm;
  TextView scrambleText;
    private ImageView imgCapture;
    StorageReference storageReference;
    CollectionReference db;
    TextView insScramble;
    Random rand;
    CardView slideShowBtn;
    Uri[] uri;
    int current_index;
    String[] scramble_array;
    LinearLayout dots;
    View view;
    int url_count=0;
    int level,task;
    private ImageView[] dot_image;
    MediaController[] mediaController;
    private int count;
    task_details task_details;
    upload_class upload_class;
    sql_rubik sql_rubik;
    shared share;
    String kid_id,user_id;

    private static final int Video_Capture_Code = 300;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.upload_video_layout, container, false);
        imageFrame = view.findViewById(R.id.imageFrames);
        //---------------------get class and set values-----------------------
        share=new shared(getActivity());
        task_details=new task_details(getActivity(),share.getCurrent_kid(),getActivity().getString(R.string.rubik_type));
        kid_id=share.getCurrent_kid();
        user_id=share.getUser_id();
        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
        task_details=new task_details(getActivity(),kid_id,getActivity().getString(R.string.rubik_type));
        sql_rubik= new sql_rubik(getActivity(),kid_id);
        upload_class=sql_rubik.get_upload_video(level,task);
        count=upload_class.getNumber_pictures();
        scrambleText=view.findViewById(R.id.scramble);
        TextView topic=view.findViewById(R.id.topic);
        topic.setText(upload_class.getDescription());
        scramble_array=getActivity().getResources().getStringArray(R.array.scramble_array);
        rand = new Random();
        insScramble=view.findViewById(R.id.insScramble);
        ImageView close=view.findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             showBack_Dialog();
            }
        });
        //-----------------------------------------------------------------
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




        Log.i(TAG,"working");
        uri=new Uri[count];
        setImage=new Boolean[count];
        Arrays.fill(setImage, false);
        confirm=view.findViewById(R.id.confirm);
        dots=view.findViewById(R.id.linearDots);
        dot_image=new ImageView[count];
        mediaController=new MediaController[count];
        for(int i =0;i<count;i++){
            mediaController[i]=new MediaController(getActivity());
        }

        addFlipperImages(imageFrame,count);



        storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("users").child(share.getCurrent_kid()).child(getActivity().getString(R.string.rubik_type)).child(level+"_"+task);
        db= FirebaseFirestore.getInstance().collection("users").document(user_id).collection(kid_id+"-upload_rubik");
        //------------add dots-----------------
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
        slideShowBtn.setOnTouchListener(new slideshow(getActivity()) {
                                            @Override
                                            public void upload() {
                                                Log.i(TAG,"click upload");
                                                Intent cInt = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                                startActivityForResult(cInt, Video_Capture_Code);
                                                current_index = imageFrame.indexOfChild(imageFrame.getCurrentView());
                                                Log.i(TAG, "cuurent index " + current_index);
                                            }

                                            public void onSwipeRight() {
                                                Log.i(TAG,"click right");

                                                int i = imageFrame.indexOfChild(imageFrame.getCurrentView());
                                                Log.i(TAG, "left+" + (i - 1));
                                                if (i > 0) {
                                                    for (int j = 0; j < count; j++) {
                                                        dot_image[j].setImageResource(R.drawable.non_active_dots);
                                                    }
                                                    dot_image[i - 1].setImageResource(R.drawable.active_dots);
                                                    imageFrame.setInAnimation(inFromLeftAnimation());
                                                    imageFrame.setOutAnimation(outToRightAnimation());
                                                    if (setImage[i - 1]) {
                                                        Log.i("set image", "if");
                                                        VideoView videoView = (VideoView) imageFrame.getChildAt(i - 1);
                                                        videoView.seekTo(1);

                                                    }
                                                    imageFrame.showPrevious();

                                                }

                                            }

                                            public void onSwipeLeft() {

                                                Log.i(TAG,"click left");
                                                int i = imageFrame.indexOfChild(imageFrame.getCurrentView());
                                                if (i < (count - 1)) {
                                                    for (int j = 0; j < count; j++) {
                                                        dot_image[j].setImageResource(R.drawable.non_active_dots);
                                                    }
                                                    Log.i(TAG, "right+" + (i + 1));
                                                    dot_image[i + 1].setImageResource(R.drawable.active_dots);

                                                    //  int i= imageFrame.indexOfChild(imageFrame.getCurrentView());

                                                    imageFrame.setInAnimation(inFromRightAnimation());
                                                    imageFrame.setOutAnimation(outToLeftAnimation());
                                                    if (setImage[i + 1]) {
                                                        Log.i("set image", "if");
                                                        VideoView videoView = (VideoView) imageFrame.getChildAt(i + 1);
                                                        videoView.seekTo(1);
                                                    }
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
                    /*--------------------------send activity to finish--------------------



                     ------------------------------------------------------------------*/
                    for(int i=0;i<count;i++){
                        VideoView videoView = (VideoView)imageFrame.getChildAt(i);


                        final String imageName= UUID.randomUUID().toString() + ".mp4";
                        UploadTask uploadTask = storageReference.child(imageName).putFile(uri[i]);
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
                                        Log.i(TAG,"video uploaded on success");
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
        hashMap.put("task",task);
        hashMap.put("course_type","rubik_type");
        hashMap.put("status","new");
        hashMap.put("Timestamp", Timestamp.now());
        db.add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                Log.i(TAG,"url uploaded");
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
        Log.i(TAG,"inside on activity");


        if (requestCode == Video_Capture_Code) {
            Log.i(TAG,"inside on activity");
            if (resultCode == RESULT_OK) {
                Log.i(TAG,"result ok");
                Uri videoUri = data.getData();
                int i = imageFrame.indexOfChild(imageFrame.getCurrentView());
                Log.i(TAG,"current index"+i);
                imageFrame.removeViewAt(i);
                VideoView videoView=new VideoView(getActivity());
                videoView.setMediaController(mediaController[i]);
                mediaController[i].setAnchorView(videoView);
                FrameLayout.LayoutParams lay=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                lay.gravity=Gravity.CENTER;
                videoView.setLayoutParams(lay);
                videoView.setVideoURI(videoUri);
                Log.i(TAG,videoUri.toString());
                uri[i]=videoUri;
                videoView.seekTo(1);
                imageFrame.addView(videoView,i);
                setImage[i]=true;
                imageFrame.setDisplayedChild(i);
                Log.i(TAG,"image count"+imageFrame.getChildCount());

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


