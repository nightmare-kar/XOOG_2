package com.karrit.xoog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class AddressListAdapter extends ArrayAdapter<Address> {


    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView Address_title;
        TextView Address_full;

    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public AddressListAdapter(Context context, int resource, ArrayList<Address> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String address_title = getItem(position).getAddress_title();
        String address_full = getItem(position).getAddress_full();


        //Create the person object with the information
        Address address = new Address(address_title,address_full);


        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.Address_full = (TextView) convertView.findViewById(R.id.full);
            holder.Address_title = (TextView) convertView.findViewById(R.id.title);
            result = convertView;
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }




        holder.Address_title.setText(address.getAddress_title());
       holder.Address_full.setText(address.getAddress_full());


        return convertView;
    }
}


