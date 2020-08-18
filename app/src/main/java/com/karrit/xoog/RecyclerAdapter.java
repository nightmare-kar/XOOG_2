package com.karrit.xoog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.TimeZone;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private Context mContext;
    StorageReference storageReference;
    private ArrayList<shopItems> mData;
    account_details account_details;
    private String TAG="Recycle Adapter";
    shared share;
    int xcash;
    Intent intent;



    public RecyclerAdapter(Context mContext, ArrayList<shopItems> mData) {
        share=new shared(mContext);
        account_details=new account_details(mContext,share.getKid1_id());
        xcash=account_details.getXcash();
        Log.i(TAG,"xcash"+xcash);
        Log.i(TAG,"kid id "+share.getCurrent_kid());
        this.mContext = mContext;

        if((TimeZone.getDefault().getRawOffset())==(TimeZone.getTimeZone("Asia/Dubai").getRawOffset())){
            Log.i(TAG,"utc");
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("shop_UAE");

        }else {
            Log.i(TAG,"pther");
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("shop");

        }
       this.mData = mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_test, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_book_title.setText(mData.get(position).getTitle());

        Glide.with(mContext).load(storageReference.child(mData.get(position).getId() + ".jpg")).into(holder.img_book_thumbnail);
        Log.i("adapter", Integer.toString(mData.get(position).getId()));
        if(xcash<mData.get(position).getCredits()){
         holder.img_book_thumbnail.setAlpha(0.2f);
        }
        //  holder.img_book_thumbnail.setImageResource(mData.get(position).getThumbnail());
        holder.credits.setText("CREDITS:" + Integer.toString(mData.get(position).getCredits()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    intent = new Intent(mContext, Shop_single_item.class);

                    intent.putExtra("name", mData.get(position).getTitle());
                    intent.putExtra("description", mData.get(position).getDescription());
                    intent.putExtra("shop_id", mData.get(position).getId());
                    intent.putExtra("credits", mData.get(position).getCredits());
                    // start the activity
                    mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_book_title;
        ImageView img_book_thumbnail;
        TextView credits;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_book_title = (TextView) itemView.findViewById(R.id.book_title_id);
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.book_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
            credits = (TextView) itemView.findViewById(R.id.credits_bar);


        }

    }
}
