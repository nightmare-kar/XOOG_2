package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Add_Address extends AppCompatActivity {
EditText phone,name,address_1,address_2,city,state,pincode,landmark,country,title;
Button button;
CollectionReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__address);
        phone=findViewById(R.id.phone);
        name=findViewById(R.id.name);
        address_1=findViewById(R.id.address_1);
        address_2=findViewById(R.id.address_2);
        city= findViewById(R.id.city);
        state=findViewById(R.id.state);
        pincode=findViewById(R.id.pincode);
        landmark=findViewById(R.id.landmark);
        country=findViewById(R.id.country);
        title=findViewById(R.id.title);
        shared share =new shared(this);
        db=FirebaseFirestore.getInstance().collection("users").document(share.getUser_id()).collection(share.getUser_id()+"-address");


button=findViewById(R.id.button);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String nameText=name.getText().toString();
        String phone_text=phone.getText().toString();
        String add_1=address_1.getText().toString();
        String add_2=address_2.getText().toString();
        String pincodeText=pincode.getText().toString();
        String cityText=city.getText().toString();

        String stateText=state.getText().toString();
        String countryText=country.getText().toString();
        String landmarkText=landmark.getText().toString();
        String titleText=title.getText().toString();



        if(nameText.isEmpty()||phone_text.isEmpty()||add_1.isEmpty()||add_2.isEmpty()||pincodeText.isEmpty()||cityText.isEmpty()||stateText.isEmpty()||countryText.isEmpty()||landmarkText.isEmpty()||titleText.isEmpty()){
            Toast.makeText(Add_Address.this,"Enter All Details",Toast.LENGTH_SHORT).show();
        }else {
            String full=nameText+","+add_1+"\n"+add_2+landmarkText+"\n"+cityText+","+stateText+"\n"+countryText+"-"+pincodeText+"\n"+"Ph:"+phone_text;
        Address address=new Address(titleText,full);
        sql_address sql=new sql_address(Add_Address.this);
            HashMap<String, Object> map=new HashMap<>();
            map.put("title",titleText);
            map.put("full",full);
            db.add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Log.i("Add address","on complete address upload");
                }
            });
        sql.addAdress(address);

        finish();
        }
    }
});
    }


}
