package com.karrit.xoog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class sql_address {
    private SQLiteDatabase sqLiteDatabase;
    Context context;
    private String TAG="Address";
    private String table="table_address";
    private String title_id="title_id";
    shared share;
    private String add_id="add_id";
    public sql_address(Context context){
        this.context=context;
        share=new shared(context);
        sqLiteDatabase = context.openOrCreateDatabase(share.getUser_id()+"address", MODE_PRIVATE, null);
    }

    public void addAdress(Address address){
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS '"+table+"' ('"+title_id+"' VARCHAR,'"+add_id+"' VARCHAR)");
        //   sqLiteDatabase.execSQL("INSERT INTO table_upload_photo VALUES('"+level+"','"+task+"','"+description+"','"+show_scramble+"')");
        ContentValues con= new ContentValues();
        con.put(title_id,address.getAddress_title());
        con.put(add_id,address.getAddress_full());
        long red=sqLiteDatabase.insert(table,null,con);
        Log.i(TAG,"Result"+red);
    }
    public ArrayList<Address> getAddresses(){
        ArrayList<Address> addresses=new ArrayList<>();
        try{
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM '"+table+"'", null);
            int titleIndex = c.getColumnIndex(title_id);
            int addIndex = c.getColumnIndex(add_id);

            c.moveToFirst();

            do {
                Log.i(TAG,"title"+ c.getString(titleIndex));
                Log.i(TAG,"addresses"+ c.getString(addIndex));
                Address address=new Address(c.getString(titleIndex),c.getString(addIndex));

              addresses.add(address);
            }while (c.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }
        return addresses;
    }

        public void delete(){
        shared share=new shared(context);
        Log.i(TAG,"delete address");
            context.deleteDatabase(share.getUser_id()+"address");
        }

}
