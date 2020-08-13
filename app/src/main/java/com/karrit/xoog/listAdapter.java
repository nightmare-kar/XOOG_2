package com.karrit.xoog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class listAdapter extends ArrayAdapter<String> {
    private Activity context;

   ArrayList<leaderBoard_class> arrayList;


    public listAdapter(Activity context, ArrayList<leaderBoard_class> arrayList, String[] name) {
        super(context, R.layout.activity_list_leaderboard, name);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.arrayList=arrayList;



    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_list_leaderboard, null,true);

        TextView rankText = (TextView) rowView.findViewById(R.id.rank);

        TextView nameText = (TextView) rowView.findViewById(R.id.name);

        TextView creditText=rowView.findViewById(R.id.credit);

      rankText.setText(arrayList.get(position).getRank());
      nameText.setText(arrayList.get(position).getName());

      creditText.setText(arrayList.get(position).getCredits());



        return rowView;

    }


}


