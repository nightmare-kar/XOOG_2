package com.karrit.xoog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class one_one extends Fragment {
    task_details task_details;
   one_one_class one_one_class;
    sql_rubik sql_rubik;
    shared share;
    String kid_id,user_id;
    int level,task;
    private String TAG="one_one";
    TextView change;
    View view;
    ClipboardManager clipboard;
    String url;
    String date,time;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.one_one_layout, container, false);

        TextView textView=view.findViewById(R.id.text);
        size(textView,38,58,"Your mentor will call you for the live\nInteractice Session\n\nOn",2f);
        TextView text_link = view.findViewById(R.id.link);

         //----------------------------------get class from sql and set----------------------------------
        change=view.findViewById(R.id.change);
        ImageView close=view.findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        share=new shared(getActivity());
              kid_id=share.getCurrent_kid();
              user_id=share.getUser_id();
              task_details=new task_details(getActivity(),kid_id,getString(R.string.rubik_type));
        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
        Log.i(TAG,"curent level"+level);
        Log.i(TAG,"current task"+task);


              sql_rubik= new sql_rubik(getActivity(),kid_id);
              one_one_class= sql_rubik.get_one_one(level,task);

              Log.i(TAG,"one_one "+one_one_class.getDesciption());
              Log.i(TAG,"one_one"+one_one_class.getTime());

            date=task_details.getOne_one_Date(level,task);
           time=task_details.getOne_one_Time(level,task);
           url=task_details.getOne_one_link(level,task);
           Log.i(TAG,"url "+url);
            TextView date_text=view.findViewById(R.id.date);
            TextView time_text=view.findViewById(R.id.time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        SimpleDateFormat dateFormat_1 = new SimpleDateFormat("dd/MM");
        String[] parts = time.split("\\-");
        String end_time=parts[1];
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        try {
            Calendar d1=Calendar.getInstance();

            Date d = dateFormat.parse(date);
            d1.setTime(d);
            Calendar dnow=Calendar.getInstance();
            if (dnow.get(Calendar.DATE)== d1.get(Calendar.DATE)) {
                Log.i(TAG, "now equal");
                try {
                    Date dtime = timeFormat.parse(end_time);
                    Calendar timeC = Calendar.getInstance();
                    timeC.setTime(dtime);
                    if (timeC.get(Calendar.HOUR) < dnow.get(Calendar.HOUR)) {
                        //go to finsh
                        goToFinish();
                        Log.i(TAG, "dtime is bigger");
                    } else if (timeC.get(Calendar.HOUR) == dnow.get(Calendar.HOUR)) {
                        if(timeC.get(Calendar.MINUTE)<=dnow.get(Calendar.MINUTE)){
                            //go ot finish
                            goToFinish();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
            }

            }else if(dnow.get(Calendar.DATE) > d1.get(Calendar.DATE)){
                Log.i(TAG,"d is big");
                //go to finish
                goToFinish();
            }

            Log.i(TAG,dateFormat_1.format(d));
            date_text.setText(dateFormat_1.format(d));


        }catch (Exception e){
            e.printStackTrace();
        }


        try {
            Date time = timeFormat.parse(end_time);
        }catch (Exception e){
            e.printStackTrace();
        }
        time_text.setText(parts[0]);

                //-----------------------------------------------------------------------------------------------


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),slot_book.class);
                intent.putExtra("change_slot",1);
                intent.putExtra("topic",one_one_class.getDesciption());
                intent.putExtra("time",one_one_class.getTime());
                startActivity(intent);
            }
        });






        LinearLayout linearLayout=view.findViewById(R.id.linear_link);
        TextView empty=view.findViewById(R.id.empty_link);
     if(url.equals(getString(R.string.empty))){

         linearLayout.setVisibility(View.GONE);

         empty.setVisibility(View.VISIBLE);
     }else {
         linearLayout.setVisibility(View.VISIBLE);
         empty.setVisibility(View.INVISIBLE);
         text_link.setText(Html.fromHtml("<a href=\"" + url + "\">Go to Link</a>"));
         TextView copy = view.findViewById(R.id.copy);
         clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

         copy.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.i("one_one", "onclick");
                 Toast.makeText(getActivity(), "Link Copied", Toast.LENGTH_SHORT).show();
                 ClipData clip = ClipData.newPlainText("XOOG Link", url);
                 clipboard.setPrimaryClip(clip);
             }
         });
     }

         text_link.setMovementMethod(LinkMovementMethod.getInstance());
   //  }
        return view;
    }
    public void size(TextView textView, int start, int end, String s, float size){
        SpannableString span = new SpannableString(s);
        span.setSpan(new RelativeSizeSpan(size),start,end,SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(span);
    }
    public void goToFinish(){
        Intent intent=new Intent(getActivity(),Task_finish.class);
        startActivity(intent);
        getActivity().finish();

    }
}


