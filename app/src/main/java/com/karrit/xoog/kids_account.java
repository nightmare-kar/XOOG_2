package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class kids_account extends AppCompatActivity {
    private String TAG="kids_account";
    ConstraintLayout cons;
    shared share;
    Uri imageUri;
    String[] gender_string;
    String[] grade_string;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int REQUEST_CAMERA=100;
    private int SELECT_FILE=101;
    CircleImageView profile;
    DocumentReference db;
    private static final int PERMISSION_CODE=1000;
    EditText email,name,schoolCode;
    TextView calendar_text;
    TextView phone;
    int selected_gender;
    Spinner gradeSpinner,genderSpinner;
    account_details accountDetails;
    boolean changedProf=false;
    Button edit_profile;

    boolean changeEmail=false;
    int posGrade,posGender;
    String selected_date;
    int selected_grade;
    boolean changeName=false;

    boolean changeGender=false;
    boolean changeGrade=false;
    boolean changeSchoolCode=false;
    boolean changeDOB=false;
    NetworkCheck networkCheck;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_account);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        share=new shared(this);
        back=findViewById(R.id.back);
        networkCheck=new NetworkCheck();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.i(TAG,"height "+height+"weight "+width);
        cons=findViewById(R.id.cons);
        cons.setMinHeight(height);
        ImageView done;
        gender_string=getResources().getStringArray(R.array.gender);
        grade_string=getResources().getStringArray(R.array.grade);
        db= FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details");
        phone=findViewById(R.id.phone_edit);
        email=findViewById(R.id.email_edit);
        name=findViewById(R.id.Name);
        calendar_text=findViewById(R.id.age);
        schoolCode=findViewById(R.id.schoolCode_edit);
        gradeSpinner=findViewById(R.id.grade_spinner);
        genderSpinner=findViewById(R.id.gender_spinner);
        done=findViewById(R.id.done);
        profile=findViewById(R.id.profile_image);
        edit_profile=findViewById(R.id.edit_profile);
        //

        accountDetails=new account_details(this,share.getCurrent_kid());
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        // //--------------set Prof Image-----------------
        if(!accountDetails.getProfImageurl().equals(getString(R.string.empty))) {
            String imageUrl = accountDetails.getProfImageurl();
            Uri uri = Uri.parse(imageUrl);
            Log.i(TAG, imageUrl);
            profile.setImageURI(uri);
            try {

                int rotate = getCameraPhotoOrientation(this, uri, imageUrl);

                profile.setRotation(rotate);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            profile.setImageResource(R.color.black);
        }
                phone.setText(accountDetails.getPhone_number());
                email.setText(accountDetails.getEmail());
                calendar_text.setText(accountDetails.getDOB());
                schoolCode.setText(accountDetails.getSchoolId());
                name.setText(accountDetails.getKid_name());
                String account_gender=accountDetails.getGender();
        Log.i(TAG,"account grade "+account_gender);
                posGender=0;
                for(int i=0;i<gender_string.length;i++){
                    if(account_gender.equals(gender_string[i])){
                        posGender=i;
                    }
                }

                changeGender=false;
               String account_grade=accountDetails.getGrade();
               Log.i(TAG,"account grade "+account_grade);
                posGrade=0;
        for(int i=0;i<grade_string.length;i++){
            Log.i(TAG,grade_string[i]+i);
            if(grade_string[i].equals(account_grade)){
                Log.i(TAG,"inside"+grade_string[i]+i);
                posGrade=i;
            }
        }
        Log.i(TAG,"pos grade"+posGrade);
        Log.i(TAG,grade_string.toString());

        changeGrade=false;
         //-------------------------------------calendar for DOB-----------------------------------------
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                selected_date=date;
                changeDOB=true;
                calendar_text.setText(date);
            }
        };
        calendar_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        //----------------------spinner for gender---------------------------------------------
        selected_gender=10;
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(kids_account.this, R.layout.spinner_text, getResources().getStringArray(R.array.gender) );
        langAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        genderSpinner.setAdapter(langAdapter);
        genderSpinner.setSelection(posGender);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        selected_gender=0;
                        break;
                    case 1:

                        selected_gender=1;
                        break;
                    case 2:
                        selected_gender=2;
                        break;
                }
                changeGender=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
        //--------------------------grade String spinner----------------------------------------------
        selected_grade=100;
        final ArrayAdapter<CharSequence> gradeAdapter = new ArrayAdapter<CharSequence>(kids_account.this, R.layout.spinner_text, getResources().getStringArray(R.array.grade) );
        langAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setSelection(posGrade);


        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_grade=position;
                changeGrade = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
        //adding text watcher to email------------------------------

        TextWatcher emailWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
              changeEmail=true;
            }
        };
        email.addTextChangedListener(emailWatcher);
        //adding text watcher to email------------------------------

        //adding text watcher to email------------------------------

        TextWatcher nameWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeName=true;
            }
        };
        name.addTextChangedListener(nameWatcher);
        //adding text watcher to email------------------------------

        TextWatcher schoolWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeSchoolCode=true;
            }
        };
        schoolCode.addTextChangedListener(schoolWatcher);

        //--------------------------------OnClick Listener Done-------------------------------------
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(networkCheck.internetConnectionAvailable(5000)){
                    if(changeName){
                        if(name.getText().toString().isEmpty()){
                            Toast.makeText(kids_account.this,"Enter name",Toast.LENGTH_SHORT).show();
                        }else {
                            changeName=false;
                            db.update("kid_name", name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i(TAG,"on complete name update");
                                }
                            });
                            accountDetails.setKid_name(name.getText().toString());
                            accountDetails.apply();
                            FirebaseFirestore.getInstance().collection("leaderboard").document(share.getCurrent_kid()).update("name",name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i(TAG,"leaderboard uploaded");
                                }
                            });
                        }
                    }
                    if(changeEmail){
                        if(email.getText().toString().isEmpty()){
                            Toast.makeText(kids_account.this,"Enter Email",Toast.LENGTH_SHORT).show();
                        }else {
                            changeEmail=false;
                            db.update("email",email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Log.i(TAG, "on complete email update");
                                        accountDetails.setEmail(email.getText().toString());
                                        accountDetails.apply();
                                    }
                                }
                            });

                        }
                    }
                    if(changeSchoolCode){
                        changeSchoolCode=false;
                        check_Institute(schoolCode.getText().toString());
                    }
                    if(changeGender){
                        if(selected_gender!=posGender){
                            changeGender=false;
                            posGender=selected_gender;
                            db.update("gender",gender_string[selected_gender]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Log.i(TAG, "on complete gender update");
                                        accountDetails.setGender(gender_string[selected_gender]);
                                        accountDetails.apply();
                                    }
                                }
                            });
                        }

                    }
                    if(changeGrade){
                        if(selected_grade!=posGrade){
                            changeGrade=false;
                            posGrade=selected_grade;
                            db.update("grade",grade_string[selected_grade]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Log.i(TAG, "on complete grade update");
                                        accountDetails.setGrade(grade_string[selected_grade]);
                                        accountDetails.apply();
                                    }
                                }
                            });
                            FirebaseFirestore.getInstance().collection("leaderboard").document(share.getCurrent_kid()).update("grade",grade_string[selected_grade]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i(TAG,"leaderboard uploaded");
                                }
                            });
                        }

                    }
                    if(changeDOB){
                        changeDOB=false;
                        db.update("DOB",calendar_text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Log.i(TAG, "on complete DOB update");
                                    accountDetails.setDOB(calendar_text.getText().toString());
                                    accountDetails.apply();
                                }
                            }
                        });
                    }
                    Toast.makeText(kids_account.this,"Saved",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(kids_account.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //------------------------on Back Pressed----------------
       back.setOnClickListener(
               new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               backPressed();
           }
       });

    }
    public void backPressed(){
        if(changeName||(posGrade!=selected_grade)||(posGender!=selected_gender)||changeDOB||changeEmail||changeSchoolCode){
            Toast.makeText(kids_account.this,"Press Done button to save Changes",Toast.LENGTH_SHORT).show();
        }else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
       backPressed();
    }

    public void check_Institute(final String Schoolid){
        if(Schoolid.isEmpty()){
            Log.i(TAG,"School id is empty");
            accountDetails.setSchoolId(getString(R.string.empty));
            accountDetails.apply();
            //go to same login
        }else{
            FirebaseFirestore instance=FirebaseFirestore.getInstance();
            Log.i(TAG,"refeera"+Schoolid);
            instance.collection("institutes").document("school").collection(Schoolid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().isEmpty()){
                            Toast.makeText(kids_account.this,"Wrong School Code",Toast.LENGTH_SHORT).show();
                        }else{
                            Log.i(TAG,"succesful"+Schoolid);
                            accountDetails.setSchoolId(Schoolid);
                            accountDetails.apply();
                           addSchool(Schoolid);
                        }
                    }else {
                        Toast.makeText(kids_account.this,"Try again Later",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }
    public void addSchool(String schoolId){
        HashMap<String,Object> map_school=new HashMap<>();
        map_school.put("kid_id",share.getCurrent_kid());
        map_school.put("DOT", Timestamp.now());
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users").document(share.getUser_id()).collection(share.getCurrent_kid()).document("account_details").update("schoolId",schoolId,"referredBy",getString(R.string.empty)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.i(TAG, "task upload successful");
                }else {
                    Log.i(TAG,"task not success");
                }
            }
        });
        db.collection("institutes").document("school").collection(schoolId).add(map_school).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.i(TAG,"on complete");
            }
        });
        db.collection("leaderboard").document(share.getCurrent_kid()).update("school_code",schoolId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG,"leaderboard uploaded");
            }
        });
    }
    public void askPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)||(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)||(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)){
                String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
                //show pop_up
                requestPermissions(permission,PERMISSION_CODE);
            }else{
                //granted
                startCamera();

            }
        }else{
            //granted
             startCamera();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permission","granted");
                    startCamera();
                   //granted
                }
            }else{
                Toast.makeText(kids_account.this,"permission denied",Toast.LENGTH_SHORT);
            }
        }
    }
    private void SelectImage(){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(kids_account.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                   askPermission();


                } else if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    //startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();


    }
    public int getCameraPhotoOrientation(Context context, Uri imageUri,
                                         String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
    public void startCamera(){
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cInt, REQUEST_CAMERA);
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode==REQUEST_CAMERA){
                try {
                    if (requestCode == REQUEST_CAMERA) {
                        if (resultCode == RESULT_OK) {
                            Bitmap bp = (Bitmap) data.getExtras().get("data");

                            profile.setImageBitmap(bp);
                            Uri tempUri = getImageUri(getApplicationContext(), bp);

                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            File finalFile = new File(getRealPathFromURI(tempUri));
                            accountDetails.setProfImageurl(finalFile.toString());
                            accountDetails.apply();
                            //ImageView im=new ImageView(getActivity());
                            //im.setImageBitmap(bp);
                            //imageFrame.addView(im,current_index);
                        } else if (resultCode == RESULT_CANCELED) {
                            Toast.makeText(kids_account.this, "Cancelled", Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if(requestCode==SELECT_FILE){
                changedProf=true;
                Uri selectedImageUri = data.getData();
                String imageURI=selectedImageUri.toString();
                accountDetails.setProfImageurl(imageURI);
                accountDetails.apply();
                profile.setImageURI(selectedImageUri);
            }

        }
    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                mDateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
}
