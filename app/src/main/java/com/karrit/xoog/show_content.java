package com.karrit.xoog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class show_content extends Fragment {
    View view;
    task_details task_details;
  show_content_class show_content_class;
    sql_rubik sql_rubik;
    shared share;
    String kid_id,user_id;
    int level,task;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.show_content_layout, container, false);
        share=new shared(getActivity());
        kid_id=share.getCurrent_kid();
        user_id=share.getUser_id();
        task_details=new task_details(getActivity(),kid_id,getString(R.string.rubik_type));
        ImageView close=view.findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        level=task_details.getCurrent_level();
        task=task_details.getCurrent_task();
        sql_rubik= new sql_rubik(getActivity(),kid_id);
       show_content_class=sql_rubik.get_show_content(level,task);

        TextView topic=view.findViewById(R.id.topic);
        topic.setText(show_content_class.getDesciption());
        Button done=view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),Task_finish.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;

    }
}
